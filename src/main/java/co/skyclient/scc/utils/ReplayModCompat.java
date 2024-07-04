package co.skyclient.scc.utils;

import co.skyclient.scc.SkyclientCosmetics;
import co.skyclient.scc.config.Settings;
import com.replaymod.core.ReplayMod;
import com.replaymod.recording.Setting;

public class ReplayModCompat {

    /**
    * i have to move this to another method or else forge will die
     */
    public static void doReplayModStuff() {
        ReplayMod.instance.getSettingsRegistry().set(Setting.AUTO_START_RECORDING, false);
        ReplayMod.instance.getSettingsRegistry().save();
        Settings.hasWipedOutReplayModAutoRecording = true;
        SkyclientCosmetics.config.save();
    }
}
