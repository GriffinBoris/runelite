package net.runelite.client.plugins.alfred.api.models.equipment

import net.runelite.api.widgets.Widget
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.models.DynamicEquipmentSet
import net.runelite.client.plugins.alfred.api.rs.equipment.RSEquipmentHelper

class EquipmentRequirements {

    val itemSets: MutableList<DynamicEquipmentSet> = mutableListOf()

    fun addItemSet(itemSet: DynamicEquipmentSet) {
        itemSets.add(itemSet)
    }

    fun checkMeetsRequirements(): Boolean {
        Alfred.api.tabs().clickEquipmentTab()
        itemSets.forEach { dynamicItemSet: DynamicEquipmentSet ->
            var meetsRequirement = false
            dynamicItemSet.getItems().forEach { equipmentTriple: Triple<Int, Int, RSEquipmentHelper.Companion.EquipmentSlot> ->
                val item: Widget? = Alfred.api.equipment().getItemFromSlot(equipmentTriple.third)
                if (item != null) {
                    if (item.itemId == equipmentTriple.first && item.itemQuantity >= equipmentTriple.second) {
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