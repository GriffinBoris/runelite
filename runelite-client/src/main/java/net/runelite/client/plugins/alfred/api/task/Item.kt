package net.runelite.client.plugins.alfred.api.task

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.item.RSGroundItem

class Item {

    fun findAndLootItems(itemIds: List<Int>, radius: Int): Boolean {
        val player = Alfred.api.players().localPlayer
        val foundItems: MutableList<RSGroundItem> = mutableListOf()

        for (itemId in itemIds) {
            foundItems.addAll(Alfred.api.items().getItemsFromTiles(radius, itemId))
        }

        if (foundItems.isEmpty()) {
            return false
        }

        val groupedItems: Map<WorldPoint, List<RSGroundItem>> = foundItems.groupBy { rsGroundItem: RSGroundItem -> rsGroundItem.worldLocation }


        groupedItems.forEach { entry: Map.Entry<WorldPoint, List<RSGroundItem>> ->
            val firstItem = entry.value.get(0)
            val standingOnItem = player.worldLocation.equals(entry.key)

            if (!standingOnItem) {
                Alfred.api.camera().lookAt(firstItem.worldLocation)
            }

            entry.value.forEach { rsGroundItem: RSGroundItem ->
                if (Alfred.api.inventory().isFull) {
                    return true
                }

                val inventoryCount = Alfred.api.inventory().count()
                if (rsGroundItem.clickAction("take")) {
                    if (!standingOnItem) {
                        Alfred.sleepUntil({ !player.isMoving }, 50, 3000)
                    }

                    Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 100, 1000 * 10)
                    Alfred.sleepUntil({ Alfred.api.items().isItemOnGround(rsGroundItem.id, rsGroundItem.worldLocation) }, 100, 1000 * 5)
                    Alfred.sleepUntil({ Alfred.api.inventory().count() == inventoryCount + 1 }, 100, 1000 * 5)
                }
            }
        }

        return true
    }
}