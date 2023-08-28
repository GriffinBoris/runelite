package net.runelite.client.plugins.alfred.scripts.ashpicker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(AshPickerPlugin.CONFIG_GROUP)
public interface AshPickerConfig extends Config {

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "ashPrice",
            name = "Ash Price",
            description = "Ash Price",
            position = 0,
            section = generalSection
    )
    default int ashPrice() {
        return 0;
    }

    @ConfigItem(
            keyName = "worldNumber",
            name = "World",
            description = "World",
            position = 1,
            section = generalSection
    )
    default int worldNumber() {
        return 302;
    }

}
