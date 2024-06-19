package me.partlysanestudios.partlysaneskies.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType

object OneConfigScreen : Config(
    Mod("Partly Sane Skies", ModType.SKYBLOCK, "/assets/partlysaneskies/textures/logo_oneconfig.png"),
    "partly-sane-skies/config.json") {
    var customMainMenu = true
}