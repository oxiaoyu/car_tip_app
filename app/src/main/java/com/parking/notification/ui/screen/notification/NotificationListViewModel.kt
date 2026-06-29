package com.parking.notification.ui.screen.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parking.notification.data.entity.NotificationItemEntity
import com.parking.notification.domain.usecase.ManageNotificationItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(
    private val manageNotificationItemUseCase: ManageNotificationItemUseCase
) : ViewModel() {

    val items: StateFlow<List<NotificationItemEntity>> =
        manageNotificationItemUseCase.getAllFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleEnabled(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            manageNotificationItemUseCase.toggleEnabled(id, enabled)
        }
    }

    fun delete(item: NotificationItemEntity) {
        viewModelScope.launch {
            manageNotificationItemUseCase.delete(item)
        }
    }

    fun create(name: String) {
        viewModelScope.launch {
            manageNotificationItemUseCase.create(name)
        }
    }
}
