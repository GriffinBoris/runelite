package net.runelite.client.plugins.alfred.api.models

import net.runelite.client.plugins.alfred.api.rs.equipment.RSEquipmentHelper

class DynamicEquipmentSet {
    private val itemsAndQuantities: MutableList<Triple<Int, Int, RSEquipmentHelper.Companion.EquipmentSlot>> = mutableListOf()

    fun addItemAndQuantity(itemID: Int, quantity: Int, equipmentSlot: RSEquipmentHelper.Companion.EquipmentSlot) {
        itemsAndQuantities.add(Triple(itemID, quantity, equipmentSlot))
    }

    fun getItems(): List<Triple<Int, Int, RSEquipmentHelper.Companion.EquipmentSlot>> {
        return itemsAndQuantities.reversed()
    }
}