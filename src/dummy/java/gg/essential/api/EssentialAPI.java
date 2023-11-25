package gg.essential.api;

import gg.essential.api.data.OnboardingData;

public interface EssentialAPI {

    static OnboardingData getOnboardingData() {
        return new OnboardingData() {
            @Override
            public boolean hasAcceptedEssentialTOS() {
                return false;
            }

            @Override
            public boolean hasDeniedEssentialTOS() {
                return false;
            }
        };
    }
}
