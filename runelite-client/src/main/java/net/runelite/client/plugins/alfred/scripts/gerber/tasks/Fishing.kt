package net.runelite.client.plugins.alfred.scripts.gerber.tasks

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.models.DynamicItemSet
import net.runelite.client.plugins.alfred.api.models.inventory.InventoryRequirements
import net.runelite.client.plugins.alfred.api.rs.inventory.RSInventoryItem
import net.runelite.client.plugins.alfred.enums.WorldDestinations
import net.runelite.client.plugins.alfred.scripts.gerber.GerberConfig
import net.runelite.client.plugins.alfred.scripts.gerber.GerberThread

class Fishing(private val config: GerberConfig) : BaseTask() {
    companion object {
        private val DRAYNOR_FISHING_AREA = WorldArea(3084, 3223, 7, 11, 0)
        private val DRAYNOR_FISHING_POINT = WorldPoint(3087, 3229, 0)
        private val BARBARIAN_FISHING_AREA = WorldArea(3101, 3423, 10, 13, 0)
        private val BARBARIAN_FISHING_POINT = WorldPoint(3105, 3430, 0)
    }

    private enum class ScriptState {
        FISHING, WAITING, BANKING,
    }

    private var scriptState: ScriptState

    init {
        Alfred.status = "Training Fishing"
        scriptState = ScriptState.WAITING
    }

    override fun getBankLocation(): WorldPoint {
        val player = Alfred.api.players.localPlayer
        val minimumSkillRequirement = player.getSkillLevel(Skill.FISHING)

        return if (minimumSkillRequirement < 20) {
            WorldDestinations.DRAYNOR_VILLAGE_BANK.worldPoint
        } else {
            WorldDestinations.VARROCK_WEST_BANK.worldPoint
        }
    }

    override fun getInventoryRequirements(): InventoryRequirements {
        val player = Alfred.api.players.localPlayer
        val skillLevel = player.getSkillLevel(Skill.FISHING)
        val inventoryRequirements = InventoryRequirements()

        val tools = DynamicItemSet()
        val other = DynamicItemSet()

        if (skillLevel >= 1) {
            tools.add(ItemID.SMALL_FISHING_NET, 1)
        }
        if (skillLevel >= 20) {
            tools.add(ItemID.FLY_FISHING_ROD, 1)
            other.add(ItemID.FEATHER, 200)
        }

        if (tools.getItems().isNotEmpty()) {
            inventoryRequirements.addItemSet(tools)
        }

        if (other.getItems().isNotEmpty()) {
            inventoryRequirements.addItemSet(other)
        }

        return inventoryRequirements
    }

    override fun shouldTrain(): Boolean {
        val player = Alfred.api.players.localPlayer
        val skillLevel = player.getSkillLevel(Skill.FISHING)
        return skillLevel < config.fishingLevel()
    }

    override fun process(): Boolean {
        val player = Alfred.api.players.localPlayer
        val minimumSkillRequirement = player.getSkillLevel(Skill.FISHING)

        if (minimumSkillRequirement < 15) {
            Alfred.taskSubStatus = "Catching Shrimp"
            GerberThread.countLabel = "Shrimp Caught"
            catchFish(DRAYNOR_FISHING_AREA, DRAYNOR_FISHING_POINT, "fishing spot", "small net", listOf(ItemID.RAW_SHRIMPS))

        } else if (minimumSkillRequirement < 20) {
            Alfred.taskSubStatus = "Catching Shrimp & Anchovies"
            GerberThread.countLabel = "Shrimp & Anchovies Caught"
            catchFish(DRAYNOR_FISHING_AREA, DRAYNOR_FISHING_POINT, "fishing spot", "small net", listOf(ItemID.RAW_SHRIMPS, ItemID.RAW_ANCHOVIES))

        } else if (minimumSkillRequirement < 99) {
            Alfred.taskSubStatus = "Catching Trout & Salmon"
            GerberThread.countLabel = "Trout & Salmon Caught"
            catchFish(BARBARIAN_FISHING_AREA, BARBARIAN_FISHING_POINT, "rod fishing spot", "lure", listOf(ItemID.RAW_SALMON, ItemID.RAW_TROUT))

        } else {
            return false
        }

        return true
    }

    private fun catchFish(fishingArea: WorldArea, fishingPoint: WorldPoint, fishingSpotName: String, actionName: String, itemIds: List<Int>) {
        val player = Alfred.api.players.localPlayer

        if (player.isMoving || player.isAnimating) {
            return
        }

        when (scriptState) {
            ScriptState.WAITING -> {
                if (!fishingArea.contains(player.worldLocation)) {
                    Alfred.api.walk.walkTo(fishingPoint)
                }
                scriptState = ScriptState.FISHING
            }

            ScriptState.FISHING -> {
                var countBefore = 0
                itemIds.forEach { itemId: Int ->
                    countBefore += Alfred.api.inventory.getItems(itemId).count()
                }

                if (Alfred.tasks.fishing.findAndInteract(fishingSpotName, actionName)) {
                    var countAfter = 0
                    itemIds.forEach { itemId: Int ->
                        countAfter += Alfred.api.inventory.getItems(itemId).count()
                    }

                    GerberThread.count += countAfter - countBefore
                    scriptState = ScriptState.BANKING
                }
            }

            ScriptState.BANKING -> {
                if (config.keepFish()) {
                    if (Alfred.api.inventory.isFull) {
                        Alfred.api.walk.walkTo(getBankLocation())
                        Alfred.tasks.banking.depositItems(itemIds)
                        Alfred.sleep(200)
                    }
                } else {
                    itemIds.forEach { itemId: Int ->
                        Alfred.api.inventory.getItems(itemId).forEach { rsInventoryItem: RSInventoryItem ->
                            val count = Alfred.api.inventory.count()
                            rsInventoryItem.drop()
                            Alfred.sleepUntil({ Alfred.api.inventory.count() == count - 1 }, 200, 1000 * 5)
                        }
                    }
                }
                scriptState = ScriptState.WAITING
            }
        }
    }
}
