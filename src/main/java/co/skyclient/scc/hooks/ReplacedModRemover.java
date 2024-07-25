package co.skyclient.scc.hooks;

import cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.MixinTweaker;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ReplacedModRemover extends LaunchWrapperTweaker {
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

        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL location = codeSource.getLocation();
            try {
                File file = new File(location.toURI());
                if (file.isFile()) {
                    CoreModManager.getIgnoredMods().remove(file.getName());
                    CoreModManager.getReparseableCoremods().add(file.getName());
                    try {
                        try {
                            List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses"); // tweak classes before other mod trolling
                            if (tweakClasses.contains("org.spongepowered.asm.launch.MixinTweaker")) { // if there's already a mixin tweaker, we'll just load it like "usual"
                                new MixinTweaker(); // also we might not need to make a new mixin tweawker all the time but im just making sure
                            } else if (!Launch.blackboard.containsKey("mixin.initialised")) { // if there isnt, we do our own trolling
                                List<ITweaker> tweaks = (List<ITweaker>) Launch.blackboard.get("Tweaks");
                                tweaks.add(new MixinTweaker());
                            }
                        } catch (Exception ignored) {
                            // if it fails i *think* we can just ignore it
                        }
                        try {
                            MixinBootstrap.getPlatform().addContainer(location.toURI());
                        } catch (Exception ignore) {
                            // fuck you essential
                            try {
                                Class<?> containerClass = Class.forName("org.spongepowered.asm.launch.platform.container.IContainerHandle");
                                Class<?> urlContainerClass = Class.forName("org.spongepowered.asm.launch.platform.container.ContainerHandleURI");
                                Object container = urlContainerClass.getConstructor(URI.class).newInstance(location.toURI());
                                MixinBootstrap.getPlatform().getClass().getDeclaredMethod("addContainer", containerClass).invoke(MixinBootstrap.getPlatform(), container);
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException("OneConfig's Mixin loading failed. Please contact https://polyfrost.org/discord to resolve this issue!");
                            }
                        }
                    } catch (Exception ignored) {

                    }
                }
            } catch (URISyntaxException ignored) {}
        } else {
            LogManager.getLogger().warn("No CodeSource, if this is not a development environment we might run into problems!");
            LogManager.getLogger().warn(this.getClass().getProtectionDomain());
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
