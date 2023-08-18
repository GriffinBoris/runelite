package net.runelite.client.plugins.alfred.api.task

import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.models.DynamicItemSet
import net.runelite.client.plugins.alfred.api.models.inventory.InventoryRequirements
import java.lang.Exception

class Inventory {

    fun fetchInventoryRequirements(inventoryRequirements: InventoryRequirements) {
        if (Alfred.api.banks().isClosed) {
            throw Exception("Cannot fetch inventory requirements, bank is not open.")
        }

        inventoryRequirements.itemSets.forEach { dynamicItemSet: DynamicItemSet ->
            for (itemAndQuantityPair: Pair<Int, Int> in dynamicItemSet.getItems()) {
                if (itemAndQuantityPair.second == 0) {
                    continue
                }

                if (!Alfred.api.banks().containsItem(itemAndQuantityPair.first)) {
                    continue
                }

                val countBefore = Alfred.api.inventory().getItems(itemAndQuantityPair.first).count()

                var success = if (itemAndQuantityPair.second == 1) {
                    Alfred.api.banks().withdrawItem(itemAndQuantityPair.first)
                } else {
                    Alfred.api.banks().withdrawX(itemAndQuantityPair.first, itemAndQuantityPair.second)
                }

                if (!success) {
                    throw Exception("Failed to withdraw item.")
                }

                success = Alfred.sleepUntil({Alfred.api.inventory().getItems(itemAndQuantityPair.first).count() == countBefore + itemAndQuantityPair.second}, 100, 2000)

                if (!success) {
                    throw Exception("Failed to withdraw all required items.")
                }

                break
            }
        }
    }
}