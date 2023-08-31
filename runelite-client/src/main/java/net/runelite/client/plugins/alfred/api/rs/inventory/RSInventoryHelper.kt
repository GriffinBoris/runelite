package net.runelite.client.plugins.alfred.api.rs.inventory

import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.alfred.Alfred

class RSInventoryHelper {
    private val INVENTORY_CONTAINER_ID = 9764864
    val isOpen: Boolean
        //    private final int UNKNOWN_WIDGET_ID = 786433;
        get() = Alfred.api.tabs.currentTab == WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB

    //        Widget widget = Alfred.Companion.getApi().getWidgets().getWidget(UNKNOWN_WIDGET_ID);
//        Alfred.Companion.getApi().getTabs().getCurrentTab() == WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB;
//        if (widget == null) {
//            return false;
//        }
//
//        return !widget.isHidden() && !widget.isSelfHidden();
    fun open() {
        if (!isOpen) {
            Alfred.api.tabs.clickInventoryTab()
            Alfred.sleepUntil({ isOpen }, 100, 2000)
        }
    }

    fun count(): Int {
        return Alfred.clientThread.invokeOnClientThread {
            open()
            val inventoryWidget = Alfred.api.widgets.getWidget(INVENTORY_CONTAINER_ID)
            return@invokeOnClientThread inventoryWidget!!.dynamicChildren.filterNotNull()
                .filter { widget: Widget -> !widget.isHidden && !widget.isSelfHidden }
                .count { widget: Widget -> widget.name.isNotEmpty() && widget.name.isNotBlank() }
        }
    }

    val isFull: Boolean
        get() = count() == 28
    val isEmpty: Boolean
        get() = count() == 0

    private fun internalClickSlot(slot: Int, leftClick: Boolean): Boolean {
        val correctSlot = slot - 1
        open()
        val slotWidget = Alfred.api.widgets.getChildWidget(INVENTORY_CONTAINER_ID, correctSlot) ?: return false
        if (leftClick) {
            Alfred.mouse.leftClick(slotWidget.getBounds())
        } else {
            Alfred.mouse.rightClick(slotWidget.getBounds())
        }
        return true
    }

    fun leftClickSlot(slot: Int): Boolean {
        return internalClickSlot(slot, true)
    }

    fun rightClickSlot(slot: Int): Boolean {
        return internalClickSlot(slot, false)
    }

    fun getItemFromSlot(slot: Int): RSInventoryItem? {
        val correctSlot = slot - 1
        val item = Alfred.api.widgets.getChildWidget(INVENTORY_CONTAINER_ID, correctSlot) ?: return null
        return RSInventoryItem(item)
    }

    private fun internalGetItems(): List<RSInventoryItem> {
        return Alfred.clientThread.invokeOnClientThread {
            open()
            val inventoryWidget = Alfred.api.widgets.getWidget(INVENTORY_CONTAINER_ID)
            return@invokeOnClientThread inventoryWidget!!.dynamicChildren.filterNotNull().map { widget: Widget -> RSInventoryItem(widget) }
        }
    }

    val items: List<RSInventoryItem?>
        get() = internalGetItems()

    fun getItems(itemID: Int): List<RSInventoryItem> {
        return internalGetItems().filter { rsInventoryItem: RSInventoryItem -> rsInventoryItem.id == itemID }
    }

    fun containsItem(itemID: Int): Boolean {
        return internalGetItems().any { rsInventoryItem: RSInventoryItem -> rsInventoryItem.id == itemID }
    }

    private fun internalDrop(rsInventoryItem: RSInventoryItem): Boolean {
        val currentCount = count()
        val success = rsInventoryItem.interact("drop")

        return if (!success) {
            false
        } else Alfred.sleepUntil({ count() == currentCount - 1 }, 100, 5000)
    }

    private fun internalDropAll(rsInventoryItems: List<RSInventoryItem>): Boolean {
        rsInventoryItems.forEach { rsInventoryItem ->
            internalDrop(rsInventoryItem)
        }
        return true
    }

    private fun internalDropSlot(slot: Int): Boolean {
        if (slot < 1 || slot > 28) {
            return false
        }
        if (isEmpty) {
            return true
        }
        val rsInventoryItem = getItemFromSlot(slot) ?: return false
        return internalDrop(rsInventoryItem)
    }

    fun drop(itemId: Int): Boolean {
        val rsInventoryItem = Alfred.api.inventory.getItems(itemId).firstOrNull() ?: return false
        return internalDrop(rsInventoryItem)
    }

    fun dropFirst(itemId: Int): Boolean {
        val rsInventoryItem = getItems(itemId).firstOrNull() ?: return false
        return internalDrop(rsInventoryItem)
    }

    fun dropAll(itemId: Int): Boolean {
        return internalDropAll(getItems(itemId))
    }

    fun dropSlot(slot: Int): Boolean {
        return internalDropSlot(slot)
    }

    fun dropSlots(vararg slots: Int): Boolean {
        for (slot in slots) {
            internalDropSlot(slot)
        }
        return true
    }

    fun dropAllBetween(start: Int, end: Int): Boolean {
        for (i in start..end) {
            internalDropSlot(i)
        }
        return true
    }

    private fun internalInteract(rsInventoryItem: RSInventoryItem, action: String): Boolean {
        return if (!rsInventoryItem.inventoryActions.contains(action)) {
            false
        } else rsInventoryItem.interact(action)
    }

    fun interactFirst(itemID: Int, action: String): Boolean {
        val rsInventoryItem = getItems(itemID).firstOrNull() ?: return false
        return internalInteract(rsInventoryItem, action)
    }

    private fun internalInteractAll(rsInventoryItem: RSInventoryItem, action: String): Boolean {
        val rsInventoryItems = getItems(rsInventoryItem.id)
        rsInventoryItems.forEach { item ->
            internalInteract(rsInventoryItem, action)
            Alfred.sleep(75, 150)
        }
        return true
    }

    fun interactAll(itemId: Int, action: String): Boolean {
        val rsInventoryItem = getItems(itemId).firstOrNull() ?: return false
        return internalInteractAll(rsInventoryItem, action)
    }
}
