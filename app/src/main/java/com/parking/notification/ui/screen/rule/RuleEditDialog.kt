package com.parking.notification.ui.screen.rule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.sp
import com.parking.notification.ui.theme.Primary
import com.parking.notification.ui.theme.TextPrimary
import com.parking.notification.ui.theme.TextSecondary

@Composable
fun RuleEditDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var phoneKeyword by remember { mutableStateOf("") }
    var contentKeyword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = { Text("添加规则", color = TextPrimary) },
        text = {
            Column {
                OutlinedTextField(
                    value = phoneKeyword,
                    onValueChange = { phoneKeyword = it },
                    label = { Text("发件人关键词（选填）") },
                    placeholder = { Text("例如：1069, 10086") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        cursorColor = Primary
                    )
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = contentKeyword,
                    onValueChange = { contentKeyword = it },
                    label = { Text("内容关键词（选填）") },
                    placeholder = { Text("例如：挪车、移车") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        cursorColor = Primary
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "留空表示匹配任意，填关键词则模糊匹配",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(phoneKeyword.trim(), contentKeyword.trim())
                },
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
