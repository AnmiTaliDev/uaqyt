package dev.anmitali.uaqyt.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.anmitali.uaqyt.domain.model.TimerMode
import dev.anmitali.uaqyt.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val workDuration = settingsRepository.getDuration(TimerMode.WORK)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimerMode.WORK.defaultDuration)

    val shortBreakDuration = settingsRepository.getDuration(TimerMode.SHORT_BREAK)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimerMode.SHORT_BREAK.defaultDuration)

    val longBreakDuration = settingsRepository.getDuration(TimerMode.LONG_BREAK)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimerMode.LONG_BREAK.defaultDuration)

    val autoStartNext = settingsRepository.isAutoStartNextEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notificationsEnabled = settingsRepository.isNotificationsEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val vibrationEnabled = settingsRepository.isVibrationEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val soundEnabled = settingsRepository.isSoundEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setDuration(mode: TimerMode, minutes: Int) {
        viewModelScope.launch {
            settingsRepository.setDuration(mode, minutes.minutes)
        }
    }

    fun setAutoStartNext(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoStartNext(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setVibrationEnabled(enabled)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSoundEnabled(enabled)
        }
    }
}
