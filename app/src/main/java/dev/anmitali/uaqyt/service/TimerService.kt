package dev.anmitali.uaqyt.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dev.anmitali.uaqyt.MainActivity
import dev.anmitali.uaqyt.R
import dev.anmitali.uaqyt.domain.model.TimerMode
import dev.anmitali.uaqyt.domain.model.TimerSession
import dev.anmitali.uaqyt.domain.repository.SettingsRepository
import dev.anmitali.uaqyt.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var durationObservationJob: Job? = null

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var countDownTimer: CountDownTimer? = null

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        observeCurrentModeDuration()
    }

    private fun observeCurrentModeDuration() {
        durationObservationJob?.cancel()
        durationObservationJob = serviceScope.launch {
            settingsRepository.getDuration(_timerState.value.session.currentMode).collect { duration ->
                if (!_timerState.value.isRunning) {
                    _timerState.value = _timerState.value.copy(
                        remainingTime = duration,
                        totalTime = duration
                    )
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESET -> resetTimer()
            ACTION_STOP_SERVICE -> stopSelf()
        }

        if (_timerState.value.isRunning) {
            startForeground(NOTIFICATION_ID, createNotification())
        }

        return START_STICKY
    }

    fun startTimer() {
        if (_timerState.value.isRunning) return

        val remaining = _timerState.value.remainingTime
        serviceScope.launch {
            val duration = settingsRepository.getDuration(_timerState.value.session.currentMode).first()
            if (_timerState.value.remainingTime == duration) {
                 _timerState.value = _timerState.value.copy(totalTime = duration)
            }
        }

        countDownTimer = object : CountDownTimer(remaining.inWholeMilliseconds, 100) {

            private var lastSecond = -1L

            override fun onTick(millisUntilFinished: Long) {
                val currentSecond = millisUntilFinished / 1000
                _timerState.value = _timerState.value.copy(
                    remainingTime = millisUntilFinished.milliseconds,
                    isRunning = true
                )

                if (currentSecond != lastSecond) {
                    lastSecond = currentSecond
                    updateNotification()
                }
            }

            override fun onFinish() {
                onTimerFinished()
            }
        }.start()

        startForeground(NOTIFICATION_ID, createNotification())
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
        stopForeground(STOP_FOREGROUND_DETACH)
        updateNotification()
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        serviceScope.launch {
            val duration = settingsRepository.getDuration(_timerState.value.session.currentMode).first()
            _timerState.value = _timerState.value.copy(
                remainingTime = duration,
                totalTime = duration,
                isRunning = false
            )
            updateNotification()
            observeCurrentModeDuration()
        }
    }

    fun setMode(mode: TimerMode) {
        countDownTimer?.cancel()
        _timerState.value = _timerState.value.copy(
            session = _timerState.value.session.copy(currentMode = mode),
            isRunning = false
        )
        observeCurrentModeDuration()
        updateNotification()
    }
    private fun onTimerFinished() {
        val finishedMode = _timerState.value.session.currentMode
        _timerState.value = _timerState.value.copy(isRunning = false, remainingTime = 0.seconds)

        serviceScope.launch {
            val nextSession = _timerState.value.session.next()
            val nextDuration = settingsRepository.getDuration(nextSession.currentMode).first()
            val autoStart = settingsRepository.isAutoStartNextEnabled().first()
            val notificationsEnabled = settingsRepository.isNotificationsEnabled().first()
            val soundEnabled = settingsRepository.isSoundEnabled().first()
            val vibrationEnabled = settingsRepository.isVibrationEnabled().first()

            _timerState.value = _timerState.value.copy(
                session = nextSession,
                remainingTime = nextDuration,
                totalTime = nextDuration
            )

            if (notificationsEnabled) {
                notificationHelper.showCompletionNotification(
                    mode = finishedMode,
                    soundEnabled = soundEnabled,
                    vibrationEnabled = vibrationEnabled
                )
            }

            if (autoStart) {
                startTimer()
            } else {
                stopForeground(STOP_FOREGROUND_DETACH)
                updateNotification()
            }
        }
    }

    private fun createNotification(): Notification {
        val state = _timerState.value
        return notificationHelper.getTimerNotification(
            mode = state.session.currentMode,
            remainingTime = formatDuration(state.remainingTime),
            isRunning = state.isRunning
        )
    }

    private fun updateNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun formatDuration(duration: Duration): String {
        val minutes = duration.inWholeMinutes
        val seconds = duration.inWholeSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        durationObservationJob?.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "timer_channel"

        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESET = "ACTION_RESET"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    }
}

data class TimerState(
    val session: TimerSession = TimerSession(),
    val remainingTime: Duration = TimerMode.WORK.defaultDuration,
    val totalTime: Duration = TimerMode.WORK.defaultDuration,
    val isRunning: Boolean = false
)
