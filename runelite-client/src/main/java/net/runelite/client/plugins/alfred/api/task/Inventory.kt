package net.runelite.client.plugins.alfred.api.task

import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.models.DynamicItemSet
import net.runelite.client.plugins.alfred.api.models.inventory.InventoryRequirements

class Inventory {

    fun fetchInventoryRequirements(inventoryRequirements: InventoryRequirements) {
        if (Alfred.api.banks.isClosed) {
            throw Exception("Cannot fetch inventory requirements, bank is not open.")
        }

        inventoryRequirements.getItemSets().forEach { dynamicItemSet: DynamicItemSet ->
            for (itemAndQuantityPair: Pair<Int, Int> in dynamicItemSet.getItems()) {
                if (itemAndQuantityPair.second == 0) {
                    continue
                }

                if (!Alfred.api.banks.containsItem(itemAndQuantityPair.first)) {
                    continue
                }

                val countBefore = Alfred.api.inventory.getItems(itemAndQuantityPair.first).count()

                var searched = false
                val rsInventoryItem = Alfred.api.banks.findItem(itemAndQuantityPair.first)
                if (!Alfred.api.banks.isItemVisible(rsInventoryItem!!.id)) {
                    Alfred.api.banks.search(rsInventoryItem.name.lowercase())
                    Alfred.sleep(200, 400)
                    searched = true
                }

                var success = if (itemAndQuantityPair.second == 1) {
                    val item = Alfred.api.banks.findItem(itemAndQuantityPair.first)
                    item!!.leftClick()

                } else {
                    Alfred.api.banks.withdrawX(itemAndQuantityPair.first, itemAndQuantityPair.second)
                }

                if (searched) {
                    Alfred.api.banks.search()
                    Alfred.sleep(200, 400)
                }

                if (!success) {
                    throw Exception("Failed to withdraw item ${itemAndQuantityPair.first}.")
                }
                success = Alfred.sleepUntil({
                    val meetsCountItems = Alfred.api.banks.getInventoryItems(itemAndQuantityPair.first).count() == countBefore + itemAndQuantityPair.second
                    val meetsCountQuantity = Alfred.api.banks.getInventoryItems(itemAndQuantityPair.first).firstOrNull()?.quantity == itemAndQuantityPair.second
                    return@sleepUntil meetsCountItems || meetsCountQuantity
                }, 100, 2000)

                if (!success) {
                    throw Exception("Failed to withdraw all required items ${itemAndQuantityPair.first}.")
                }

                break
            }
        }
    }
}