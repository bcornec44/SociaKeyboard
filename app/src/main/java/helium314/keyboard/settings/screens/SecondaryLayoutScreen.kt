// SPDX-License-Identifier: GPL-3.0-only
package com.keyfluent.keyboard.settings.screens

import android.content.Context
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.keyfluent.keyboard.latin.R
import com.keyfluent.keyboard.latin.settings.Settings
import com.keyfluent.keyboard.latin.utils.LayoutType
import com.keyfluent.keyboard.latin.utils.LayoutType.Companion.displayNameId
import com.keyfluent.keyboard.latin.utils.LayoutUtilsCustom
import com.keyfluent.keyboard.latin.utils.Log
import com.keyfluent.keyboard.latin.utils.getActivity
import com.keyfluent.keyboard.latin.utils.getStringResourceOrName
import com.keyfluent.keyboard.latin.utils.prefs
import com.keyfluent.keyboard.settings.SearchSettingsScreen
import com.keyfluent.keyboard.settings.Setting
import com.keyfluent.keyboard.settings.SettingsActivity
import com.keyfluent.keyboard.settings.Theme
import com.keyfluent.keyboard.settings.dialogs.LayoutPickerDialog
import com.keyfluent.keyboard.settings.initPreview
import com.keyfluent.keyboard.settings.preferences.Preference
import com.keyfluent.keyboard.settings.previewDark

@Composable
fun SecondaryLayoutScreen(
    onClickBack: () -> Unit,
) {
    // no main layouts in here
    // could be added later, but need to decide how to do it (showing all main layouts is too much)
    SearchSettingsScreen(
        onClickBack = onClickBack,
        title = stringResource(R.string.settings_screen_secondary_layouts),
        settings = LayoutType.entries.filter { it != LayoutType.MAIN }.map { Settings.PREF_LAYOUT_PREFIX + it.name }
    )
}

fun createLayoutSettings(context: Context) = LayoutType.entries.filter { it != LayoutType.MAIN }.map { layoutType ->
    Setting(context, Settings.PREF_LAYOUT_PREFIX + layoutType, layoutType.displayNameId) { setting ->
        val ctx = LocalContext.current
        val prefs = ctx.prefs()
        val b = (ctx.getActivity() as? SettingsActivity)?.prefChanged?.collectAsState()
        if ((b?.value ?: 0) < 0)
            Log.v("irrelevant", "stupid way to trigger recomposition on preference change")
        var showDialog by rememberSaveable { mutableStateOf(false) }
        val currentLayout = Settings.readDefaultLayoutName(layoutType, prefs)
        val displayName = if (LayoutUtilsCustom.isCustomLayout(currentLayout)) LayoutUtilsCustom.getDisplayName(currentLayout)
            else currentLayout.getStringResourceOrName("layout_", ctx)
        Preference(
            name = setting.title,
            description = displayName,
            onClick = { showDialog = true }
        )
        if (showDialog)
            LayoutPickerDialog(
                onDismissRequest = { showDialog = false },
                setting = setting,
                layoutType = layoutType
            )
    }
}

@Preview
@Composable
private fun Preview() {
    initPreview(LocalContext.current)
    Theme(previewDark) {
        Surface {
            SecondaryLayoutScreen { }
        }
    }
}
