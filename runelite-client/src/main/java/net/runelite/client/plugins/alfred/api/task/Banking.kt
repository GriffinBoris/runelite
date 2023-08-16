package net.runelite.client.plugins.alfred.api.task

import net.runelite.client.plugins.alfred.Alfred

class Banking {

    fun openBank(): Boolean {
        val player = Alfred.api.players().localPlayer
        val bank = Alfred.api.banks().nearestBanks.firstOrNull()

        bank ?: return false
        if (bank.worldLocation.distanceTo(player.worldLocation) >= 2) {
            if (!Alfred.api.screen().isPointOnScreen(bank.localLocation, bank.worldLocation.plane)) {
                return false
            }

            val minimapPoint = Alfred.api.miniMap().getWorldPointToScreenPoint(bank.worldLocation)
            minimapPoint ?: return false
            Alfred.getMouse().leftClick(minimapPoint)
            Alfred.sleep(1000)
            Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
        }

        Alfred.api.banks().open(bank)
        return Alfred.sleepUntil({ Alfred.api.banks().isOpen() }, 100, 5000)
    }

    fun closeBank(): Boolean {
        Alfred.api.banks().close()
        return Alfred.sleepUntil({ Alfred.api.banks().isClosed() }, 100, 5000)
    }

    fun depositInventory(): Boolean {
        if (!openBank()) {
            return false
        }

        Alfred.api.banks().depositInventory()
        Alfred.sleepUntil({ Alfred.api.inventory().isEmpty }, 100, 5000)
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

        for (itemAndQuantity in itemsAndQuantities) {
            if (!Alfred.api.banks().containsItem(itemAndQuantity.first)) {
                continue
            }

            when {
                itemAndQuantity.second <= 0 -> continue
                itemAndQuantity.second == 1 -> Alfred.api.banks().withdrawItem(itemAndQuantity.first)
                else -> Alfred.api.banks().withdrawX(itemAndQuantity.first, itemAndQuantity.second)
            }

            Alfred.sleep(100, 200)
        }

        if (!closeBank()) {
            return false
        }

        Alfred.sleep(200, 400)
        return true
    }
}