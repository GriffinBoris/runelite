package net.runelite.client.plugins.alfred.api.models.inventory

import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.models.DynamicItemSet

class InventoryRequirements {

    val itemSets: MutableList<DynamicItemSet> = mutableListOf()

    fun addItemSet(itemSet: DynamicItemSet) {
        itemSets.add(itemSet)
    }

    fun checkMeetsRequirements(): Boolean {
        Alfred.api.inventory().open()
        itemSets.forEach { dynamicItemSet: DynamicItemSet ->
            var meetsRequirement = false
            dynamicItemSet.getItems().forEach { itemAndQuantityPair: Pair<Int, Int> ->
                if (Alfred.api.inventory().containsItem(itemAndQuantityPair.first)) {
                    if (Alfred.api.inventory().getItems(itemAndQuantityPair.first).count() >= itemAndQuantityPair.second) {
                        meetsRequirement = true
                    }
                }
            }

            if (!meetsRequirement) {
                return false
            }
        }

        return true
    }
}