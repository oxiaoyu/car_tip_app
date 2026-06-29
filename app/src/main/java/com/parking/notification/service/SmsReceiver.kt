package com.parking.notification.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var smsProcessor: SmsProcessor

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return

        val senderNumber = messages.first().originatingAddress ?: return
        val messageBody = messages.joinToString("") { it.messageBody ?: "" }

        Timber.d("SMS received from $senderNumber: ${messageBody.take(80)}")

        // Process in background via coroutine
        CoroutineScope(Dispatchers.IO).launch {
            smsProcessor.process(senderNumber, messageBody)
        }
    }
}
