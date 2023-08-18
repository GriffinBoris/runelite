package net.runelite.client.plugins.alfred.api.rs.equipment

import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.alfred.Alfred

class RSEquipmentHelper {
    //    child 2 empty
    //    child 1 equipped
    companion object {
        private const val HELMET_WIDGET_ID = 25362447
        private const val CAPE_WIDGET_ID = 25362448
        private const val NECKLACE_WIDGET_ID = 25362449
        private const val ARROW_WIDGET_ID = 25362457
        private const val WEAPON_WIDGET_ID = 25362450
        private const val CHEST_WIDGET_ID = 25362451
        private const val SHIELD_WIDGET_ID = 25362452
        private const val LEGS_WIDGET_ID = 25362453
        private const val GLOVES_WIDGET_ID = 25362454
        private const val BOOTS_WIDGET_ID = 25362455
        private const val RING_WIDGET_ID = 25362456

        enum class EquipmentSlot {
            HELMET,
            CAPE,
            NECKLACE,
            ARROW,
            WEAPON,
            CHEST,
            SHIELD,
            LEGS,
            GLOVES,
            BOOTS,
            RING,
        }
    }

    private fun clickSlot(slot: Int): Boolean {
        return Alfred.api.widgets().leftClickWidget(slot)
    }

    private fun getEquippedItem(slot: Int): Widget? {
        if (Alfred.api.tabs().currentTab != WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB) {
            Alfred.api.tabs().clickEquipmentTab()
        }
        val childOne = Alfred.api.widgets().getChildWidget(slot, 1)
        val childTwo = Alfred.api.widgets().getChildWidget(slot, 2)
        return if (!childTwo.isHidden()) {
            null
        } else childOne
    }


    private fun getEquippedItemId(slot: Int): Int {
        val item = getEquippedItem(slot) ?: return -1
        return item.getItemId()
    }

    private fun isEquipped(slot: Int): Boolean {
        return getEquippedItemId(slot) != -1
    }

    val isHelmetEquipped: Boolean
        get() = isEquipped(HELMET_WIDGET_ID)
    val isCapeEquipped: Boolean
        get() = isEquipped(CAPE_WIDGET_ID)
    val isNecklaceEquipped: Boolean
        get() = isEquipped(NECKLACE_WIDGET_ID)
    val isArrowEquipped: Boolean
        get() = isEquipped(ARROW_WIDGET_ID)
    val isWeaponEquipped: Boolean
        get() = isEquipped(WEAPON_WIDGET_ID)
    val isChestEquipped: Boolean
        get() = isEquipped(CHEST_WIDGET_ID)
    val isShieldEquipped: Boolean
        get() = isEquipped(SHIELD_WIDGET_ID)
    val isLegsEquipped: Boolean
        get() = isEquipped(LEGS_WIDGET_ID)
    val isGlovesEquipped: Boolean
        get() = isEquipped(GLOVES_WIDGET_ID)
    val isBootsEquipped: Boolean
        get() = isEquipped(BOOTS_WIDGET_ID)
    val isRingEquipped: Boolean
        get() = isEquipped(RING_WIDGET_ID)
    val helmetId: Int
        get() = getEquippedItemId(HELMET_WIDGET_ID)
    val capeId: Int
        get() = getEquippedItemId(CAPE_WIDGET_ID)
    val necklaceId: Int
        get() = getEquippedItemId(NECKLACE_WIDGET_ID)
    val arrowId: Int
        get() = getEquippedItemId(ARROW_WIDGET_ID)
    val weaponId: Int
        get() = getEquippedItemId(WEAPON_WIDGET_ID)
    val chestId: Int
        get() = getEquippedItemId(CHEST_WIDGET_ID)
    val shieldId: Int
        get() = getEquippedItemId(SHIELD_WIDGET_ID)
    val legsId: Int
        get() = getEquippedItemId(LEGS_WIDGET_ID)
    val glovesId: Int
        get() = getEquippedItemId(GLOVES_WIDGET_ID)
    val bootsId: Int
        get() = getEquippedItemId(BOOTS_WIDGET_ID)
    val ringId: Int
        get() = getEquippedItemId(RING_WIDGET_ID)

    fun getItemFromSlot(slot: EquipmentSlot): Widget? {
        return when (slot) {
            EquipmentSlot.HELMET -> getEquippedItem(HELMET_WIDGET_ID)
            EquipmentSlot.CAPE -> getEquippedItem(CAPE_WIDGET_ID)
            EquipmentSlot.NECKLACE -> getEquippedItem(NECKLACE_WIDGET_ID)
            EquipmentSlot.ARROW -> getEquippedItem(ARROW_WIDGET_ID)
            EquipmentSlot.WEAPON -> getEquippedItem(WEAPON_WIDGET_ID)
            EquipmentSlot.CHEST -> getEquippedItem(CHEST_WIDGET_ID)
            EquipmentSlot.SHIELD -> getEquippedItem(SHIELD_WIDGET_ID)
            EquipmentSlot.LEGS -> getEquippedItem(LEGS_WIDGET_ID)
            EquipmentSlot.GLOVES -> getEquippedItem(GLOVES_WIDGET_ID)
            EquipmentSlot.BOOTS -> getEquippedItem(BOOTS_WIDGET_ID)
            EquipmentSlot.RING -> getEquippedItem(RING_WIDGET_ID)
        }
    }
    fun clickHelmet(): Boolean {
        return clickSlot(HELMET_WIDGET_ID)
    }

    fun clickCape(): Boolean {
        return clickSlot(CAPE_WIDGET_ID)
    }

    fun clickNecklace(): Boolean {
        return clickSlot(NECKLACE_WIDGET_ID)
    }

    fun clickArrow(): Boolean {
        return clickSlot(ARROW_WIDGET_ID)
    }

    fun clickWeapon(): Boolean {
        return clickSlot(WEAPON_WIDGET_ID)
    }

    fun clickChest(): Boolean {
        return clickSlot(CHEST_WIDGET_ID)
    }

    fun clickShield(): Boolean {
        return clickSlot(SHIELD_WIDGET_ID)
    }

    fun clickLegs(): Boolean {
        return clickSlot(LEGS_WIDGET_ID)
    }

    fun clickGloves(): Boolean {
        return clickSlot(GLOVES_WIDGET_ID)
    }

    fun clickBoots(): Boolean {
        return clickSlot(BOOTS_WIDGET_ID)
    }

    fun clickRing(): Boolean {
        return clickSlot(RING_WIDGET_ID)
    }
}
