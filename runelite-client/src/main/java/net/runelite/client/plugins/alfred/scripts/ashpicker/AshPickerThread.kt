package net.runelite.client.plugins.alfred.scripts.ashpicker

import net.runelite.api.GameState
import net.runelite.api.ItemID
import net.runelite.api.coords.WorldArea
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.item.RSGroundItem
import net.runelite.client.plugins.alfred.api.rs.npc.RSNpc
import net.runelite.client.plugins.alfred.enums.WorldDestinations

class AshPickerThread(private var config: AshPickerConfig) : Thread() {

    companion object {
        private val GE_WORLD_AREA = WorldArea(3147, 3471, 36, 38, 0);
    }

    private enum class ScriptState {
        PICKING, BANKING,
    }

    private var scriptState: ScriptState
    private var currentPickTime: Long
    private val pickTimes: MutableList<Pair<Int, Int>>

    init {
        scriptState = ScriptState.BANKING
        pickTimes = mutableListOf()
        currentPickTime = 0
    }


    override fun run() {
        if (Alfred.client.getGameState() != GameState.LOGGED_IN) {
            Alfred.api.account.login(config.worldNumber())
        }

        Alfred.api.camera.setPitch(1.0f)
        Alfred.api.camera.setYaw(315)

        val player = Alfred.api.players.localPlayer

        while (true) {
            when (scriptState) {
                ScriptState.BANKING -> {
                    if (!GE_WORLD_AREA.contains(player.worldLocation)) {
                        Alfred.api.walk.walkTo(WorldDestinations.VARROCK_GRAND_EXCHANGE.worldPoint)

                    }

                    val rsNpc = Alfred.api.npcs.npcs.firstOrNull { rsNpc: RSNpc -> rsNpc.name.equals("banker", ignoreCase = true) } ?: continue

                    if (rsNpc.worldLocation.distanceTo(player.worldLocation) >= 4) {
                        Alfred.api.walk.walkTo(WorldDestinations.VARROCK_GRAND_EXCHANGE.worldPoint)
                    }

                    if (!Alfred.tasks.npc.findAndInteract("banker", "bank")) {
                        continue
                    }

                    Alfred.sleepUntil({ Alfred.api.banks.isOpen }, 100, 1000 * 10)
                    Alfred.api.banks.depositInventory()

                    if (!Alfred.tasks.banking.closeBank()) {
                        continue
                    }

                    scriptState = ScriptState.PICKING
                }

                ScriptState.PICKING -> {
                    if (Alfred.api.inventory.isFull) {
                        scriptState = ScriptState.BANKING
                        continue
                    }

                    AshPickerOverlay.ashPrice = config.ashPrice();
                    val countBefore = Alfred.api.inventory.getItems(ItemID.ASHES).count()
                    if (lootAsh(100)) {
                        val countAfter = Alfred.api.inventory.getItems(ItemID.ASHES).count()
                        AshPickerOverlay.ashPicked += countAfter - countBefore
                        setLootsPerHour(countAfter - countBefore)
                    }
                }
            }
        }
    }

    private fun lootAsh(radius: Int): Boolean {
        val player = Alfred.api.players.localPlayer
        val rsGroundItem = Alfred.api.items.getItemsFromTiles(radius, ItemID.ASHES).sortedBy { item: RSGroundItem -> item.worldLocation.distanceTo(player.worldLocation) }.firstOrNull() ?: return false

        if (Alfred.api.items.getItemsFromTiles(radius, rsGroundItem.id).filter { groundItem: RSGroundItem -> groundItem.worldLocation.equals(rsGroundItem.worldLocation) }.isEmpty()) {
            return false
        }

        if (rsGroundItem.worldLocation.distanceTo(player.worldLocation) >= 4) {
            Alfred.api.walk.walkTo(rsGroundItem.worldLocation)
        }

        if (Alfred.api.items.getItemsFromTiles(radius, rsGroundItem.id).filter { groundItem: RSGroundItem -> groundItem.worldLocation.equals(rsGroundItem.worldLocation) }.isEmpty()) {
            return false
        }

        val inventoryCount = Alfred.api.inventory.count()
        if (rsGroundItem.clickAction("take")) {
            Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 100, 1000 * 10)
            Alfred.sleepUntil({ Alfred.api.items.isItemOnGround(rsGroundItem.id, rsGroundItem.worldLocation) }, 100, 1000 * 5)
            return Alfred.sleepUntil({ Alfred.api.inventory.count() == inventoryCount + 1 }, 100, 1000 * 5)

        } else {
            val inventory = Alfred.api.widgets.getWidget(WidgetInfo.FIXED_VIEWPORT_INTERFACE_CONTAINER) ?: return false
            Alfred.mouse.move(inventory.bounds)
        }
        return false
    }

    private fun setLootsPerHour(amount: Int) {
        if (currentPickTime.toInt() == 0) {
            currentPickTime = System.currentTimeMillis()
            return
        }

        val difference = System.currentTimeMillis() - currentPickTime
        currentPickTime = System.currentTimeMillis()

        pickTimes.add(Pair(difference.toInt(), amount))
        if (pickTimes.count() < 2) {
            return
        }

        val averageSeconds = pickTimes.map { item -> item.first }.average() / 1000
        val averageLootAmounts = pickTimes.map { item -> item.second }.average()

        val lootsPerHour = (3600 / averageSeconds).toInt() * averageLootAmounts
        AshPickerOverlay.ashPerHour = lootsPerHour.toInt()
    }
}
