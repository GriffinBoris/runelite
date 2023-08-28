package net.runelite.client.plugins.alfred.scripts.vialbuyer

import net.runelite.api.GameState
import net.runelite.api.ItemID
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.Widget
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.enums.WorldDestinations

class VialBuyerThread : Thread() {

    companion object {
        val vialsBought = 0
        private val VIAL_SHOP_WORLD_POINT = WorldPoint(2614, 3293, 0)
    }

    private enum class ScriptState {
        SETUP,
        BUYING,
        BANKING
    }

    private var scriptState: ScriptState
    private val buyTimes: MutableList<Pair<Int, Int>>

    private var currentBuyTime: Long

    init {
        scriptState = ScriptState.SETUP
        buyTimes = mutableListOf()
        currentBuyTime = 0
    }


    override fun run() {
        if (Alfred.client.getGameState() != GameState.LOGGED_IN) {
            Alfred.api.account.login()
        }

        val player = Alfred.api.players.localPlayer

        while (true) {
            when (scriptState) {
                ScriptState.SETUP -> {
                    Alfred.api.walk.walkTo(WorldDestinations.ARDOUGNE_SOUTH_BANK.worldPoint)
                    if (!Alfred.tasks.banking.openBank()) {
                        continue
                    }

                    Alfred.api.banks.depositInventory()
                    Alfred.api.banks.withdrawAllButOne(ItemID.COINS)

                    if (!Alfred.tasks.banking.closeBank()) {
                        continue
                    }

                    scriptState = ScriptState.BUYING
                }

                ScriptState.BUYING -> {
                    Alfred.api.walk.walkTo(VIAL_SHOP_WORLD_POINT)
                    Alfred.tasks.npc.findAndInteract("kortan", "trade")
                    Alfred.sleepUntil({ isShopWindowOpen() }, 100, 5000)

                    val vial = findItemInShop()
                    vial ?: continue

                    Alfred.sleep(100, 200)
                    Alfred.api.widgets.rightClickWidget(vial)
                    if (!Alfred.sleepUntil({ Alfred.api.menu.menu.hasAction("buy 50") }, 200, 2000)) {
                        continue
                    }

                    if (!Alfred.api.menu.menu.clickAction("buy 50")) {
                        continue
                    }

                    Alfred.sleep(300, 700)

                    val closeButton = Alfred.api.widgets.getChildWidget(19660801, 11)
                    closeButton ?: continue
                    Alfred.api.widgets.leftClickWidget(closeButton)

                    Alfred.sleep(200, 400)
                    val vialCount = Alfred.api.inventory.getItems(ItemID.VIAL_OF_WATER).count()
                    VialBuyerOverlay.vialsBought += vialCount
                    setVialsPerHour(vialCount)

                    scriptState = ScriptState.BANKING
                }

                ScriptState.BANKING -> {
                    Alfred.api.walk.walkTo(WorldDestinations.ARDOUGNE_SOUTH_BANK.worldPoint)
                    if (!Alfred.tasks.banking.openBank()) {
                        continue
                    }

                    Alfred.api.banks.depositInventory()
                    Alfred.api.banks.withdrawAllButOne(ItemID.COINS)

                    if (!Alfred.tasks.banking.closeBank()) {
                        continue
                    }

                    scriptState = ScriptState.BUYING
                }
            }
        }

    }

    private fun isShopWindowOpen(): Boolean {
        val widget = Alfred.api.widgets.getWidget(19660800) ?: return false
        return !widget.isHidden && !widget.isSelfHidden
    }

    private fun findItemInShop(): Widget? {
        val widget = Alfred.api.widgets.getWidget(19660816) ?: return null

        for (child in widget.dynamicChildren) {
            if (child.itemId == ItemID.VIAL_OF_WATER) {
                return child
            }
        }

        return null
    }

    private fun setVialsPerHour(buyAmount: Int) {
        if (currentBuyTime.toInt() == 0) {
            currentBuyTime = System.currentTimeMillis()
            return
        }

        val difference = System.currentTimeMillis() - currentBuyTime
        currentBuyTime = System.currentTimeMillis()

        buyTimes.add(Pair(difference.toInt(), buyAmount))
        if (buyTimes.count() < 2) {
            return
        }

        val averageSeconds = buyTimes.map { item -> item.first }.average() / 1000
        val averageBuyAmounts = buyTimes.map { item -> item.second }.average()

        val buysPerHour = (3600 / averageSeconds).toInt() * averageBuyAmounts
        VialBuyerOverlay.vialsPerHour = buysPerHour.toInt()
    }
}
