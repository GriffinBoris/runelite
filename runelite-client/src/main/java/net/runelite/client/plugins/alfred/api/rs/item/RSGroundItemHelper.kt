package net.runelite.client.plugins.alfred.api.rs.item

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred

class RSGroundItemHelper {
    private fun internalGetGroundItems(): List<RSGroundItem> {
        return Alfred.api.objects().getItemsFromTiles()
    }

    fun getItemsFromTiles(name: String): List<RSGroundItem> {
        return internalGetGroundItems().filter { rsGroundItem: RSGroundItem -> rsGroundItem.name.equals(name, ignoreCase = true) }
    }

    fun getItemsFromTiles(id: Int): List<RSGroundItem> {
        return internalGetGroundItems().filter { rsGroundItem: RSGroundItem -> rsGroundItem.id == id }
    }

    fun getItemsFromTiles(radius: Int, name: String): List<RSGroundItem> {
        val playerLocation = Alfred.api.players().localPlayer.worldLocation
        return getItemsFromTiles(name)
            .filter { rsGroundItem: RSGroundItem -> rsGroundItem.worldLocation.distanceTo(playerLocation) <= radius }
            .sortedBy { rsGroundItem: RSGroundItem -> rsGroundItem.worldLocation.distanceTo(playerLocation) }
    }

    fun getItemsFromTiles(radius: Int, id: Int): List<RSGroundItem> {
        val playerLocation = Alfred.api.players().localPlayer.worldLocation
        return internalGetGroundItems()
            .filter { rsGroundItem: RSGroundItem -> rsGroundItem.id == id }
            .filter { rsGroundItem: RSGroundItem -> rsGroundItem.worldLocation.distanceTo(playerLocation) <= radius }
            .sortedBy { rsGroundItem: RSGroundItem -> rsGroundItem.worldLocation.distanceTo(playerLocation) }
    }

    fun isItemOnGround(id: Int, worldPoint: WorldPoint): Boolean {
        return internalGetGroundItems().filter { rsGroundItem: RSGroundItem -> rsGroundItem.id == id && rsGroundItem.worldLocation == worldPoint }.isNotEmpty()
    }
}
