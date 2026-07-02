package com.parking.notification.ui.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    // Settings will be managed via DataStore in a real implementation

    init {
        Timber.i("[TRACE] VM_CREATE: SettingsViewModel initialized on thread=%s",
            Thread.currentThread().name)
    }
}
