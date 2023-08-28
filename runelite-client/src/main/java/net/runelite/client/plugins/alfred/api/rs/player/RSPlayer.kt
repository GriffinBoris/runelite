package net.runelite.client.plugins.alfred.api.rs.player

import net.runelite.api.Player
import net.runelite.api.Skill
import net.runelite.api.Varbits
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.alfred.Alfred
import java.awt.Rectangle
import java.awt.Shape

class RSPlayer(private val runelitePlayer: Player) {
    companion object {
        private const val ANIMATION_IDLE = 808
        private const val ANIMATION_WALKING = 819
        private const val ANIMATION_RUNNING = 824
    }

    val name: String?
        get() = runelitePlayer.name
    val worldLocation: WorldPoint
        get() = runelitePlayer.worldLocation
    val worldArea: WorldArea
        get() = runelitePlayer.worldArea
    val clickBox: Rectangle
        get() = runelitePlayer.convexHull.bounds
    val convexHull: Shape
        get() = runelitePlayer.convexHull
    val runEnergy: Int
        get() = Alfred.client.energy / 100
    val isDead: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { runelitePlayer.isDead }
    val isWalking: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { runelitePlayer.poseAnimation != ANIMATION_WALKING }
    val isRunning: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { runelitePlayer.poseAnimation == ANIMATION_RUNNING }
    val isIdle: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { runelitePlayer.poseAnimation == 813 || runelitePlayer.poseAnimation == ANIMATION_IDLE }
    val isMoving: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { runelitePlayer.poseAnimation == ANIMATION_WALKING || runelitePlayer.poseAnimation == ANIMATION_RUNNING }
    val isAnimating: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { runelitePlayer.animation != -1 }
    val isInteracting: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { runelitePlayer.isInteracting }
    val isRunningActive: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { Alfred.client.getVarpValue(173) == 1 }
    val isQuickPrayerActive: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { Alfred.client.getVarbitValue(Varbits.QUICK_PRAYER) == 1 }
    val isXpDisplayActive: Boolean
        get() = Alfred.clientThread.invokeOnClientThread { Alfred.client.getVarbitValue(4702) == 1 }

    fun toggleRunning(value: Boolean) {
        val isRunning = isRunningActive
        if (value != isRunning) {
            Alfred.api.widgets.leftClickWidget(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB)
        }
    }

    fun toggleQuickPrayer(value: Boolean) {
        val isOn = isQuickPrayerActive
        if (value != isOn) {
            Alfred.api.widgets.leftClickWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB)
        }
    }

    fun toggleXpDisplay(value: Boolean) {
        val isOn = isXpDisplayActive
        if (value != isOn) {
            Alfred.api.widgets.leftClickWidget(WidgetInfo.MINIMAP_XP_ORB)
        }
    }

    fun getSkillLevel(skill: Skill?): Int {
        return Alfred.client.getRealSkillLevel(skill)
    } //    public int getHealth() {
    //    }
}
