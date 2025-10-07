// SPDX-License-Identifier: GPL-3.0-only
package com.keyfluent.keyboard.settings

import android.content.Context
import com.keyfluent.keyboard.keyboard.internal.KeyboardIconsSet
import com.keyfluent.keyboard.latin.settings.Settings
import com.keyfluent.keyboard.latin.utils.SubtypeSettings

// file is meant for making compose previews work

fun initPreview(context: Context) {
    Settings.init(context)
    SubtypeSettings.init(context)
    SettingsActivity.settingsContainer = SettingsContainer(context)
    KeyboardIconsSet.instance.loadIcons(context)
}

const val previewDark = true
