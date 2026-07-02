package com.parking.notification.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parking.notification.service.alert.AlertManager
import com.parking.notification.ui.theme.Background
import com.parking.notification.ui.theme.Primary
import com.parking.notification.ui.theme.TextPrimary
import com.parking.notification.ui.theme.TextSecondary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlertFullScreenActivity : ComponentActivity() {

    @Inject
    lateinit var alertManager: AlertManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val t0 = System.currentTimeMillis()
        super.onCreate(savedInstanceState)
        Timber.i("[TRACE] ALERT_FS: AlertFullScreenActivity.onCreate() on thread=%s", Thread.currentThread().name)

        val historyId = intent.getLongExtra(AlertManager.EXTRA_HISTORY_ID, -1L)
        val sender = intent.getStringExtra(AlertManager.EXTRA_SENDER) ?: "未知号码"
        val message = intent.getStringExtra(AlertManager.EXTRA_MESSAGE) ?: ""
        val itemName = intent.getStringExtra(AlertManager.EXTRA_ITEM_NAME) ?: "挪车通知"

        Timber.i("[TRACE] ALERT_FS: intent extras loaded at +%dms, historyId=%d, sender=%s",
            System.currentTimeMillis() - t0, historyId, sender)

        setContent {
            AlertFullScreen(
                itemName = itemName,
                senderNumber = sender,
                messageContent = message,
                onDismiss = {
                    if (historyId != -1L) {
                        alertManager.dismissAlert(historyId)
                    }
                    finish()
                }
            )
        }
    }
}

@Composable
private fun AlertFullScreen(
    itemName: String,
    senderNumber: String,
    messageContent: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(32.dp)
    ) {
        // Dismiss button top-right
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "关闭",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Alert icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Item name
            Text(
                text = itemName,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Sender info
            Text(
                text = "来自: $senderNumber",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            // Message content
            if (messageContent.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = messageContent,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(Modifier.height(48.dp))

            // Dismiss button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                )
            ) {
                Text("关闭提醒", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
