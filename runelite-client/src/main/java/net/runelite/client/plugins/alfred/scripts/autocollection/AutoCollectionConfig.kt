package net.runelite.client.plugins.alfred.scripts.autocollection

import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem
import net.runelite.client.config.ConfigSection

@ConfigGroup(AutoCollectionPlugin.CONFIG_GROUP)
interface AutoCollectionConfig : Config {
    companion object {
        @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
        )
        const val generalSection = "general"
    }

    @ConfigItem(
        keyName = "points",
        name = "Points",
        description = "Points, each line is x,y,z",
        position = 0,
        section = generalSection
    )
    fun points(): String {
        return ""
    }
}