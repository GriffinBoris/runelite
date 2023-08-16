package net.runelite.client.plugins.alfred.api.task

import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.objects.RSObject

class Woodcutting {
    fun findAndChopTree(treeName: String): Boolean {
        val player = Alfred.api.players().localPlayer
        val objects = Alfred.api.objects().getObjectsFromTiles(treeName)

        if (objects.isEmpty()) {
            Alfred.setStatus("No ${treeName} trees found")
            return false;
        }

        val nearestTree = objects.minBy { rsObject: RSObject -> rsObject.worldLocation.distanceTo(player.worldLocation) }
        if (nearestTree.worldLocation.distanceTo(player.worldLocation) >= 2) {
            Alfred.setStatus("Walking to nearest ${treeName} tree")

            if (!Alfred.api.screen().isPointOnScreen(nearestTree.localLocation, nearestTree.plane)) {
                Alfred.api.walk().walkTo(nearestTree.worldLocation)
            }
        }

        Alfred.api.camera().lookAt(nearestTree.worldLocation)
        val success = nearestTree.clickAction("chop down")
        if (!success) {
            return false
        }

        Alfred.sleepUntil(player::isAnimating, 100, 1000 * 10)
        Alfred.setStatus("Waiting to finish chopping down ${treeName} tree")
        return Alfred.sleepUntil({ !player.isMoving && player.isIdle && !player.isAnimating }, 100, 1000 * 90)
    }
}