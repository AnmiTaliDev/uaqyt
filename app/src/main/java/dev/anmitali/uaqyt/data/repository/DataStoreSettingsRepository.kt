package dev.anmitali.uaqyt.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.anmitali.uaqyt.domain.model.TimerMode
import dev.anmitali.uaqyt.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreSettingsRepository(private val context: Context) : SettingsRepository {

    private object PreferencesKeys {
        val WORK_DURATION = longPreferencesKey("work_duration")
        val SHORT_BREAK_DURATION = longPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION = longPreferencesKey("long_break_duration")
        val AUTO_START_NEXT = booleanPreferencesKey("auto_start_next")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    }

    override fun getDuration(mode: TimerMode): Flow<Duration> = context.dataStore.data.map { prefs ->
        val key = when (mode) {
            TimerMode.WORK -> PreferencesKeys.WORK_DURATION
            TimerMode.SHORT_BREAK -> PreferencesKeys.SHORT_BREAK_DURATION
            TimerMode.LONG_BREAK -> PreferencesKeys.LONG_BREAK_DURATION
        }
        prefs[key]?.milliseconds ?: mode.defaultDuration
    }

    override suspend fun setDuration(mode: TimerMode, duration: Duration) {
        context.dataStore.edit { prefs ->
            val key = when (mode) {
                TimerMode.WORK -> PreferencesKeys.WORK_DURATION
                TimerMode.SHORT_BREAK -> PreferencesKeys.SHORT_BREAK_DURATION
                TimerMode.LONG_BREAK -> PreferencesKeys.LONG_BREAK_DURATION
            }
            prefs[key] = duration.inWholeMilliseconds
        }
    }

    override fun isAutoStartNextEnabled(): Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.AUTO_START_NEXT] ?: true }
    override suspend fun setAutoStartNext(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.AUTO_START_NEXT] = enabled } }

    override fun isNotificationsEnabled(): Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true }
    override suspend fun setNotificationsEnabled(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled } }

    override fun isVibrationEnabled(): Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.VIBRATION_ENABLED] ?: true }
    override suspend fun setVibrationEnabled(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.VIBRATION_ENABLED] = enabled } }

    override fun isSoundEnabled(): Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.SOUND_ENABLED] ?: true }
    override suspend fun setSoundEnabled(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.SOUND_ENABLED] = enabled } }
}
