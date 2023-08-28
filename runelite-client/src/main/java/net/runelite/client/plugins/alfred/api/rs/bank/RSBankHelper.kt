package net.runelite.client.plugins.alfred.api.rs.bank

import net.runelite.api.GameObject
import net.runelite.api.ObjectID
import net.runelite.api.Varbits
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.inventory.RSInventoryItem
import net.runelite.client.plugins.alfred.api.rs.objects.RSObject
import java.util.*
import java.util.stream.Collectors

class RSBankHelper {
    companion object {
        private const val WIDGET_REARRANGE_SWAP = 786450
        private const val WIDGET_REARRANGE_INSERT = 786452
        private const val VARBIT_WITHDRAW_AS = 3958
    }

    private val bankBoothObjectIDs: MutableList<Int> = mutableListOf()

    init {
        loadBankBoothObjectIDs()
    }

    private fun loadBankBoothObjectIDs() {
        val fields = ObjectID::class.java.getDeclaredFields()
        for (field in fields) {
            field.setAccessible(true)
            if (field.type == Int::class.javaPrimitiveType) {
                try {
                    if (field.name.contains("BANK_BOOTH")) {
                        if (field.getInt(null) == 10527) {
                            continue
                        }
                        bankBoothObjectIDs.add(field.getInt(null))
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun getNearestBanks(): List<RSBank> {
        return Alfred.clientThread.invokeOnClientThread {
            val player = Alfred.api.players.localPlayer

            return@invokeOnClientThread Alfred.api.objects.objectsFromTiles.filterNotNull()
                .filter { rsObject: RSObject -> bankBoothObjectIDs.contains(rsObject.id) }
                .map { rsObject: RSObject -> RSBank(rsObject.rsObject as GameObject) }
                .sortedBy { rsBank: RSBank -> rsBank.worldLocation.distanceTo(player.worldLocation) }
        }
    }

    fun open(bank: RSBank): Boolean {
        Alfred.status = "Opening bank"
        if (bank.worldLocation == null) {
            println("Could not find bank world location")
            return false
        }

        if (bank.clickbox == null) {
            println("Could not find bank clickbox")
            return false
        }
        if (bank.clickbox!!.bounds == null) {
            println("Could not find bank clickbox bounds")
            return false
        }
        if (isOpen) {
            return false
        }
        Alfred.mouse.rightClick(bank.clickbox!!.bounds)
        if (!Alfred.sleepUntil({ Alfred.api.menu.menu.hasAction("bank") }, 200, 2000)) {
            println("Could not find bank action")
            return false
        }
        Alfred.api.menu.menu.clickAction("bank")
        if (!Alfred.sleepUntil({ Alfred.api.banks.isOpen }, 200, 1000 * 10)) {
            println("Could not open bank")
            return false
        }
        Alfred.sleep(250, 750)
        Alfred.status = "Opened bank"
        return true
    }

    val isOpen: Boolean
        get() {
            val widget = Alfred.api.widgets.getWidget(WidgetInfo.BANK_CONTAINER) ?: return false
            return !widget.isHidden() && !widget.isSelfHidden()
        }
    val isClosed: Boolean
        get() {
            val widget = Alfred.api.widgets.getWidget(WidgetInfo.BANK_CONTAINER)
            return widget == null
        }

    fun close(): Boolean {
        Alfred.status = "Closing bank"
        val widget = Alfred.api.widgets.getChildWidget(786434, 11)
        if (widget == null) {
            println("Could not find bank close button widget")
            return false
        }
        Alfred.mouse.leftClick(widget.getBounds())
        if (!Alfred.sleepUntil({ !isOpen }, 200, 1000 * 10)) {
            println("Could not close bank")
            return false
        }
        Alfred.status = "Closed bank"
        return true
    }

    fun viewAllItems(): Boolean {
        val widget = Alfred.api.widgets.getWidget(WidgetInfo.BANK_TAB_CONTAINER)
        if (widget == null) {
            println("Could not find bank container widget")
            return false
        }
        Alfred.mouse.leftClick(widget.getBounds())
        return true
    }

    val isRearrangeSwapSelected: Boolean
        get() = Alfred.client.getVarbitValue(Varbits.BANK_REARRANGE_MODE) == 0
    val isRearrangeInsertSelected: Boolean
        get() = Alfred.client.getVarbitValue(Varbits.BANK_REARRANGE_MODE) == 0

    fun rearrangeSwap(): Boolean {
        val widget = Alfred.api.widgets.getWidget(WIDGET_REARRANGE_SWAP)
        if (widget == null) {
            println("Could not find bank withdraw as item widget")
            return false
        }
        Alfred.mouse.leftClick(widget.getBounds())
        return true
    }

    fun rearrangeInsert(): Boolean {
        val widget = Alfred.api.widgets.getWidget(WIDGET_REARRANGE_INSERT)
        if (widget == null) {
            println("Could not find bank withdraw as note widget")
            return false
        }
        Alfred.mouse.leftClick(widget.getBounds())
        return true
    }

    val isWithdrawAsItemSelected: Boolean
        get() = Alfred.client.getVarbitValue(VARBIT_WITHDRAW_AS) == 0
    val isWithdrawAsNoteSelected: Boolean
        get() = Alfred.client.getVarbitValue(VARBIT_WITHDRAW_AS) == 1

    fun withdrawAsItem(): Boolean {
        val widget = Alfred.api.widgets.getWidget(786454)
        if (widget == null) {
            println("Could not find bank withdraw as item widget")
            return false
        }
        Alfred.mouse.leftClick(widget.getBounds())
        return true
    }

    fun withdrawAsNote(): Boolean {
        val widget = Alfred.api.widgets.getWidget(786456)
        if (widget == null) {
            println("Could not find bank withdraw as note widget")
            return false
        }
        Alfred.mouse.leftClick(widget.getBounds())
        return true
    }

    fun search(): Boolean {
        val widget = Alfred.api.widgets.getWidget(WidgetInfo.BANK_SEARCH_BUTTON_BACKGROUND)
        if (widget == null) {
            println("Could not find bank search widget")
            return false
        }
        Alfred.mouse.leftClick(widget.getBounds())
        return true
    }

    fun search(searchValue: String?): Boolean {
        if (!search()) {
            return false
        }
        val success = Alfred.sleepUntil({ Alfred.api.widgets.getWidget(WidgetInfo.CHATBOX_TITLE) != null }, 100, 2000)
        if (!success) {
            return false
        }
        val widget = Alfred.api.widgets.getWidget(WidgetInfo.CHATBOX_TITLE)
        val searchBoxShown = Alfred.sleepUntil({
            if (widget.isHidden() || widget.isSelfHidden()) {
                return@sleepUntil false
            }
            widget.getText().lowercase(Locale.getDefault()).contains("show items whose names contain the following text")
        }, 100, 3000)
        if (!searchBoxShown) {
            return false
        }
        Alfred.keyboard.sendKeys(searchValue)
        Alfred.sleep(75, 125)
        Alfred.keyboard.pressEnter()
        return true
    }

    fun depositInventory(): Boolean {
        val widget = Alfred.api.widgets.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY)
        if (widget == null) {
            println("Could not find bank deposit inventory widget")
            return false
        }
        Alfred.mouse.leftClick(widget.getBounds())
        return Alfred.sleepUntil({ Alfred.api.inventory.isEmpty }, 100, 2000)
    }

    fun depositEquipment(): Boolean {
        val widget = Alfred.api.widgets.getWidget(WidgetInfo.BANK_DEPOSIT_EQUIPMENT)
        if (widget == null) {
            println("Could not find bank deposit equipment widget")
            return false
        }
        Alfred.mouse.leftClick(widget.getBounds())
        return true
    }

    val items: List<RSInventoryItem?>
        get() = Alfred.clientThread.invokeOnClientThread {
            val itemContainer = Alfred.api.widgets.getWidget(WidgetInfo.BANK_ITEM_CONTAINER)
            Arrays.stream(itemContainer.getDynamicChildren()).map { item: Widget? -> RSInventoryItem(item!!) }.collect(Collectors.toList())
        }
    val inventoryItems: List<RSInventoryItem?>
        get() = Alfred.clientThread.invokeOnClientThread {
            val itemContainer = Alfred.api.widgets.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER)
            Arrays.stream(itemContainer.getDynamicChildren()).map { item: Widget? -> RSInventoryItem(item!!) }.collect(Collectors.toList())
        }

    fun getInventoryItems(itemId: Int): List<RSInventoryItem?> {
        return inventoryItems.stream().filter { item: RSInventoryItem? -> item!!.id == itemId }.collect(Collectors.toList())
    }

    fun findItem(itemId: Int): RSInventoryItem? {
        return items.stream().filter { item: RSInventoryItem? -> item!!.id == itemId }.findFirst().orElse(null)
    }

    fun findInventoryItem(itemId: Int): RSInventoryItem? {
        return inventoryItems.stream().filter { item: RSInventoryItem? -> item!!.id == itemId }.findFirst().orElse(null)
    }

    fun internalDepositAll(itemId: Int): Boolean {
        val rsInventoryItem = Alfred.api.banks.findInventoryItem(itemId) ?: return false
        return rsInventoryItem.interact("deposit-all")
    }

    fun depositAll(itemId: Int): Boolean {
        return internalDepositAll(itemId)
    }

    private fun internalWithdrawItem(itemId: Int, action: String): Boolean {
        val foundItem = findItem(itemId)
        if (foundItem == null) {
            println("Could not find item in bank")
            return false
        }
        val success = foundItem.interact(action)
        if (success) {
            Alfred.sleep(150, 350)
        }
        return success
    }

    private fun internalWithdrawX(amount: Int): Boolean {
        val widget = Alfred.api.widgets.getWidget(WidgetInfo.CHATBOX_TITLE)
        val searchBoxShown = Alfred.sleepUntil({
            if (widget.isHidden() || widget.isSelfHidden()) {
                return@sleepUntil false
            }
            widget.getText().lowercase(Locale.getDefault()).contains("enter amount")
        }, 100, 3000)
        if (!searchBoxShown) {
            return false
        }
        Alfred.keyboard.sendKeys(amount.toString())
        Alfred.sleep(75, 125)
        Alfred.keyboard.pressEnter()
        return true
    }

    fun withdrawItem(itemId: Int): Boolean {
        return internalWithdrawItem(itemId, "withdraw-1")
    }

    fun withdrawX(itemId: Int, amount: Int): Boolean {
        return if (internalWithdrawItem(itemId, "withdraw-x")) {
            internalWithdrawX(amount)
        } else false
    }

    fun withdrawAll(itemId: Int): Boolean {
        return internalWithdrawItem(itemId, "withdraw-all")
    }

    fun withdrawAllButOne(itemId: Int): Boolean {
        return internalWithdrawItem(itemId, "withdraw-all-but-1")
    }

    fun isItemVisible(itemId: Int): Boolean {
        val itemContainer = Alfred.api.widgets.getWidget(WidgetInfo.BANK_ITEM_CONTAINER)
        val rsInventoryItem = Alfred.api.banks.findItem(itemId) ?: return false
        return itemContainer.getBounds().contains(rsInventoryItem.bounds.centerX, rsInventoryItem.bounds.centerY)
    }

    private fun internalContainsItem(itemId: Int): Boolean {
        return Alfred.clientThread.invokeOnClientThread {
            val itemContainer = Alfred.api.widgets.getWidget(WidgetInfo.BANK_ITEM_CONTAINER)
            Arrays.stream(itemContainer.getDynamicChildren()).anyMatch { item: Widget? -> RSInventoryItem(item!!).id == itemId }
        }
    }

    private fun internalInventoryContainsItem(itemId: Int): Boolean {
        return Alfred.clientThread.invokeOnClientThread {
            val itemContainer = Alfred.api.widgets.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER)
            Arrays.stream(itemContainer.getDynamicChildren()).anyMatch { item: Widget? -> RSInventoryItem(item!!).id == itemId }
        }
    }

    fun containsItem(itemId: Int): Boolean {
        return internalContainsItem(itemId)
    }

    fun inventoryContainsItem(itemId: Int): Boolean {
        return internalInventoryContainsItem(itemId)
    }
}
