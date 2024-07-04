package com.replaymod.core;

public class SettingsRegistry {

    public <T> void set(SettingKey<T> key, T value) {
        // dummy
    }

    public void save() {
        // dummy
    }

    public interface SettingKey<T> {
        String getCategory();
        String getKey();
        String getDisplayString();
        T getDefault();
    }

    public static class SettingKeys<T> implements SettingKey<T> {
        private final String category;
        private final String key;
        private final String displayString;
        private final T defaultValue;

        public SettingKeys(String category, String key, String displayString, T defaultValue) {
            this.category = category;
            this.key = key;
            this.displayString = displayString;
            this.defaultValue = defaultValue;
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getDisplayString() {
            return displayString;
        }

        @Override
        public T getDefault() {
            return defaultValue;
        }
    }
}
