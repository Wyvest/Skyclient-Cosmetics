package co.skyclient.scc.hooks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import gg.essential.loader.stage0.EssentialSetupTweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ReplacedModRemover extends EssentialSetupTweaker {
    private static final JsonParser PARSER = new JsonParser();

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        try {
            File modsFolder = new File(Launch.minecraftHome, "mods");
            HashMap<String, Triple<File, String, String>> modsMap = new HashMap<>(); //modid : file, version, name
            File[] modFolder = modsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
            if (modFolder != null) {
                for (File file : modFolder) {
                    try {
                        try (ZipFile mod = new ZipFile(file)) {
                            ZipEntry entry = mod.getEntry("mcmod.info");
                            if (entry != null) {
                                try (InputStream inputStream = mod.getInputStream(entry)) {
                                    byte[] availableBytes = new byte[inputStream.available()];
                                    inputStream.read(availableBytes, 0, inputStream.available());
                                    JsonObject modInfo = PARSER.parse(new String(availableBytes)).getAsJsonArray().get(0).getAsJsonObject();
                                    if (!modInfo.has("modid") || !modInfo.has("version")) {
                                        continue;
                                    }

                                    String modid = modInfo.get("modid").getAsString().toLowerCase(Locale.ENGLISH);
                                    if (!modsMap.containsKey(modid)) {
                                        modsMap.put(modid, new Triple<>(file, modInfo.get("version").getAsString(), modInfo.has("name") ? modInfo.get("name").getAsString() : modid));
                                    }
                                }
                            }
                        }
                    } catch (MalformedJsonException | IllegalStateException ignored) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (modsMap.containsKey("itlt")) {
                tryDeleting(modsMap.get("itlt").first);
            }
            if (modsMap.containsKey("custommainmenu")) {
                tryDeleting(modsMap.get("custommainmenu").first);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.injectIntoClassLoader(classLoader);
    }

    private void tryDeleting(File file) {
        if (!file.delete()) {
            if (!file.delete()) {
                if (!file.delete()) {
                    file.deleteOnExit();
                }
            }
        }
    }

    public static class Triple<A, B, C> {
        public A first;
        public B second;
        public C third;

        public Triple(A a, B b, C c) {
            first = a;
            second = b;
            third = c;
        }

        @Override
        public String toString() {
            return "Triple{" +
                    "first=" + first +
                    ", second=" + second +
                    ", third=" + third +
                    '}';
        }
    }
}
