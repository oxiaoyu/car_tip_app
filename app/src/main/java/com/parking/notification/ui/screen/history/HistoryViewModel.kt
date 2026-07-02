package com.parking.notification.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parking.notification.data.entity.NotificationHistoryEntity
import com.parking.notification.domain.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {

    init {
        Timber.i("[TRACE] VM_CREATE: HistoryViewModel initialized on thread=%s",
            Thread.currentThread().name)
    }

    val historyItems: StateFlow<List<NotificationHistoryEntity>> =
        getHistoryUseCase.getAllFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteAll() {
        viewModelScope.launch {
            getHistoryUseCase.deleteAll()
        }
    }
}
