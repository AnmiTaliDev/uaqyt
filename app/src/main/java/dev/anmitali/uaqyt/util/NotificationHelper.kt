package dev.anmitali.uaqyt.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.anmitali.uaqyt.MainActivity
import dev.anmitali.uaqyt.R
import dev.anmitali.uaqyt.domain.model.TimerMode
import dev.anmitali.uaqyt.service.TimerService

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Pomodoro timer events"
                enableVibration(true)
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getModeName(mode: TimerMode): String {
        return context.getString(
            when (mode) {
                TimerMode.WORK -> R.string.work
                TimerMode.SHORT_BREAK -> R.string.short_break
                TimerMode.LONG_BREAK -> R.string.long_break
            }
        )
    }

    fun getTimerNotification(
        mode: TimerMode,
        remainingTime: String,
        isRunning: Boolean
    ): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Uaqyt - ${getModeName(mode)}")
            .setContentText(remainingTime)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(isRunning)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        if (isRunning) {
            val pauseIntent = Intent(context, TimerService::class.java).apply { action = TimerService.ACTION_PAUSE }
            val pausePendingIntent = PendingIntent.getService(context, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
            builder.addAction(0, context.getString(R.string.pause), pausePendingIntent)
        } else {
            val startIntent = Intent(context, TimerService::class.java).apply { action = TimerService.ACTION_START }
            val startPendingIntent = PendingIntent.getService(context, 2, startIntent, PendingIntent.FLAG_IMMUTABLE)
            builder.addAction(0, context.getString(R.string.start), startPendingIntent)
        }

        return builder.build()
    }

    fun showCompletionNotification(
        mode: TimerMode,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val nextMode = mode.next()
        val nextActionRes = if (nextMode == TimerMode.WORK) R.string.notification_start_work else R.string.notification_start_break
        val nextActionText = context.getString(nextActionRes)
        val nextIntent = Intent(context, TimerService::class.java).apply { action = TimerService.ACTION_START }
        val nextPendingIntent = PendingIntent.getService(context, 3, nextIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_session_finished))
            .setContentText(
                context.getString(
                    R.string.notification_session_over,
                    getModeName(mode),
                    getModeName(nextMode)
                )
            )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, nextActionText, nextPendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (soundEnabled) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        } else {
            builder.setSound(null)
        }

        if (vibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 500, 250, 500))
        } else {
            builder.setVibrate(null)
        }

        if (!soundEnabled && !vibrationEnabled) {
            builder.setSilent(true)
        }

        notificationManager.notify(COMPLETION_ID, builder.build())
    }

    companion object {
        const val CHANNEL_ID = "timer_notifications_v2"
        const val COMPLETION_ID = 2
    }
}
