package net.runelite.client.plugins.alfred.api.rs.combat

import net.runelite.api.VarPlayer
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.alfred.Alfred

class RSCombatHelper {
    fun clickPunch(): Boolean {
        Alfred.api.tabs().clickCombatTab()
        Alfred.sleep(200, 400)
        val punch = Alfred.getClientThread().invokeOnClientThread { Alfred.getClient().getWidget(WidgetInfo.COMBAT_STYLE_ONE) } ?: return false
        Alfred.getMouse().leftClick(punch.bounds)
        return true
    }

    fun clickKick(): Boolean {
        Alfred.api.tabs().clickCombatTab()
        Alfred.sleep(200, 400)
        val punch = Alfred.getClientThread().invokeOnClientThread { Alfred.getClient().getWidget(WidgetInfo.COMBAT_STYLE_TWO) } ?: return false
        Alfred.getMouse().leftClick(punch.bounds)
        return true
    }

    fun clickBlock(): Boolean {
        Alfred.api.tabs().clickCombatTab()
        Alfred.sleep(200, 400)
        val punch = Alfred.getClientThread().invokeOnClientThread { Alfred.getClient().getWidget(WidgetInfo.COMBAT_STYLE_FOUR) } ?: return false
        Alfred.getMouse().leftClick(punch.bounds)
        return true
    }

    fun clickAutoRetaliate(): Boolean {
        Alfred.api.tabs().clickCombatTab()
        Alfred.sleep(200, 400)
        val punch = Alfred.getClientThread().invokeOnClientThread { Alfred.getClient().getWidget(WidgetInfo.COMBAT_AUTO_RETALIATE) } ?: return false
        Alfred.getMouse().leftClick(punch.bounds)
        return true
    }

    val isPunchSelected: Boolean
        get() = Alfred.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 0
    val isKickSelected: Boolean
        get() = Alfred.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 1
    val isBlockSelected: Boolean
        get() = Alfred.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 3
    val isAutoRetaliateSelected: Boolean
        get() = Alfred.getVarbitPlayerValue(172) == 0
}
