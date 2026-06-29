package com.parking.notification.ui.screen.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.parking.notification.ui.theme.Primary
import com.parking.notification.ui.theme.TextPrimary
import com.parking.notification.ui.theme.TextSecondary

@Composable
fun NotificationEditDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    initialName: String = ""
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                if (initialName.isEmpty()) "添加通知项" else "编辑通知项",
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text("为通知项命名", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("例如：挪车提醒") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        cursorColor = Primary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.trim()) },
                enabled = name.trim().isNotEmpty(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TextSecondary.copy(alpha = 0.2f))
            ) {
                Text("取消", color = TextPrimary)
            }
        }
    )
}
