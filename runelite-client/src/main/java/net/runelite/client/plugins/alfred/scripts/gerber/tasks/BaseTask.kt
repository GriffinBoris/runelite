package net.runelite.client.plugins.alfred.scripts.gerber.tasks

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.models.inventory.InventoryRequirements
import net.runelite.client.plugins.alfred.scripts.gerber.GerberThread

abstract class BaseTask {
    abstract fun getBankLocation(): WorldPoint
    abstract fun getInventoryRequirements(): InventoryRequirements
    abstract fun shouldTrain(): Boolean
    abstract fun process(): Boolean
    open fun setup() {
        fetchRequiredItems()
    }

    open fun teardown() {
        Alfred.api.walk.walkTo(getBankLocation())
        Alfred.tasks.banking.depositInventoryAndEquipment()
    }

    fun run() {
        GerberThread.count = 0
        GerberThread.countLabel = ""
        setup()

        while (!GerberThread.taskTimer.isTimerComplete && !GerberThread.overallTimer.isTimerComplete && shouldTrain()) {
            if (!process()) {
                break
            }
            Alfred.sleep(100)
        }

        teardown()
    }

    fun fetchRequiredItems() {
        val player = Alfred.api.players.localPlayer

        // if we are too far away from the bank we don't have it open
        if (player.worldLocation.distanceTo(getBankLocation()) > 5) {
            Alfred.api.walk.walkTo(getBankLocation())
        }

        // if the bank is not open then open it
        Alfred.tasks.banking.openBank()

        Alfred.api.banks.depositInventory()
        Alfred.api.banks.depositEquipment()
        Alfred.tasks.inventory.fetchInventoryRequirements(getInventoryRequirements())

        Alfred.sleep(250, 750)
        Alfred.api.banks.close()
        Alfred.sleep(250, 750)
    }
}
