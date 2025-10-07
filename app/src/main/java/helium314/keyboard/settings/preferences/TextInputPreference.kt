// SPDX-License-Identifier: GPL-3.0-only
package com.keyfluent.keyboard.settings.preferences

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.keyfluent.keyboard.keyboard.KeyboardSwitcher
import com.keyfluent.keyboard.latin.utils.prefs
import com.keyfluent.keyboard.settings.Setting
import com.keyfluent.keyboard.settings.dialogs.TextInputDialog

@Composable
fun TextInputPreference(setting: Setting, default: String, checkTextValid: (String) -> Boolean = { true }) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val prefs = LocalContext.current.prefs()
    Preference(
        name = setting.title,
        onClick = { showDialog = true },
        description = prefs.getString(setting.key, default)?.takeIf { it.isNotEmpty() }
    )
    if (showDialog) {
        TextInputDialog(
            onDismissRequest = { showDialog = false },
            onConfirmed = {
                prefs.edit().putString(setting.key, it).apply()
                KeyboardSwitcher.getInstance().setThemeNeedsReload()
            },
            initialText = prefs.getString(setting.key, default) ?: "",
            title = { Text(setting.title) },
            checkTextValid = checkTextValid
        )
    }
}
