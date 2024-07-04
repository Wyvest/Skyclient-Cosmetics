package com.replaymod.recording;

import com.replaymod.core.SettingsRegistry;

public final class Setting<T> extends SettingsRegistry.SettingKeys<T> {
    public static final Setting<Boolean> AUTO_START_RECORDING = make("autoStartRecording", "autostartrecording", true);

    private static <T> Setting<T> make(String key, String displayName, T defaultValue) {
        return new Setting<>(key, displayName, defaultValue);
    }

    public Setting(String key, String displayString, T defaultValue) {
        super("recording", key, displayString == null ? null : "replaymod.gui.settings." + displayString, defaultValue);
    }
}