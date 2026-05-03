package dev.anmitali.uaqyt.ui.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.anmitali.uaqyt.R
import dev.anmitali.uaqyt.domain.model.TimerMode
import dev.anmitali.uaqyt.service.TimerService
import dev.anmitali.uaqyt.service.TimerState
import kotlin.time.Duration

@Composable
fun TimerScreen(
    timerService: TimerService?,
    modifier: Modifier = Modifier
) {
    val state by timerService?.timerState?.collectAsState() ?: remember { mutableStateOf(TimerState()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (state.session.currentMode) {
                TimerMode.WORK -> stringResource(R.string.work)
                TimerMode.SHORT_BREAK -> stringResource(R.string.short_break)
                TimerMode.LONG_BREAK -> stringResource(R.string.long_break)
            },
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(
                R.string.session_count,
                state.session.sessionCount,
                state.session.totalSessionsBeforeLongBreak
            ),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        TimerProgress(
            remainingTime = state.remainingTime,
            totalTime = state.totalTime,
            modifier = Modifier.size(280.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { timerService?.resetTimer() },
                colors = ButtonDefaults.filledTonalButtonColors()
            ) {
                Text(stringResource(R.string.reset))
            }

            Button(
                onClick = {
                    if (state.isRunning) timerService?.pauseTimer() else timerService?.startTimer()
                },
                modifier = Modifier.width(120.dp)
            ) {
                Text(if (state.isRunning) stringResource(R.string.pause) else stringResource(R.string.start))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val modes = listOf(TimerMode.WORK, TimerMode.SHORT_BREAK, TimerMode.LONG_BREAK)
            modes.forEach { mode ->
                TimerModeButton(
                    mode = mode,
                    currentMode = state.session.currentMode,
                    onClick = { timerService?.setMode(mode) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerModeButton(
    mode: TimerMode,
    currentMode: TimerMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = when (mode) {
        TimerMode.WORK -> stringResource(R.string.work)
        TimerMode.SHORT_BREAK -> stringResource(R.string.short_break)
        TimerMode.LONG_BREAK -> stringResource(R.string.long_break)
    }

    FilterChip(
        selected = mode == currentMode,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        },
        modifier = modifier
    )
}

@Composable
fun TimerProgress(
    remainingTime: Duration,
    totalTime: Duration,
    modifier: Modifier = Modifier
) {
    val progress = (1f - (remainingTime.inWholeMilliseconds.toFloat() / totalTime.inWholeMilliseconds.toFloat()))
        .coerceIn(0f, 1f)
    val color = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = trackColor,
                style = Stroke(width = 12.dp.toPx())
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            text = formatDuration(remainingTime),
            style = MaterialTheme.typography.displayLarge
        )
    }
}

private fun formatDuration(duration: Duration): String {
    val minutes = duration.inWholeMinutes
    val seconds = duration.inWholeSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
