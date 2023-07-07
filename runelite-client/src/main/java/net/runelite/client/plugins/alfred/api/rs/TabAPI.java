package net.runelite.client.plugins.alfred.api.rs;

import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.alfred.Alfred;

import java.awt.*;

public class TabAPI {

    private static boolean clickTab(WidgetInfo widgetInfo) {
        Rectangle bounds = Utility.getTabBounds(widgetInfo);
        if (bounds == null) {
            return false;
        }

        Alfred.getMouse().leftClick(bounds);
        return true;
    }

    public static boolean clickCombatTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_COMBAT_TAB);
    }

    public static boolean clickSkillsTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_STATS_TAB);
    }

    public static boolean clickQuestsTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB);
    }

    public static boolean clickInventoryTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB);
    }

    public static boolean clickEquipmentTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB);
    }

    public static boolean clickPrayerTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_PRAYER_TAB);
    }

    public static boolean clickMagicTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB);
    }

    public static boolean clickClanChatTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_FRIENDS_CHAT_TAB);
    }

    public static boolean clickFriendsTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_FRIENDS_TAB);
    }

    public static boolean clickIgnoreTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_IGNORES_TAB);
    }

    public static boolean clickLogoutTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_LOGOUT_TAB);
    }

    public static boolean clickOptionsTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_OPTIONS_TAB);
    }

    public static boolean clickEmotesTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_EMOTES_TAB);
    }

    public static boolean clickMusicTab() {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_MUSIC_TAB);
    }

    public static boolean clickCompass() {
        return clickTab(WidgetInfo.RESIZABLE_MINIMAP_STONES_WIDGET);
    }

    public static boolean clickXP() {
        return clickTab(WidgetInfo.MINIMAP_XP_ORB);
    }

    public static boolean clickQuickPrayer() {
        return clickTab(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
    }

    public static boolean clickRunning() {
        return clickTab(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB);
    }

    public static void test() {
        int delay = 500;

        TabAPI.clickCombatTab();
        Alfred.sleep(delay);

        TabAPI.clickSkillsTab();
        Alfred.sleep(delay);

        TabAPI.clickQuestsTab();
        Alfred.sleep(delay);

        TabAPI.clickInventoryTab();
        Alfred.sleep(delay);

        TabAPI.clickEquipmentTab();
        Alfred.sleep(delay);

        TabAPI.clickPrayerTab();
        Alfred.sleep(delay);

        TabAPI.clickMagicTab();
        Alfred.sleep(delay);

        TabAPI.clickClanChatTab();
        Alfred.sleep(delay);

        TabAPI.clickFriendsTab();
        Alfred.sleep(delay);

        TabAPI.clickIgnoreTab();
        Alfred.sleep(delay);

        TabAPI.clickLogoutTab();
        Alfred.sleep(delay);

        TabAPI.clickOptionsTab();
        Alfred.sleep(delay);

        TabAPI.clickEmotesTab();
        Alfred.sleep(delay);

        TabAPI.clickMusicTab();
        Alfred.sleep(delay);

        TabAPI.clickCompass();
        Alfred.sleep(delay);

        TabAPI.clickXP();
        Alfred.sleep(delay);

        TabAPI.clickQuickPrayer();
        Alfred.sleep(delay);

        TabAPI.clickRunning();
    }
}
