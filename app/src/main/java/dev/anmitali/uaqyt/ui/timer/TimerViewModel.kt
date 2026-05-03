package dev.anmitali.uaqyt.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.anmitali.uaqyt.domain.model.TimerMode
import dev.anmitali.uaqyt.domain.repository.SettingsRepository
import dev.anmitali.uaqyt.service.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    // Logic for interacting with TimerService will be in TimerScreen via service binding
    // But we might need some initial settings here
}
