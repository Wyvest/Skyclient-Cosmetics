package com.replaymod.core;

public class ReplayMod {

    public static ReplayMod instance;

    public SettingsRegistry getSettingsRegistry() {
        return new SettingsRegistry();
    }
}
