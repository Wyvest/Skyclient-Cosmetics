package co.skyclient.scc.utils;

import co.skyclient.scc.SkyclientCosmetics;
import gg.essential.api.EssentialAPI;

public class MixinHook {
    public static boolean hasEssentialAcceptedTOS() {
        return !SkyclientCosmetics.isEssential || (EssentialAPI.getOnboardingData().hasAcceptedEssentialTOS() || EssentialAPI.getOnboardingData().hasDeniedEssentialTOS());
    }
}
