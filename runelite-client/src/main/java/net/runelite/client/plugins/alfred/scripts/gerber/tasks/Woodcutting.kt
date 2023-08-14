package net.runelite.client.plugins.alfred.scripts.gerber.tasks

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.inventory.RSInventoryItem
import net.runelite.client.plugins.alfred.enums.WorldDestinations
import net.runelite.client.plugins.alfred.scripts.gerber.GerberConfig

class Woodcutting(private val config: GerberConfig) : BaseTask() {
    companion object {
        private val VARROCK_WEST_TREE_WORLD_AREA = WorldArea(3159, 3388, 13, 25, 0)
        private val VARROCK_WEST_TREE_WORLD_POINT = WorldPoint(3164, 3402, 0)
        private val VARROCK_WEST_OAK_TREE_WORLD_AREA = WorldArea(3132, 3393, 17, 23, 0)
        private val VARROCK_WEST_OAK_TREE_WORLD_POINT = WorldPoint(3137, 3403, 0);
        private val SEERS_WILLOW_TREE_WORLD_AREA = WorldArea(2701, 3502, 20, 14, 0)
        private val SEERS_WILLOW_TREE_WORLD_POINT = WorldPoint(2709, 3506, 0)
    }

    private enum class ScriptState {
        WAITING,
        CHOPPING,
        BANKING,
        SETUP
    }

    private var scriptState: ScriptState
    private val recommendedItems: MutableList<List<Int>>

    init {
        scriptState = ScriptState.SETUP
        recommendedItems = ArrayList()
        recommendedItems.add(recommendedAxes)
    }

    public override fun getBankLocation(): WorldPoint {
        val player = Alfred.api.players().localPlayer
        val minimumSkillRequirement = player.getSkillLevel(Skill.WOODCUTTING)

        return if (minimumSkillRequirement < 30) {
            WorldDestinations.VARROCK_WEST_BANK.worldPoint
        } else {
            WorldDestinations.SEERS_VILLAGE_BANK.worldPoint
        }
    }

    fun run() {
        val player = Alfred.api.players().localPlayer

        while (!Alfred.getPlayTimer().isTimerComplete()) {
            val minimumSkillRequirement = player.getSkillLevel(Skill.WOODCUTTING)

            if (minimumSkillRequirement < 15) {
                Alfred.setTaskSubStatus("Chopping Trees")
                chopTrees(VARROCK_WEST_TREE_WORLD_AREA, VARROCK_WEST_TREE_WORLD_POINT, "tree", "logs")

            } else if (minimumSkillRequirement < 30) {
                Alfred.setTaskSubStatus("Chopping Oak Trees")
                chopTrees(VARROCK_WEST_OAK_TREE_WORLD_AREA, VARROCK_WEST_OAK_TREE_WORLD_POINT, "oak tree", "oak logs")

            } else if (minimumSkillRequirement < 99) {
                Alfred.setTaskSubStatus("Chopping Willow Trees")
                chopTrees(SEERS_WILLOW_TREE_WORLD_AREA, SEERS_WILLOW_TREE_WORLD_POINT, "willow tree", "willow logs")

            } else {
                return
            }
            Alfred.sleep(100)
        }
    }

    private val recommendedAxes: List<Int>
        get() {
            val player = Alfred.api.players().localPlayer
            val skillLevel = player.getSkillLevel(Skill.WOODCUTTING)
            val itemIds: MutableList<Int> = ArrayList()

            if (skillLevel >= 1) {
                itemIds.add(ItemID.BRONZE_AXE)
                itemIds.add(ItemID.IRON_AXE)
            }
            if (skillLevel >= 6) {
                itemIds.add(ItemID.STEEL_AXE)
            }
            if (skillLevel >= 11) {
                itemIds.add(ItemID.BLACK_AXE)
            }
            if (skillLevel >= 21) {
                itemIds.add(ItemID.MITHRIL_AXE)
            }
            if (skillLevel >= 31) {
                itemIds.add(ItemID.ADAMANT_AXE)
            }

//        if (skillLevel >= 35 && player.isMembers()) {
//            itemIds.add(ItemID.BLESSED_AXE);
//        }
            if (skillLevel >= 41) {
                itemIds.add(ItemID.RUNE_AXE)
            }
            return itemIds
        }

    private fun chopTrees(treeArea: WorldArea, treePoint: WorldPoint, treeName: String, itemName: String) {
        val player = Alfred.api.players().localPlayer

        if (player.isMoving || player.isInteracting) {
            return
        }

        when (scriptState) {
            ScriptState.SETUP -> {
                if (!isWieldingRecommendedAxe) {
                    val axe = recommendedAxeFromInventory
                    if (axe != null) {
                        val inventoryCount = Alfred.api.inventory().count()
                        axe.leftClick()
                        Alfred.sleepUntil({ Alfred.api.inventory().count() == inventoryCount - 1 }, 100, 3000)

                    } else {
                        Alfred.setStatus("Going to get an axe")
                        // walk to bank, get an axe, close the bank and then loop around so it equips it
                        retrieveRecommendedItems(recommendedItems)
                    }
                } else {
                    scriptState = ScriptState.BANKING
                }
            }

            ScriptState.WAITING -> {
                if (!treeArea.contains(player.worldLocation)) {
                    Alfred.api.walk().walkTo(treePoint)
                }
                scriptState = ScriptState.CHOPPING
            }

            ScriptState.CHOPPING -> {
                Alfred.tasks.woodcutting.findAndChopTree(treeName)
                scriptState = ScriptState.BANKING
            }

            ScriptState.BANKING -> {
                if (config.keepLogs()) {
                    if (Alfred.api.inventory().isFull) {
                        Alfred.api.walk().walkTo(bankLocation)
                        Alfred.tasks.banking.depositInventory()
                        Alfred.sleep(200)
                    }
                } else {
                    for (item in Alfred.api.inventory().getItems(itemName)) {
                        val count = Alfred.api.inventory().count()
                        item.drop()
                        Alfred.sleepUntil({ Alfred.api.inventory().count() == count - 1 }, 200, 1000 * 5)
                    }
                }
                scriptState = ScriptState.WAITING
            }
        }
    }

    private val isWieldingRecommendedAxe: Boolean
        get() {
            Alfred.setStatus("Checking for axe")
            return if (!Alfred.api.equipment().isWeaponEquipped) {
                false
            } else recommendedAxes.contains(Alfred.api.equipment().weaponId)
        }
    private val recommendedAxeFromInventory: RSInventoryItem?
        get() {
            val inventoryItems = Alfred.api.inventory().items
            for (recommendedItemId in recommendedAxes) {
                for (rsInventoryItem in inventoryItems) {
                    if (rsInventoryItem.id == recommendedItemId) {
                        return rsInventoryItem
                    }
                }
            }
            return null
        }
}
