package com.parking.notification.ui.screen.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parking.notification.data.entity.NotificationItemEntity
import com.parking.notification.ui.theme.ActiveIndicator
import com.parking.notification.ui.theme.Background
import com.parking.notification.ui.theme.Primary
import com.parking.notification.ui.theme.SwitchTrackActive
import com.parking.notification.ui.theme.TextPrimary
import com.parking.notification.ui.theme.TextSecondary
import com.parking.notification.ui.theme.TopBarBackground

@Composable
fun NotificationListScreen(
    viewModel: NotificationListViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("通知项", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text(
                            "管理需要响应的短信通知",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加通知项")
            }
        },
        containerColor = Background
    ) { padding ->
        if (items.isEmpty()) {
            EmptyNotificationList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }
                items(items, key = { it.id }) { item ->
                    NotificationItemCard(
                        item = item,
                        onToggle = { viewModel.toggleEnabled(item.id, it) },
                        onDelete = { viewModel.delete(item) },
                        onEdit = { /* open edit dialog */ }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showCreateDialog) {
        NotificationEditDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                viewModel.create(name)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun NotificationItemCard(
    item: NotificationItemEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (item.enabled) Primary.copy(alpha = 0.1f) else Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (item.enabled) Primary else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Text(
                    text = if (item.enableVibration) "震动开" else "震动关",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Switch(
                checked = item.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = SwitchTrackActive,
                    checkedThumbColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun EmptyNotificationList(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("暂无通知项", color = TextSecondary, fontWeight = FontWeight.Medium)
            Text("点击右下角 + 添加", color = TextSecondary, fontSize = 13.sp)
        }
    }
}
