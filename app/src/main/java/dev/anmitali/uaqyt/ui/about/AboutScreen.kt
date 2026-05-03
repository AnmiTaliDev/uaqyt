package dev.anmitali.uaqyt.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.anmitali.uaqyt.R

data class LicenseInfo(
    val name: String,
    val version: String,
    val license: String,
    val url: String
)

val dependencies = listOf(
    LicenseInfo("Jetpack Compose", "2024.10.01", "Apache-2.0", "https://developer.android.com/jetpack/compose"),
    LicenseInfo("Hilt", "2.52", "Apache-2.0", "https://dagger.dev/hilt/"),
    LicenseInfo("Kotlin Coroutines", "1.9.0", "Apache-2.0", "https://github.com/Kotlin/kotlinx.coroutines"),
    LicenseInfo("DataStore", "1.1.1", "Apache-2.0", "https://developer.android.com/topic/libraries/architecture/datastore"),
    LicenseInfo("Navigation Compose", "2.8.3", "Apache-2.0", "https://developer.android.com/jetpack/compose/navigation")
)

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(stringResource(R.string.about), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.about_text), style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.open_source_licenses), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(dependencies) { dep ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "${dep.name} (${dep.version})", style = MaterialTheme.typography.titleMedium)
                    Text(text = "License: ${dep.license}", style = MaterialTheme.typography.bodySmall)
                    Text(text = dep.url, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
