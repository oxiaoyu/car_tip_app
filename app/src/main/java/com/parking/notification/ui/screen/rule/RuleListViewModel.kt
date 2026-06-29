package com.parking.notification.ui.screen.rule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parking.notification.data.entity.TriggerRuleEntity
import com.parking.notification.domain.usecase.ManageRuleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RuleListViewModel @Inject constructor(
    private val manageRuleUseCase: ManageRuleUseCase
) : ViewModel() {

    val rules: StateFlow<List<TriggerRuleEntity>> =
        manageRuleUseCase.getAllFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun create(phoneKeyword: String, contentKeyword: String) {
        viewModelScope.launch {
            manageRuleUseCase.create(phoneKeyword, contentKeyword)
        }
    }

    fun delete(rule: TriggerRuleEntity) {
        viewModelScope.launch {
            manageRuleUseCase.delete(rule)
        }
    }
}
