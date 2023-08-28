package net.runelite.client.plugins.alfred.api.rs.tab

import net.runelite.api.VarClientInt
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.alfred.Alfred

class RSTabHelper {
    val currentTab: WidgetInfo
        get() {
            val tab = Alfred.client.getVarcIntValue(VarClientInt.INVENTORY_TAB)
            return when (tab) {
                0 -> WidgetInfo.FIXED_VIEWPORT_COMBAT_TAB
                1 -> WidgetInfo.FIXED_VIEWPORT_STATS_TAB
                2 -> WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB
                3 -> WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB
                4 -> WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB
                5 -> WidgetInfo.FIXED_VIEWPORT_PRAYER_TAB
                6 -> WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB
                7 -> WidgetInfo.FIXED_VIEWPORT_FRIENDS_CHAT_TAB
                8 -> WidgetInfo.FIXED_VIEWPORT_IGNORES_TAB
                9 -> WidgetInfo.FIXED_VIEWPORT_FRIENDS_TAB
                10 -> WidgetInfo.FIXED_VIEWPORT_LOGOUT_TAB
                11 -> WidgetInfo.FIXED_VIEWPORT_OPTIONS_TAB
                12 -> WidgetInfo.FIXED_VIEWPORT_EMOTES_TAB
                13 -> WidgetInfo.FIXED_VIEWPORT_MUSIC_TAB
                else -> throw IllegalStateException("Unexpected tab value: $tab")
            }
        }

    private fun clickTab(widgetInfo: WidgetInfo): Boolean {
        if (widgetInfo.id == currentTab.id) {
            return true
        }
        Alfred.status = "Clicking tab: " + widgetInfo.name
        val bounds = Alfred.api.widgets.getWidget(widgetInfo).getBounds() ?: return false
        Alfred.mouse.leftClick(bounds)
        return Alfred.sleepUntil({ currentTab == widgetInfo }, 100, 3000)
    }

    fun clickCombatTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_COMBAT_TAB)
    }

    fun clickSkillsTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_STATS_TAB)
    }

    fun clickQuestsTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB)
    }

    fun clickInventoryTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB)
    }

    fun clickEquipmentTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB)
    }

    fun clickPrayerTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_PRAYER_TAB)
    }

    fun clickMagicTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB)
    }

    fun clickClanChatTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_FRIENDS_CHAT_TAB)
    }

    fun clickFriendsTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_FRIENDS_TAB)
    }

    fun clickIgnoreTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_IGNORES_TAB)
    }

    fun clickLogoutTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_LOGOUT_TAB)
    }

    fun clickOptionsTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_OPTIONS_TAB)
    }

    fun clickEmotesTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_EMOTES_TAB)
    }

    fun clickMusicTab(): Boolean {
        return clickTab(WidgetInfo.FIXED_VIEWPORT_MUSIC_TAB)
    }
}
