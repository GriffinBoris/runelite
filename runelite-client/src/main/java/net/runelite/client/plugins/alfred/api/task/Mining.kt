package net.runelite.client.plugins.alfred.api.task

import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.objects.RSObject

class Mining {

    fun findAndMineOre(oreName: String): Boolean {
        val player = Alfred.api.players.localPlayer
        val objects = Alfred.api.objects.getObjectsFromTiles(oreName)

        if (objects.isEmpty()) {
            Alfred.status = "No ${oreName} ores found"
            return false
        }

        val nearestOre = objects.minBy { rsObject: RSObject -> rsObject.worldLocation!!.distanceTo(player.worldLocation) }
        if (nearestOre.worldLocation!!.distanceTo(player.worldLocation) >= 2) {
            Alfred.status = "Walking to nearest ${oreName} ore"
            if (!Alfred.api.screen.isPointOnScreen(nearestOre.localLocation, nearestOre.plane)) {
                Alfred.api.walk.walkTo(nearestOre.worldLocation)
            }
        }

        if (!Alfred.api.screen.isPointOnScreen(nearestOre.localLocation, nearestOre.plane)) {
            Alfred.api.camera.lookAt(nearestOre.worldLocation!!)
        }
            
        val success = nearestOre.leftClick()
        if (!success) {
            return false
        }

        Alfred.sleepUntil(player::isAnimating, 100, 1000 * 3)
        Alfred.status = "Waiting to finish mining ${oreName} ore"
        return Alfred.sleepUntil({ !player.isMoving && player.isIdle && !player.isAnimating }, 100, 1000 * 90)
    }
}