package net.runelite.client.plugins.alfred.api.models.inventory

import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.models.DynamicItemSet

class InventoryRequirements {

    private val itemSets: MutableList<DynamicItemSet> = mutableListOf()

    fun getItemSets(): List<DynamicItemSet> {
        return itemSets.reversed()
    }

    fun addItemSet(itemSet: DynamicItemSet) {
        itemSets.add(itemSet)
    }

    fun checkMeetsRequirements(): Boolean {
        Alfred.api.inventory.open()
        itemSets.forEach { dynamicItemSet: DynamicItemSet ->
            var meetsRequirement = false
            dynamicItemSet.getItems().forEach { itemAndQuantityPair: Pair<Int, Int> ->
                if (Alfred.api.inventory.containsItem(itemAndQuantityPair.first)) {
                    if (Alfred.api.inventory.getItems(itemAndQuantityPair.first).count() >= itemAndQuantityPair.second) {
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