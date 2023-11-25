package co.skyclient.scc.gui.greeting

import club.sk1er.patcher.config.PatcherConfig
import co.skyclient.scc.SkyclientCosmetics
import co.skyclient.scc.gui.greeting.components.CorrectOutsidePixelConstraint
import co.skyclient.scc.gui.greeting.components.GreetingSlide
import co.skyclient.scc.utils.Files
import cc.polyfrost.oneconfig.libs.elementa.components.UIText
import cc.polyfrost.oneconfig.libs.elementa.components.UIWrappedText
import cc.polyfrost.oneconfig.libs.elementa.components.Window
import cc.polyfrost.oneconfig.libs.elementa.constraints.CenterConstraint
import cc.polyfrost.oneconfig.libs.elementa.constraints.SiblingConstraint
import cc.polyfrost.oneconfig.libs.elementa.dsl.*
import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UDesktop
import cc.polyfrost.oneconfig.utils.Multithreading
import cc.polyfrost.oneconfig.utils.Notifications
import cc.polyfrost.oneconfig.utils.dsl.tick
import co.skyclient.scc.gui.greeting.components.ButtonComponent
import co.skyclient.scc.gui.greeting.components.onLeftClick
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.MathHelper
import java.awt.Color
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.net.URI

class OptimizationSlide : GreetingSlide<HUDChachySlide>(HUDChachySlide::class.java) {
    init {
        hideNextButton()
    }

    val text by UIWrappedText("""
        Would you like to apply pre-optimized settings?
    """.trimIndent(), centered = true) constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 75.percent()
        textScale = 3.pixels()
    } childOf window

    val secondaryText by UIWrappedText("""
        Click here for a full explanation on what this changes.
    """.trimIndent(), centered = true) constrain {
        x = CenterConstraint()
        y = SiblingConstraint(5f).also { it.constrainTo = text }
        width = 100.percent()
    } childOf window

    init {
        secondaryText.onLeftClick { UDesktop.browse(URI.create("https://github.com/nacrt/SkyblockClient-REPO/blob/main/files/guides/skyclient_greeting_optimizer.md")) }
    }

    val progressText by UIText() constrain {
        color = Color.GREEN.darker().toConstraint()
        x = CenterConstraint()
        y = 2.pixels(alignOpposite = true)
    } childOf window

    val yesButton by ButtonComponent("${ChatColor.GREEN}Yes") {
        progressText.setText("Applying optimized settings... This might take a while...")
        tick(2) {
            if (isOptifineLoaded()) {
                try {
                    val settingsClass: Class<GameSettings> = mc.gameSettings.javaClass
                    val configClass = Class.forName("Config")
                    val shadersClass = Class.forName("net.optifine.shaders.Shaders")
                    settingsClass.getFieldAndSetAccessible("ofFastRender")?.setBooleanSafe(mc.gameSettings, false)
                    configClass.getMethodAndSetAccessible("updateFramebufferSize")?.invokeSafe(null)

                    settingsClass.getFieldAndSetAccessible("ofFastMath")?.setBooleanSafe(mc.gameSettings, false)
                    MathHelper::class.java.getFieldAndSetAccessible("fastMath")?.setBooleanSafe(null, false)

                    settingsClass.getFieldAndSetAccessible("ofSmartAnimations")?.setBooleanSafe(mc.gameSettings, true)
                    settingsClass.getFieldAndSetAccessible("ofRenderRegions")?.setBooleanSafe(mc.gameSettings, true)
                    settingsClass.getFieldAndSetAccessible("ofSmoothFps")?.setBooleanSafe(mc.gameSettings, false)
                    settingsClass.getFieldAndSetAccessible("ofSmoothWorld")?.setBooleanSafe(mc.gameSettings, false)
                    configClass.getMethodAndSetAccessible("updateThreadPriorities")?.invokeSafe(null)

                    settingsClass.getFieldAndSetAccessible("ofClouds")?.setIntSafe(mc.gameSettings, 0)
                    settingsClass.getMethodAndSetAccessible("updateRenderClouds")?.invokeSafe(mc.gameSettings)
                    mc.renderGlobal.javaClass.getMethodAndSetAccessible("resetClouds")?.invokeSafe(mc.renderGlobal)

                    settingsClass.getFieldAndSetAccessible("ofFogType")?.setIntSafe(mc.gameSettings, 0)
                    val connectedTextures = settingsClass.getFieldAndSetAccessible("ofConnectedTextures")
                    try {
                        if (isNewPatcher) {
                            connectedTextures?.getInt(null).let {
                                if (it == 0 || it == 3) {
                                    connectedTextures?.setIntSafe(Minecraft.getMinecraft().gameSettings, 2)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    settingsClass.getFieldAndSetAccessible("ofTranslucentBlocks")?.setIntSafe(mc.gameSettings, 1)
                    settingsClass.getFieldAndSetAccessible("ofDroppedItems")?.setIntSafe(mc.gameSettings, 1)
                    settingsClass.getFieldAndSetAccessible("ofVignette")?.setIntSafe(mc.gameSettings, 1)
                    settingsClass.getFieldAndSetAccessible("ofSwampColors")?.setBooleanSafe(mc.gameSettings, false)
                    Class.forName("net.optifine.CustomColors").getMethodAndSetAccessible("updateUseDefaultGrassFoliageColors")
                        ?.invokeSafe(null)

                    settingsClass.getFieldAndSetAccessible("ofRain")?.setIntSafe(mc.gameSettings, 1)
                    shadersClass.getMethodAndSetAccessible("setShaderPack")?.invokeSafe(null, "OFF")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (SkyclientCosmetics.isPatcher) {
                try {
                    PatcherConfig.cullingFix = true
                    PatcherConfig.separateResourceLoading = true
                    PatcherConfig.disableAchievements = true
                    PatcherConfig.autoTitleScale = true
                    PatcherConfig.unfocusedFPS = true
                    PatcherConfig.cleanProjectiles = true
                    PatcherConfig.numericalEnchants = true
                    PatcherConfig.staticItems = true
                    PatcherConfig.limitChunks = true
                    PatcherConfig.chunkUpdateLimit = 250
                    PatcherConfig.playerBackFaceCulling = true
                    PatcherConfig.openToLanReplacement = 1
                    PatcherConfig.INSTANCE.markDirty()
                    PatcherConfig.INSTANCE.writeData()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mc.gameSettings.saveOptions()
            if (mc.gameSettings.entityShadows) {
                mc.gameSettings.setOptionValue(GameSettings.Options.ENTITY_SHADOWS, 0)
            }
            if (!mc.gameSettings.useVbo) {
                mc.gameSettings.setOptionValue(GameSettings.Options.USE_VBO, 1)
            }
            if (mc.gameSettings.enableVsync) {
                mc.gameSettings.setOptionValue(GameSettings.Options.ENABLE_VSYNC, 0)
            }
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FRAMERATE_LIMIT, 260f)
            mc.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, 10)
            mc.gameSettings.guiScale = 3
            mc.gameSettings.saveOptions()
            mc.gameSettings.loadOptions()
            mc.refreshResources()
            mc.renderGlobal.loadRenderers()
            tick(2) {
                Window.enqueueRenderOperation { displayNextScreen() }
            }
        }
    } constrain {
        y = CenterConstraint()
        x = CorrectOutsidePixelConstraint(window.getWidth() / 2 - 2)
    } childOf blackbar

    val noButton by ButtonComponent("${ChatColor.RED}No") {
        displayNextScreen()
    } constrain {
        y = CenterConstraint()
        x = (window.getWidth() / 2 + 2).pixels()
    } childOf blackbar

    override fun setButtonFloat() {
        yesButton.setFloating(true)
        noButton.setFloating(true)
    }

    companion object {
        private var isNewPatcher = false

        fun sendCTMFixNotification() {
            isNewPatcher = true
            if (Files.greetingFile.exists()) {
                try {
                    val settingsClass: Class<GameSettings> = Minecraft.getMinecraft().gameSettings.javaClass
                    val field = settingsClass.getFieldAndSetAccessible("ofConnectedTextures")
                    field?.let { property ->
                        try {
                            property.getInt(Minecraft.getMinecraft().gameSettings).let {
                                if (it == 0 || it == 3) {
                                    Notifications.INSTANCE.send("SkyClientCosmetics", "New versions of Patcher fixes the Connected Textures crash on Forge.\n\nClick here to enable Connected Textures!", 10000f, Runnable {
                                        if (Minecraft.getMinecraft().theWorld != null) {
                                            Minecraft.getMinecraft().theWorld.sendQuittingDisconnectingPacket()
                                            Minecraft.getMinecraft().loadWorld(null)
                                            Minecraft.getMinecraft().displayGuiScreen(GuiMainMenu())
                                        }
                                        Multithreading.runAsync {
                                            while (Minecraft.getMinecraft().theWorld != null) {
                                                Thread.sleep(100)
                                            }
                                            Minecraft.getMinecraft().addScheduledTask {
                                                try {
                                                    property.setInt(Minecraft.getMinecraft().gameSettings, 2)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                    Notifications.INSTANCE.send("SkyClientCosmetics", "Failed to enable Connected Textures! Enable it manually in Options -> Video Settings -> Quality -> Connected Textures.", 10000f)
                                                    return@addScheduledTask
                                                }
                                                Minecraft.getMinecraft().gameSettings.saveOptions()
                                                Minecraft.getMinecraft().gameSettings.loadOptions()
                                                Minecraft.getMinecraft().refreshResources()
                                            }
                                        }
                                    })
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun Class<*>.getFieldAndSetAccessible(name: String): Field? {
            return try {
                val field = this.getDeclaredField(name)
                field.isAccessible = true
                field
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun Class<*>.getMethodAndSetAccessible(name: String): Method? {
            return try {
                val method = this.getDeclaredMethod(name)
                method.isAccessible = true
                method
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun Field.setBooleanSafe(obj: Any?, value: Boolean) {
            try {
                this.setBoolean(obj, value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun Field.setIntSafe(obj: Any?, value: Int) {
            try {
                this.setInt(obj, value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun Method.invokeSafe(obj: Any?, vararg strings: String) {
            try {
                this.invoke(obj, strings)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun isOptifineLoaded(): Boolean {
            return try {
                val clazz = Class.forName("Config")
                clazz.getDeclaredField("OF_RELEASE")
                true
            } catch (_: Exception) {
                false
            }
        }
    }
}