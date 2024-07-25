package co.skyclient.scc.gui.greeting

import cc.polyfrost.oneconfig.libs.elementa.components.UIText
import cc.polyfrost.oneconfig.libs.elementa.components.UIWrappedText
import cc.polyfrost.oneconfig.libs.elementa.components.Window
import cc.polyfrost.oneconfig.libs.elementa.constraints.CenterConstraint
import cc.polyfrost.oneconfig.libs.elementa.constraints.SiblingConstraint
import cc.polyfrost.oneconfig.libs.elementa.dsl.*
import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.utils.Multithreading
import cc.polyfrost.oneconfig.utils.NetworkUtils
import co.skyclient.scc.gui.greeting.components.ButtonComponent
import co.skyclient.scc.gui.greeting.components.CorrectOutsidePixelConstraint
import co.skyclient.scc.gui.greeting.components.GreetingSlide
import com.google.gson.JsonParser
import net.minecraft.client.Minecraft
import net.minecraft.launchwrapper.Launch
import java.awt.Color
import java.io.File

class ImportSlide : GreetingSlide<OptimizationSlide>(OptimizationSlide::class.java) {
    private val parentConfig: File? = run {
        val parent = Minecraft.getMinecraft().mcDataDir.parentFile
        if (parent?.name != ".minecraft" || !parent.isDirectory) {
            val parentParent = parent?.parentFile
            if (parentParent?.name != ".minecraft" || !parentParent.isDirectory) {
                null
            } else {
                parentParent
            }
        } else {
            parent
        }
    }
    init {
        if (parentConfig == null) {
            displayNextScreen()
        }
        hideNextButton()
    }

    val text by UIWrappedText("""
        Would you like to import your config from .minecraft?
    """.trimIndent(), centered = true) constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 75.percent()
        textScale = 3.pixels()
    } childOf window

    val secondaryText by UIWrappedText("""
        This means if you previously changed settings for your mods before installing SkyClient, Skyclient would copy those settings.
        ${ChatColor.ITALIC}You will still need to import resource packs manually. Please contact the SkyClient Discord for more information.${ChatColor.RESET}
    """.trimIndent(), centered = true) constrain {
        x = CenterConstraint()
        y = SiblingConstraint(5f).also { it.constrainTo = text }
        width = 100.percent()
    } childOf window

    val progressText by UIText() constrain {
        color = Color.GREEN.darker().toConstraint()
        x = CenterConstraint()
        y = 2.pixels(alignOpposite = true)
    } childOf window

    val yesButton by ButtonComponent("${ChatColor.GREEN}Yes") {
        hideButtons()
        progressText.setFloating(true)
        Multithreading.runAsync {
            progressText.setText("Downloading config locations...")
            val configLocations = arrayListOf<String>()
            try {
                NetworkUtils.getJsonElement("https://cdn.jsdelivr.net/gh/SkyblockClient/SkyblockClient-REPO@main/files/config_locations.json").asJsonArray.forEach {
                    configLocations.add(it.asString)
                }
            } catch (e: Exception) {
                progressText.setText("${ChatColor.RED}Failed, using offline locations...")
                Thread.sleep(1000)
                e.printStackTrace()
                configLocations.add("config")
                configLocations.add("essential/config.toml")
                configLocations.add("OneConfig/config")
                configLocations.add("OneConfig/profiles")
                configLocations.add("W-OVERFLOW")
                configLocations.add("options.txt")
                configLocations.add("optionsof.txt")
            }
            for (location in configLocations) {
                try {
                    progressText.setText("Finding \"$location\"...")
                    val file = File(parentConfig, location)
                    if (file.exists()) {
                        progressText.setText("Copying \"$location\"...")
                        var errored = false
                        File(Launch.minecraftHome, location).copyRecursively(File(Launch.minecraftHome, location + "-backup"), overwrite = true, onError = { _, _ ->
                            errored = true
                            return@copyRecursively OnErrorAction.SKIP
                        })
                        if (errored) {
                            progressText.setText("${ChatColor.RED}\"$location\" failed to copy a backup, skipping...")
                            Thread.sleep(1000)
                        } else {
                            file.copyRecursively(File(Launch.minecraftHome, location), overwrite = true, onError = { _, _ -> OnErrorAction.SKIP })
                            Thread.sleep(500)
                        }
                    } else {
                        progressText.setText("${ChatColor.RED}\"$location\" not found, skipping...")
                        Thread.sleep(1000)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    progressText.setText("${ChatColor.RED}Finding \"$location\" failed, skipping...")
                    Thread.sleep(1000)
                }
            }
            Window.enqueueRenderOperation {
                displayNextScreen()
            }
        }
    } constrain {
        y = CenterConstraint()
        x = CorrectOutsidePixelConstraint(window.getWidth() / 2 - 2)
    } childOf blackbar

    private fun hideButtons() {
        yesButton.hide(true)
        noButton.hide(true)
    }

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
}
