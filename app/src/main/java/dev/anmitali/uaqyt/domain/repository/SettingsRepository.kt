package dev.anmitali.uaqyt.domain.repository

import dev.anmitali.uaqyt.domain.model.TimerMode
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface SettingsRepository {
    fun getDuration(mode: TimerMode): Flow<Duration>
    suspend fun setDuration(mode: TimerMode, duration: Duration)

    fun isAutoStartNextEnabled(): Flow<Boolean>
    suspend fun setAutoStartNext(enabled: Boolean)

    fun isNotificationsEnabled(): Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)

    fun isVibrationEnabled(): Flow<Boolean>
    suspend fun setVibrationEnabled(enabled: Boolean)

    fun isSoundEnabled(): Flow<Boolean>
    suspend fun setSoundEnabled(enabled: Boolean)
}
