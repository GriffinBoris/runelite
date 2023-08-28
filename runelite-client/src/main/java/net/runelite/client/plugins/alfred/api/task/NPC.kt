package net.runelite.client.plugins.alfred.api.task

import net.runelite.api.NpcID
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.npc.RSNpc

class NPC {

    private fun internalFindAndInteract(npc: RSNpc, action: String): Boolean {
        val player = Alfred.api.players.localPlayer
        if (npc.worldLocation.distanceTo(player.worldLocation) >= 3) {
            Alfred.api.walk.walkTo(npc.worldLocation)
        }

        if (!Alfred.api.screen.isPointOnScreen(npc.localLocation, npc.worldLocation.plane)) {
            Alfred.api.camera.lookAt(npc.worldLocation)
        }

        if (!npc.hasAction(action)) {
            Alfred.mouse.rightClick(npc.clickBox)
            Alfred.sleep(200, 600)

            val menu = Alfred.api.menu.menu
            if (menu.hasAction(action)) {
                return menu.clickAction(action)
            }

            return false
        }

        return npc.interact(action)
    }

    fun findAndInteract(npcName: String, action: String): Boolean {
        val player = Alfred.api.players.localPlayer
        val nearestNpc = Alfred.api.npcs.npcs
            .filter { rsNpc: RSNpc -> rsNpc.name.equals(npcName, ignoreCase = true) }
            .sortedBy { rsNpc: RSNpc -> rsNpc.worldLocation.distanceTo(player.worldLocation) }
            .firstOrNull()

        nearestNpc ?: return false
        return internalFindAndInteract(nearestNpc, action)
    }

    fun findAndInteract(npcID: Int, action: String): Boolean {
        val player = Alfred.api.players.localPlayer
        val nearestNpc = Alfred.api.npcs.npcs
            .filter { rsNpc: RSNpc -> rsNpc.id == npcID }
            .sortedBy { rsNpc: RSNpc -> rsNpc.worldLocation.distanceTo(player.worldLocation) }
            .firstOrNull()

        nearestNpc ?: return false
        return internalFindAndInteract(nearestNpc, action)
    }

    fun findAndAttack(npcName: String): Boolean {
        val player = Alfred.api.players.localPlayer
        val nearestNpc = Alfred.api.npcs.npcs
            .filter { rsNpc: RSNpc -> rsNpc.name.equals(npcName, ignoreCase = true) }
            .sortedBy { rsNpc: RSNpc -> rsNpc.worldLocation.distanceTo(player.worldLocation) }
            .firstOrNull()

        nearestNpc ?: return false

        if (nearestNpc.worldLocation.distanceTo(player.worldLocation) >= 3) {
            Alfred.api.walk.walkTo(nearestNpc.worldLocation)
        }

        if (!Alfred.api.screen.isPointOnScreen(nearestNpc.localLocation, nearestNpc.worldLocation.plane)) {
            Alfred.api.camera.lookAt(nearestNpc.worldLocation)
        }

        if (nearestNpc.interact("attack")) {
//            Alfred.sleepUntil(player::isAnimating, 100, 2000)
            if (!Alfred.sleepUntil({ player.isInteracting }, 100, 3000)) {
                return false
            }

            Alfred.status = "Waiting to finish attacking"
            Alfred.sleepUntil({ !player.isMoving && player.isIdle }, 200, 1000 * 10)
            Alfred.sleepUntil({ nearestNpc.isDead || player.isDead }, 200, 1000 * 90)
            Alfred.sleep(3000, 3500)
            return true
        }

        return false
    }
}