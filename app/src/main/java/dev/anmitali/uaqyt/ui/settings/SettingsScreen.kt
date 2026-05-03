package dev.anmitali.uaqyt.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.anmitali.uaqyt.R
import dev.anmitali.uaqyt.domain.model.TimerMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val workDuration by viewModel.workDuration.collectAsState()
    val shortBreakDuration by viewModel.shortBreakDuration.collectAsState()
    val longBreakDuration by viewModel.longBreakDuration.collectAsState()
    val autoStartNext by viewModel.autoStartNext.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(stringResource(R.string.duration_settings), style = MaterialTheme.typography.titleLarge)

        DurationSlider(
            label = stringResource(R.string.work),
            value = workDuration.inWholeMinutes.toInt(),
            onValueChange = { viewModel.setDuration(TimerMode.WORK, it) },
            range = 1f..60f
        )

        DurationSlider(
            label = stringResource(R.string.short_break),
            value = shortBreakDuration.inWholeMinutes.toInt(),
            onValueChange = { viewModel.setDuration(TimerMode.SHORT_BREAK, it) },
            range = 1f..30f
        )

        DurationSlider(
            label = stringResource(R.string.long_break),
            value = longBreakDuration.inWholeMinutes.toInt(),
            onValueChange = { viewModel.setDuration(TimerMode.LONG_BREAK, it) },
            range = 1f..45f
        )

        HorizontalDivider()

        SettingSwitch(
            label = stringResource(R.string.auto_start_next),
            checked = autoStartNext,
            onCheckedChange = viewModel::setAutoStartNext
        )

        HorizontalDivider()

        Text(stringResource(R.string.notifications), style = MaterialTheme.typography.titleLarge)

        SettingSwitch(
            label = stringResource(R.string.enable_notifications),
            checked = notificationsEnabled,
            onCheckedChange = viewModel::setNotificationsEnabled
        )

        SettingSwitch(
            label = stringResource(R.string.vibration),
            checked = vibrationEnabled,
            onCheckedChange = viewModel::setVibrationEnabled,
            enabled = notificationsEnabled
        )

        SettingSwitch(
            label = stringResource(R.string.sound),
            checked = soundEnabled,
            onCheckedChange = viewModel::setSoundEnabled,
            enabled = notificationsEnabled
        )
    }
}

@Composable
fun DurationSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.minutes, value), style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range,
            steps = range.endInclusive.toInt() - range.start.toInt() - 1
        )
    }
}

@Composable
fun SettingSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}
