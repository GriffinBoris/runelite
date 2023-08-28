package net.runelite.client.plugins.alfred.api.task

import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.util.Utility

class Banking {

    fun openBank(): Boolean {
        return Utility.retryFunction(3, false) {
            val player = Alfred.api.players.localPlayer
            val bank = Alfred.api.banks.getNearestBanks().firstOrNull()

            bank ?: return@retryFunction false
            if (bank.worldLocation.distanceTo(player.worldLocation) >= 2) {
                if (!Alfred.api.screen.isPointOnScreen(bank.localLocation, bank.worldLocation.plane)) {
                    val minimapPoint = Alfred.api.miniMap.getWorldPointToScreenPoint(bank.worldLocation)
                    minimapPoint ?: return@retryFunction false
                    Alfred.mouse.leftClick(minimapPoint)
                    Alfred.sleep(1000)
                    Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
                }
            }

            Alfred.api.banks.open(bank)
            return@retryFunction Alfred.sleepUntil({ Alfred.api.banks.isOpen }, 100, 5000)
        }
    }

    fun closeBank(): Boolean {
        Alfred.api.banks.close()
        return Alfred.sleepUntil({ Alfred.api.banks.isClosed }, 100, 5000)
    }

    fun depositInventory(): Boolean {
        if (!openBank()) {
            return false
        }

        Alfred.api.banks.depositInventory()
        Alfred.sleepUntil({ Alfred.api.inventory.isEmpty }, 100, 5000)
        Alfred.sleep(200, 400)

        if (!closeBank()) {
            return false
        }

        Alfred.sleep(200, 400)
        return true
    }

    fun depositInventoryAndEquipment(): Boolean {
        if (!openBank()) {
            return false
        }

        Alfred.api.banks.depositInventory()
        Alfred.api.banks.depositEquipment()
        Alfred.sleepUntil({ Alfred.api.inventory.isEmpty }, 100, 5000)
        Alfred.sleep(200, 400)

        if (!closeBank()) {
            return false
        }

        Alfred.sleep(200, 400)
        return true
    }

    fun withdrawItems(itemsAndQuantities: List<Pair<Int, Int>>): Boolean {
        if (!openBank()) {
            return false
        }

        for (itemAndQuantity in itemsAndQuantities.reversed()) {
            if (!Alfred.api.banks.containsItem(itemAndQuantity.first)) {
                continue
            }

            var searched = false
            val rsInventoryItem = Alfred.api.banks.findItem(itemAndQuantity.first)
            if (!Alfred.api.banks.isItemVisible(rsInventoryItem!!.id)) {
                Alfred.api.banks.search(rsInventoryItem.name)
                Alfred.sleep(200, 400)
                searched = true
            }

            when {
                itemAndQuantity.second <= 0 -> continue
                itemAndQuantity.second == 1 -> Alfred.api.banks.withdrawItem(itemAndQuantity.first)
                else -> Alfred.api.banks.withdrawX(itemAndQuantity.first, itemAndQuantity.second)
            }

            if (searched) {
                Alfred.api.banks.search()
                Alfred.sleep(200, 400)
            }

            Alfred.sleep(100, 200)
            break
        }

        if (!closeBank()) {
            return false
        }

        Alfred.sleep(200, 400)
        return true
    }

    fun withdrawItemX(itemID: Int, quantity: Int): Boolean {
        if (!Alfred.api.banks.containsItem(itemID)) {
            return false
        }

        var searched = false
        val rsInventoryItem = Alfred.api.banks.findItem(itemID)
        if (!Alfred.api.banks.isItemVisible(rsInventoryItem!!.id)) {
            Alfred.api.banks.search(rsInventoryItem.name)
            Alfred.sleep(200, 400)
            searched = true
        }

        when {
            quantity <= 0 -> return true
            else -> Alfred.api.banks.withdrawX(itemID, quantity)
        }

        if (searched) {
            Alfred.api.banks.search()
            Alfred.sleep(200, 400)
        }

        Alfred.sleep(100, 200)

        return true
    }

    fun depositItems(itemIds: List<Int>): Boolean {
        if (!openBank()) {
            return false
        }

        itemIds.forEach { itemId ->
            Alfred.api.banks.depositAll(itemId)
            Alfred.sleep(100, 200)
        }

        if (!closeBank()) {
            return false
        }

        Alfred.sleep(200, 400)
        return true
    }
}