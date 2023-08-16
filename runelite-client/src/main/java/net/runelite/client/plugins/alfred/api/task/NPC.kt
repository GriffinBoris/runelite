package net.runelite.client.plugins.alfred.api.task

import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.npc.RSNpc

class NPC {

    fun findAndInteract(npcName: String, action: String): Boolean {
        val player = Alfred.api.players().localPlayer
        val nearestNpc = Alfred.api.npcs().npcs
            .filter { rsNpc: RSNpc -> rsNpc.name.equals(npcName, ignoreCase = true) }
            .sortedBy { rsNpc: RSNpc -> rsNpc.worldLocation.distanceTo(player.worldLocation) }
            .firstOrNull()

        nearestNpc ?: return false

        if (nearestNpc.worldLocation.distanceTo(player.worldLocation) >= 3) {
            Alfred.api.walk().walkTo(nearestNpc.worldLocation)
        }

        if (!Alfred.api.screen().isPointOnScreen(nearestNpc.localLocation, nearestNpc.worldLocation.plane)) {
            Alfred.api.camera().lookAt(nearestNpc.worldLocation)
        }

        return nearestNpc.interact(action)
    }

    fun findAndAttack(npcName: String): Boolean {
        val player = Alfred.api.players().localPlayer
        val nearestNpc = Alfred.api.npcs().npcs
            .filter { rsNpc: RSNpc -> rsNpc.name.equals(npcName, ignoreCase = true) }
            .sortedBy { rsNpc: RSNpc -> rsNpc.worldLocation.distanceTo(player.worldLocation) }
            .firstOrNull()

        nearestNpc ?: return false

        if (nearestNpc.worldLocation.distanceTo(player.worldLocation) >= 3) {
            Alfred.api.walk().walkTo(nearestNpc.worldLocation)
        }

        if (!Alfred.api.screen().isPointOnScreen(nearestNpc.localLocation, nearestNpc.worldLocation.plane)) {
            Alfred.api.camera().lookAt(nearestNpc.worldLocation)
        }

        if (nearestNpc.interact("attack")) {
//            Alfred.sleepUntil(player::isAnimating, 100, 2000)
            if (!Alfred.sleepUntil({ player.isInteracting }, 100, 3000)) {
                return false
            }

            Alfred.setStatus("Waiting to finish attacking")
            Alfred.sleepUntil({ !player.isMoving && player.isIdle }, 200, 1000 * 10)
            Alfred.sleepUntil({ nearestNpc.isDead || player.isDead }, 200, 1000 * 90)
            Alfred.sleep(3000, 3500)
            return true
        }

        return false
    }
}