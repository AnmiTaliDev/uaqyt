package dev.anmitali.uaqyt.domain.model

data class TimerSession(
    val currentMode: TimerMode = TimerMode.WORK,
    val sessionCount: Int = 1,
    val totalSessionsBeforeLongBreak: Int = 4
) {
    fun next(): TimerSession {
        val nextMode = if (currentMode != TimerMode.WORK) {
            TimerMode.WORK
        } else {
            if (sessionCount % totalSessionsBeforeLongBreak == 0) {
                TimerMode.LONG_BREAK
            } else {
                TimerMode.SHORT_BREAK
            }
        }

        val nextCount = if (currentMode != TimerMode.WORK && nextMode == TimerMode.WORK) {
            sessionCount + 1
        } else {
            sessionCount
        }

        return copy(currentMode = nextMode, sessionCount = nextCount)
    }

}
