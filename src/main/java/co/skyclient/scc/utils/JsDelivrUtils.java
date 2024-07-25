package co.skyclient.scc.utils;

import cc.polyfrost.oneconfig.utils.Multithreading;
import cc.polyfrost.oneconfig.utils.NetworkUtils;
import com.google.gson.JsonArray;

public class JsDelivrUtils {
    private static String dataCommitId = "main";
    private static String websiteCommitId = "main";

    public static String getWebsiteUrl(String path) {
        return "https://cdn.jsdelivr.net/gh/SkyblockClient/Website@" + websiteCommitId + path;
    }

    public static String getDataUrl(String path) {
        return "https://cdn.jsdelivr.net/gh/SkyblockClient/SkyblockClient-REPO@" + dataCommitId + path;
    }

    public static void initialize() {
        Multithreading.runAsync(() -> {
            try {
                JsonArray websiteApi = NetworkUtils.getJsonElement("https://api.github.com/repos/SkyblockClient/Website/commits").getAsJsonArray();
                websiteCommitId = websiteApi.get(0).getAsJsonObject().get("sha").getAsString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JsonArray dataApi = NetworkUtils.getJsonElement("https://api.github.com/repos/SkyblockClient/SkyblockClient-REPO/commits").getAsJsonArray();
                dataCommitId = dataApi.get(0).getAsJsonObject().get("sha").getAsString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
