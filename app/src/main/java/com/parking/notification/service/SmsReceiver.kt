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
        val t0 = System.currentTimeMillis()
        Timber.i("[TRACE] SMS_RECEIVER: onReceive on thread=%s, action=%s",
            Thread.currentThread().name, intent.action)

        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return

        val senderNumber = messages.first().originatingAddress ?: return
        val messageBody = messages.joinToString("") { it.messageBody ?: "" }

        Timber.i("[TRACE] SMS_RECEIVER: received from %s at +%dms: %s",
            senderNumber, System.currentTimeMillis() - t0, messageBody.take(80))

        // Process in background via coroutine
        CoroutineScope(Dispatchers.IO).launch {
            Timber.i("[TRACE] SMS_RECEIVER: launching smsProcessor.process() at +%dms",
                System.currentTimeMillis() - t0)
            smsProcessor.process(senderNumber, messageBody)
        }
    }
}
