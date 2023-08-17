package net.runelite.client.plugins.alfred.api.task

import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.npc.RSNpc

class Fishing {

    fun findAndInteract(fishingSpotName: String, action: String): Boolean {
        val player = Alfred.api.players().localPlayer
        val nearestNpc = Alfred.api.npcs().npcs
            .filter { rsNpc: RSNpc -> rsNpc.name.equals(fishingSpotName, ignoreCase = true) }
            .sortedBy { rsNpc: RSNpc -> rsNpc.worldLocation.distanceTo(player.worldLocation) }
            .firstOrNull()

        nearestNpc ?: return false

        if (nearestNpc.worldLocation.distanceTo(player.worldLocation) >= 5) {
            val minimapPoint = Alfred.api.miniMap().getWorldPointToScreenPoint(nearestNpc.worldLocation)
            minimapPoint ?: return false

            Alfred.getMouse().leftClick(minimapPoint)
            Alfred.sleep(1000)
            Alfred.sleepUntil({
                !player.isMoving && !player.isInteracting && player.isIdle
            }, 200, 1000 * 30)
        }

        if (!Alfred.api.screen().isPointOnScreen(nearestNpc.localLocation, nearestNpc.worldLocation.plane)) {
            Alfred.api.camera().lookAt(nearestNpc.worldLocation)
        }

        val success = nearestNpc.interact(action)
        if (!success) {
            return false
        }

        Alfred.sleepUntil(player::isAnimating, 100, 1000 * 10)
        Alfred.setStatus("Waiting to finish fishing")
        return Alfred.sleepUntil({ !player.isMoving && player.isIdle && !player.isAnimating }, 100, 1000 * 60 * 5)
    }
}