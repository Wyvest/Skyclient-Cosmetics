package club.sk1er.patcher.config;

public class PatcherConfig {
    public static boolean hudCaching;
    public static boolean cullingFix;
    public static boolean separateResourceLoading;
    public static boolean disableAchievements;
    public static boolean autoTitleScale;
    public static boolean unfocusedFPS;
    public static boolean cleanProjectiles;
    public static boolean numericalEnchants;
    public static boolean staticItems;
    public static boolean limitChunks;
    public static int chunkUpdateLimit;
    public static boolean playerBackFaceCulling;
    public static int openToLanReplacement;

    public static PatcherConfig INSTANCE = new PatcherConfig();

    public void markDirty() {
        // lol
    }

    public void writeData() {
        // lol
    }
}
