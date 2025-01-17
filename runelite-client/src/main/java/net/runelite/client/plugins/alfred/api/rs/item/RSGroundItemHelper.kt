package net.runelite.client.plugins.alfred.api.rs.item

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred

class RSGroundItemHelper {
    private fun internalGetGroundItems(): List<RSGroundItem> {
        return Alfred.api.objects.itemsFromTiles
    }

    fun getItemsFromTiles(id: Int): List<RSGroundItem> {
        return internalGetGroundItems().filter { rsGroundItem: RSGroundItem -> rsGroundItem.id == id }
    }

    fun getItemsFromTiles(radius: Int, id: Int): List<RSGroundItem> {
        val playerLocation = Alfred.api.players.localPlayer.worldLocation
        return internalGetGroundItems()
            .filter { rsGroundItem: RSGroundItem -> rsGroundItem.id == id }
            .filter { rsGroundItem: RSGroundItem -> rsGroundItem.worldLocation.distanceTo(playerLocation) <= radius }
            .sortedBy { rsGroundItem: RSGroundItem -> rsGroundItem.worldLocation.distanceTo(playerLocation) }
    }

    fun isItemOnGround(id: Int, worldPoint: WorldPoint): Boolean {
        return internalGetGroundItems().any { rsGroundItem: RSGroundItem -> rsGroundItem.id == id && rsGroundItem.worldLocation == worldPoint }
    }
}
