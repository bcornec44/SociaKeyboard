// SPDX-License-Identifier: GPL-3.0-only
package com.keyfluent.keyboard.settings.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.keyfluent.keyboard.keyboard.KeyboardActionListener
import com.keyfluent.keyboard.keyboard.KeyboardLayoutSet
import com.keyfluent.keyboard.keyboard.KeyboardSwitcher
import com.keyfluent.keyboard.keyboard.internal.keyboard_parser.POPUP_KEYS_ALL
import com.keyfluent.keyboard.keyboard.internal.keyboard_parser.POPUP_KEYS_MAIN
import com.keyfluent.keyboard.keyboard.internal.keyboard_parser.POPUP_KEYS_MORE
import com.keyfluent.keyboard.keyboard.internal.keyboard_parser.POPUP_KEYS_NORMAL
import com.keyfluent.keyboard.keyboard.internal.keyboard_parser.morePopupKeysResId
import com.keyfluent.keyboard.latin.BuildConfig
import com.keyfluent.keyboard.latin.R
import com.keyfluent.keyboard.latin.SystemBroadcastReceiver
import com.keyfluent.keyboard.latin.common.splitOnWhitespace
import com.keyfluent.keyboard.latin.settings.DebugSettings
import com.keyfluent.keyboard.latin.settings.Defaults
import com.keyfluent.keyboard.latin.settings.Settings
import com.keyfluent.keyboard.latin.utils.checkTimestampFormat
import com.keyfluent.keyboard.latin.utils.prefs
import com.keyfluent.keyboard.settings.NextScreenIcon
import com.keyfluent.keyboard.settings.SettingsContainer
import com.keyfluent.keyboard.settings.preferences.ListPreference
import com.keyfluent.keyboard.settings.SettingsWithoutKey
import com.keyfluent.keyboard.settings.Setting
import com.keyfluent.keyboard.settings.preferences.Preference
import com.keyfluent.keyboard.settings.SearchSettingsScreen
import com.keyfluent.keyboard.settings.SettingsActivity
import com.keyfluent.keyboard.settings.SettingsDestination
import com.keyfluent.keyboard.settings.preferences.SliderPreference
import com.keyfluent.keyboard.settings.preferences.SwitchPreference
import com.keyfluent.keyboard.settings.Theme
import com.keyfluent.keyboard.settings.dialogs.TextInputDialog
import com.keyfluent.keyboard.settings.preferences.BackupRestorePreference
import com.keyfluent.keyboard.settings.preferences.LoadGestureLibPreference
import com.keyfluent.keyboard.settings.preferences.TextInputPreference
import com.keyfluent.keyboard.settings.previewDark

@Composable
fun AdvancedSettingsScreen(
    onClickBack: () -> Unit,
) {
    val prefs = LocalContext.current.prefs()
    val items = listOf(
        Settings.PREF_ALWAYS_INCOGNITO_MODE,
        Settings.PREF_KEY_LONGPRESS_TIMEOUT,
        Settings.PREF_SPACE_HORIZONTAL_SWIPE,
        Settings.PREF_SPACE_VERTICAL_SWIPE,
        if (Settings.readHorizontalSpaceSwipe(prefs) == KeyboardActionListener.SWIPE_SWITCH_LANGUAGE
            || Settings.readVerticalSpaceSwipe(prefs) == KeyboardActionListener.SWIPE_SWITCH_LANGUAGE)
            Settings.PREF_LANGUAGE_SWIPE_DISTANCE else null,
        Settings.PREF_DELETE_SWIPE,
        Settings.PREF_SPACE_TO_CHANGE_LANG,
        Settings.PREFS_LONG_PRESS_SYMBOLS_FOR_NUMPAD,
        Settings.PREF_ENABLE_EMOJI_ALT_PHYSICAL_KEY,
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) Settings.PREF_SHOW_SETUP_WIZARD_ICON else null,
        Settings.PREF_ABC_AFTER_SYMBOL_SPACE,
        Settings.PREF_ABC_AFTER_NUMPAD_SPACE,
        Settings.PREF_ABC_AFTER_EMOJI,
        Settings.PREF_ABC_AFTER_CLIP,
        Settings.PREF_CUSTOM_CURRENCY_KEY,
        Settings.PREF_MORE_POPUP_KEYS,
        Settings.PREF_TIMESTAMP_FORMAT,
        SettingsWithoutKey.BACKUP_RESTORE,
        if (BuildConfig.DEBUG || prefs.getBoolean(DebugSettings.PREF_SHOW_DEBUG_SETTINGS, Defaults.PREF_SHOW_DEBUG_SETTINGS))
            SettingsWithoutKey.DEBUG_SETTINGS else null,
        R.string.settings_category_experimental,
        Settings.PREF_EMOJI_MAX_SDK,
        Settings.PREF_URL_DETECTION,
        if (BuildConfig.BUILD_TYPE != "nouserlib") SettingsWithoutKey.LOAD_GESTURE_LIB else null
    )
    SearchSettingsScreen(
        onClickBack = onClickBack,
        title = stringResource(R.string.settings_screen_advanced),
        settings = items
    )
}

@SuppressLint("ApplySharedPref")
fun createAdvancedSettings(context: Context) = listOf(
    Setting(context, Settings.PREF_ALWAYS_INCOGNITO_MODE,
        R.string.incognito, R.string.prefs_force_incognito_mode_summary)
    {
        SwitchPreference(it, Defaults.PREF_ALWAYS_INCOGNITO_MODE) { KeyboardSwitcher.getInstance().setThemeNeedsReload() }
    },
    Setting(context, Settings.PREF_KEY_LONGPRESS_TIMEOUT, R.string.prefs_key_longpress_timeout_settings) { setting ->
        SliderPreference(
            name = setting.title,
            key = setting.key,
            default = Defaults.PREF_KEY_LONGPRESS_TIMEOUT,
            range = 100f..700f,
            description = { stringResource(R.string.abbreviation_unit_milliseconds, it.toString()) }
        )
    },
    Setting(context, Settings.PREF_SPACE_HORIZONTAL_SWIPE, R.string.show_horizontal_space_swipe) {
        val items = listOf(
            stringResource(R.string.space_swipe_move_cursor_entry) to "move_cursor",
            stringResource(R.string.switch_language) to "switch_language",
            stringResource(R.string.space_swipe_toggle_numpad_entry) to "toggle_numpad",
            stringResource(R.string.action_none) to "none",
        )
        ListPreference(it, items, Defaults.PREF_SPACE_HORIZONTAL_SWIPE)
    },
    Setting(context, Settings.PREF_SPACE_VERTICAL_SWIPE, R.string.show_vertical_space_swipe) {
        val items = listOf(
            stringResource(R.string.space_swipe_move_cursor_entry) to "move_cursor",
            stringResource(R.string.switch_language) to "switch_language",
            stringResource(R.string.space_swipe_toggle_numpad_entry) to "toggle_numpad",
            stringResource(R.string.action_none) to "none",
        )
        ListPreference(it, items, Defaults.PREF_SPACE_VERTICAL_SWIPE)
    },
    Setting(context, Settings.PREF_LANGUAGE_SWIPE_DISTANCE, R.string.prefs_language_swipe_distance) { setting ->
        SliderPreference(
            name = setting.title,
            key = setting.key,
            default = Defaults.PREF_LANGUAGE_SWIPE_DISTANCE,
            range = 2f..18f,
            description = { it.toString() }
        )
    },
    Setting(context, Settings.PREF_DELETE_SWIPE, R.string.delete_swipe, R.string.delete_swipe_summary) {
        SwitchPreference(it, Defaults.PREF_DELETE_SWIPE)
    },
    Setting(context, Settings.PREF_SPACE_TO_CHANGE_LANG,
        R.string.prefs_long_press_keyboard_to_change_lang,
        R.string.prefs_long_press_keyboard_to_change_lang_summary)
    {
        SwitchPreference(it, Defaults.PREF_SPACE_TO_CHANGE_LANG)
    },
    Setting(context, Settings.PREFS_LONG_PRESS_SYMBOLS_FOR_NUMPAD, R.string.prefs_long_press_symbol_for_numpad) {
        SwitchPreference(it, Defaults.PREFS_LONG_PRESS_SYMBOLS_FOR_NUMPAD)
    },
    Setting(context, Settings.PREF_ENABLE_EMOJI_ALT_PHYSICAL_KEY, R.string.prefs_enable_emoji_alt_physical_key,
        R.string.prefs_enable_emoji_alt_physical_key_summary)
    {
        SwitchPreference(it, Defaults.PREF_ENABLE_EMOJI_ALT_PHYSICAL_KEY)
    },
    Setting(context, Settings.PREF_SHOW_SETUP_WIZARD_ICON, R.string.show_setup_wizard_icon, R.string.show_setup_wizard_icon_summary) {
        val ctx = LocalContext.current
        SwitchPreference(it, Defaults.PREF_SHOW_SETUP_WIZARD_ICON) { SystemBroadcastReceiver.toggleAppIcon(ctx) }
    },
    Setting(context, Settings.PREF_ABC_AFTER_SYMBOL_SPACE,
        R.string.switch_keyboard_after, R.string.after_symbol_and_space)
    {
        SwitchPreference(it, Defaults.PREF_ABC_AFTER_SYMBOL_SPACE)
    },
    Setting(context, Settings.PREF_ABC_AFTER_NUMPAD_SPACE,
        R.string.switch_keyboard_after, R.string.after_numpad_and_space)
    {
        SwitchPreference(it, Defaults.PREF_ABC_AFTER_NUMPAD_SPACE)
    },
    Setting(context, Settings.PREF_ABC_AFTER_EMOJI, R.string.switch_keyboard_after, R.string.after_emoji) {
        SwitchPreference(it, Defaults.PREF_ABC_AFTER_EMOJI)
    },
    Setting(context, Settings.PREF_ABC_AFTER_CLIP, R.string.switch_keyboard_after, R.string.after_clip) {
        SwitchPreference(it, Defaults.PREF_ABC_AFTER_EMOJI)
    },
    Setting(context, Settings.PREF_CUSTOM_CURRENCY_KEY, R.string.customize_currencies) { setting ->
        var showDialog by rememberSaveable { mutableStateOf(false) }
        Preference(
            name = setting.title,
            onClick = { showDialog = true }
        )
        if (showDialog) {
            val prefs = LocalContext.current.prefs()
            TextInputDialog(
                onDismissRequest = { showDialog = false },
                textInputLabel = { Text(stringResource(R.string.customize_currencies_detail)) },
                initialText = prefs.getString(setting.key, Defaults.PREF_CUSTOM_CURRENCY_KEY)!!,
                onConfirmed = { prefs.edit().putString(setting.key, it).apply(); KeyboardLayoutSet.onSystemLocaleChanged() },
                title = { Text(stringResource(R.string.customize_currencies)) },
                neutralButtonText = if (prefs.contains(setting.key)) stringResource(R.string.button_default) else null,
                onNeutral = { prefs.edit().remove(setting.key).apply(); KeyboardLayoutSet.onSystemLocaleChanged() },
                checkTextValid = { text -> text.splitOnWhitespace().none { it.length > 8 } }
            )
        }
    },
    Setting(context, Settings.PREF_MORE_POPUP_KEYS, R.string.show_popup_keys_title) {
        val items = listOf(POPUP_KEYS_NORMAL, POPUP_KEYS_MAIN, POPUP_KEYS_MORE, POPUP_KEYS_ALL).map { setting ->
            stringResource(morePopupKeysResId(setting)) to setting
        }
        ListPreference(it, items, Defaults.PREF_MORE_POPUP_KEYS) { KeyboardLayoutSet.onSystemLocaleChanged() }
    },
    Setting(context, SettingsWithoutKey.BACKUP_RESTORE, R.string.backup_restore_title) {
        BackupRestorePreference(it)
    },
    Setting(context, Settings.PREF_TIMESTAMP_FORMAT, R.string.timestamp_format_title) {
        TextInputPreference(it, Defaults.PREF_TIMESTAMP_FORMAT) { checkTimestampFormat(it) }
    },
    Setting(context, SettingsWithoutKey.DEBUG_SETTINGS, R.string.debug_settings_title) {
        Preference(
            name = it.title,
            onClick = { SettingsDestination.navigateTo(SettingsDestination.Debug) }
        ) { NextScreenIcon() }
    },
    Setting(context, Settings.PREF_EMOJI_MAX_SDK, R.string.prefs_key_emoji_max_sdk) { setting ->
        SliderPreference(
            name = setting.title,
            key = setting.key,
            default = Defaults.PREF_EMOJI_MAX_SDK,
            range = 21f..35f,
            description = {
                "Android " + when(it) {
                    21 -> "5.0"
                    22 -> "5.1"
                    23 -> "6"
                    24 -> "7.0"
                    25 -> "7.1"
                    26 -> "8.0"
                    27 -> "8.1"
                    28 -> "9"
                    29 -> "10"
                    30 -> "11"
                    31 -> "12"
                    32 -> "12L"
                    33 -> "13"
                    34 -> "14"
                    35 -> "15"
                    else -> "version unknown"
                }
            },
            onConfirmed = { KeyboardSwitcher.getInstance().setThemeNeedsReload() }
        )
    },
    Setting(context, Settings.PREF_URL_DETECTION, R.string.url_detection_title, R.string.url_detection_summary) {
        SwitchPreference(it, Defaults.PREF_URL_DETECTION)
    },
    Setting(context, SettingsWithoutKey.LOAD_GESTURE_LIB, R.string.load_gesture_library, R.string.load_gesture_library_summary) {
        LoadGestureLibPreference(it)
    },
)

@Preview
@Composable
private fun Preview() {
    SettingsActivity.settingsContainer = SettingsContainer(LocalContext.current)
    Theme(previewDark) {
        Surface {
            AdvancedSettingsScreen { }
        }
    }
}
