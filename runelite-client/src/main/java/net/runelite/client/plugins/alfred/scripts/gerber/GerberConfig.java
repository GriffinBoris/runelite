package net.runelite.client.plugins.alfred.scripts.gerber;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(GerberPlugin.CONFIG_GROUP)
public interface GerberConfig extends Config {

    @ConfigSection(
            name = "Tasks",
            description = "Tasks",
            position = 0,
            closedByDefault = false
    )
    String taskSection = "tasks";

    @ConfigItem(
            keyName = "trainCombat",
            name = "Train Combat",
            description = "Train Combat",
            position = 0,
            section = taskSection
    )
    default boolean trainCombat() {
        return true;
    }

    @ConfigItem(
            keyName = "trainWoodcutting",
            name = "Train Woodcutting",
            description = "Train Woodcutting",
            position = 1,
            section = taskSection
    )
    default boolean trainWoodcutting() {
        return true;
    }

    @ConfigItem(
            keyName = "trainMining",
            name = "Train Mining",
            description = "Train Mining",
            position = 2,
            section = taskSection
    )
    default boolean trainMining() {
        return true;
    }

    @ConfigItem(
            keyName = "trainFishing",
            name = "Train Fishing",
            description = "Train Fishing",
            position = 3,
            section = taskSection
    )
    default boolean trainFishing() {
        return true;
    }


    @ConfigSection(
            name = "Skill Levels",
            description = "Skill Levels",
            position = 1,
            closedByDefault = false
    )
    String skillsSection = "skills";

    @ConfigItem(
            keyName = "attackLevel",
            name = "Attack",
            description = " Attack Level",
            position = 0,
            section = skillsSection
    )
    default int attackLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "strengthLevel",
            name = "Strength",
            description = " Strength Level",
            position = 1,
            section = skillsSection
    )
    default int strengthLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "defenseLevel",
            name = "Defense",
            description = " Defense Level",
            position = 2,
            section = skillsSection
    )
    default int defenseLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "prayerLevel",
            name = "Prayer",
            description = " Prayer Level",
            position = 3,
            section = skillsSection
    )
    default int prayerLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "miningLevel",
            name = "Mining",
            description = " Mining Level",
            position = 4,
            section = skillsSection
    )
    default int miningLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "woodcuttingLevel",
            name = "Woodcutting",
            description = "Woodcutting Level",
            position = 4,
            section = skillsSection
    )
    default int woodcuttingLevel() {
        return 0;
    }

    @ConfigItem(
            keyName = "fishingLevel",
            name = "Fishing",
            description = "Fishing Level",
            position = 5,
            section = skillsSection
    )
    default int fishingLevel() {
        return 0;
    }

    @ConfigSection(
            name = "Combat Settings",
            description = "Combat Settings",
            position = 2,
            closedByDefault = false
    )
    String combatSettingsSection = "combatSettings";

    @ConfigItem(
            keyName = "collectItems",
            name = "Loot Items",
            description = "Loot Items",
            position = 0,
            section = combatSettingsSection
    )
    default boolean collectItems() {
        return true;
    }

    @ConfigItem(
            keyName = "buryBones",
            name = "Bury Bones",
            description = "Bury Bones",
            position = 1,
            section = combatSettingsSection
    )
    default boolean buryBones() {
        return true;
    }

    @ConfigSection(
            name = "Mining Settings",
            description = "Mining Settings",
            position = 3,
            closedByDefault = false
    )
    String miningSettingsSection = "miningSettings";

    @ConfigItem(
            keyName = "keepOre",
            name = "Keep Ore",
            description = "Keep Ore",
            position = 0,
            section = miningSettingsSection
    )
    default boolean keepOre() {
        return true;
    }

    @ConfigSection(
            name = "Woodcutting Settings",
            description = "Woodcutting Settings",
            position = 4,
            closedByDefault = false
    )
    String woodcuttingSettingsSection = "woodcuttingSettings";

    @ConfigItem(
            keyName = "keepLogs",
            name = "Keep Logs",
            description = "Keep Logs",
            position = 0,
            section = woodcuttingSettingsSection
    )
    default boolean keepLogs() {
        return true;
    }

    @ConfigSection(
            name = "Fishing Settings",
            description = "Fishing Settings",
            position = 5,
            closedByDefault = false
    )
    String fishingSettingsSection = "fishingSettings";

    @ConfigItem(
            keyName = "keepFish",
            name = "Keep Fish",
            description = "Keep Fish",
            position = 0,
            section = fishingSettingsSection
    )
    default boolean keepFish() {
        return true;
    }
}
