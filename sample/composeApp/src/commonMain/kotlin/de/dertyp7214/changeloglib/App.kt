package de.dertyp7214.changeloglib

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.dertyp7214.changeloglib.theme.AppTheme
import de.dertyp7214.changelogs.Change
import de.dertyp7214.changelogs.ChangeType
import de.dertyp7214.changelogs.ChangelogConfig
import de.dertyp7214.changelogs.Changelogs

@Composable
internal fun App() = AppTheme {
    val open = remember { mutableStateOf(false) }
    val config = remember {
        ChangelogConfig()
    }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = { open.value = true }) {
            Text(text = "Open Changelog")
        }
        Changelogs(
            open = open,
            config = config,
            onReady = {
                open.value = true
            },
            onClose = {
                open.value = false
            },
            onOpenUrl = {
                openUrl(it)
            }
        ) {
            version(
                versionName = "1.0.0",
                versionCode = 1
            ) {
                Change(
                    type = ChangeType.ADDED,
                    change = "Added something with a link",
                    url = "https://google.com"
                )
                Change(
                    type = ChangeType.REMOVED,
                    change = "Removed something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Fixed something"
                )
                Change(
                    type = ChangeType.IMPROVED,
                    change = "Improved something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Improved security"
                )
            }
            version(
                versionName = "1.0.0",
                versionCode = 1
            ) {
                Change(
                    type = ChangeType.ADDED,
                    change = "Added something"
                )
                Change(
                    type = ChangeType.REMOVED,
                    change = "Removed something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Fixed something"
                )
                Change(
                    type = ChangeType.IMPROVED,
                    change = "Improved something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Improved security"
                )
            }
            version(
                versionName = "1.0.0",
                versionCode = 1
            ) {
                Change(
                    type = ChangeType.ADDED,
                    change = "Added something"
                )
                Change(
                    type = ChangeType.REMOVED,
                    change = "Removed something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Fixed something"
                )
                Change(
                    type = ChangeType.IMPROVED,
                    change = "Improved something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Improved security"
                )
            }
            version(
                versionName = "1.0.0",
                versionCode = 1
            ) {
                Change(
                    type = ChangeType.ADDED,
                    change = "Added something"
                )
                Change(
                    type = ChangeType.REMOVED,
                    change = "Removed something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Fixed something"
                )
                Change(
                    type = ChangeType.IMPROVED,
                    change = "Improved something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Improved security"
                )
            }
            version(
                versionName = "1.0.0",
                versionCode = 1
            ) {
                Change(
                    type = ChangeType.ADDED,
                    change = "Added something"
                )
                Change(
                    type = ChangeType.REMOVED,
                    change = "Removed something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Fixed something"
                )
                Change(
                    type = ChangeType.IMPROVED,
                    change = "Improved something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Improved security"
                )
            }
            version(
                versionName = "1.0.0",
                versionCode = 1
            ) {
                Change(
                    type = ChangeType.ADDED,
                    change = "Added something"
                )
                Change(
                    type = ChangeType.REMOVED,
                    change = "Removed something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Fixed something"
                )
                Change(
                    type = ChangeType.IMPROVED,
                    change = "Improved something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Improved security"
                )
            }
            version(
                versionName = "1.0.0",
                versionCode = 1
            ) {
                Change(
                    type = ChangeType.ADDED,
                    change = "Added something"
                )
                Change(
                    type = ChangeType.REMOVED,
                    change = "Removed something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Fixed something"
                )
                Change(
                    type = ChangeType.IMPROVED,
                    change = "Improved something"
                )
                Change(
                    type = ChangeType.FIXED,
                    change = "Improved security"
                )
            }
        }
    }
}

internal expect fun openUrl(url: String?)