package net.runelite.client.plugins.alfred.scripts.transporthelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.alfred.enums.TransportTypes;
import net.runelite.client.plugins.alfred.enums.WorldDestinations;
import net.runelite.client.plugins.alfred.scripts.collection.WorldCollectionPlugin;

@ConfigGroup(TransportHelperPlugin.CONFIG_GROUP)
public interface TransportHelperConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Transport Type",
            name = "Transport Type",
            description = "Transport Type",
            position = 0,
            section = generalSection
    )
    default TransportTypes transportType() {
        return TransportTypes.DOOR;
    }
}


