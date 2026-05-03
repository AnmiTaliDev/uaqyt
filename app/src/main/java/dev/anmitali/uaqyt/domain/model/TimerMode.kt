package dev.anmitali.uaqyt.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

enum class TimerMode(val defaultDuration: Duration) {
    WORK(25.minutes),
    SHORT_BREAK(5.minutes),
    LONG_BREAK(15.minutes);

    fun next(): TimerMode = when (this) {
        WORK -> SHORT_BREAK
        SHORT_BREAK -> WORK
        LONG_BREAK -> WORK
    }
}
