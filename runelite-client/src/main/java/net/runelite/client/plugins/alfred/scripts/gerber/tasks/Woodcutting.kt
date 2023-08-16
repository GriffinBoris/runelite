package net.runelite.client.plugins.alfred.scripts.gerber.tasks

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.inventory.RSInventoryItem
import net.runelite.client.plugins.alfred.enums.WorldDestinations
import net.runelite.client.plugins.alfred.scripts.gerber.GerberConfig
import net.runelite.client.plugins.alfred.scripts.gerber.GerberThread

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

    init {
        scriptState = ScriptState.SETUP
    }

//    public override fun getBankLocation(): WorldPoint {
//        val player = Alfred.api.players().localPlayer
//        val minimumSkillRequirement = player.getSkillLevel(Skill.WOODCUTTING)
//
//        return if (minimumSkillRequirement < 30) {
//            WorldDestinations.VARROCK_WEST_BANK.worldPoint
//        } else {
//            WorldDestinations.SEERS_VILLAGE_BANK.worldPoint
//        }
//    }

    override fun getBankLocation(): WorldPoint? {
        return WorldDestinations.VARROCK_WEST_BANK.worldPoint
    }

    override fun getRequiredItems(): List<Pair<Int, Int>> {
        val player = Alfred.api.players().localPlayer
        val skillLevel = player.getSkillLevel(Skill.WOODCUTTING)
        val requiredItems: MutableList<Pair<Int, Int>> = mutableListOf()

        if (skillLevel >= 1) {
            requiredItems.add(Pair(ItemID.BRONZE_AXE, 1))
            requiredItems.add(Pair(ItemID.IRON_AXE, 1))
        }
        if (skillLevel >= 6) {
            requiredItems.add(Pair(ItemID.STEEL_AXE, 1))
        }
        if (skillLevel >= 11) {
            requiredItems.add(Pair(ItemID.BLACK_AXE, 1))
        }
        if (skillLevel >= 21) {
            requiredItems.add(Pair(ItemID.MITHRIL_AXE, 1))
        }
        if (skillLevel >= 31) {
            requiredItems.add(Pair(ItemID.ADAMANT_AXE, 1))
        }
//        if (skillLevel >= 35 && player.isMembers()) {
//            requiredItems.add(Pair(ItemID.BLESSED_AXE, 1));
//        }
        if (skillLevel >= 41) {
            requiredItems.add(Pair(ItemID.RUNE_AXE, 1))
        }
        return requiredItems
    }

    fun run() {
        val player = Alfred.api.players().localPlayer

        while (!GerberThread.taskTimer.isTimerComplete()) {
            val minimumSkillRequirement = player.getSkillLevel(Skill.WOODCUTTING)

            if (minimumSkillRequirement < 15) {
                Alfred.setTaskSubStatus("Chopping Trees")
                chopTrees(VARROCK_WEST_TREE_WORLD_AREA, VARROCK_WEST_TREE_WORLD_POINT, "tree", "logs")

//            } else if (minimumSkillRequirement < 30) {
            } else if (minimumSkillRequirement < 99) {
                Alfred.setTaskSubStatus("Chopping Oak Trees")
                chopTrees(VARROCK_WEST_OAK_TREE_WORLD_AREA, VARROCK_WEST_OAK_TREE_WORLD_POINT, "oak tree", "oak logs")

//            } else if (minimumSkillRequirement < 99) {
//                Alfred.setTaskSubStatus("Chopping Willow Trees")
//                chopTrees(SEERS_WILLOW_TREE_WORLD_AREA, SEERS_WILLOW_TREE_WORLD_POINT, "willow tree", "willow logs")

            } else {
                return
            }
            Alfred.sleep(100)
        }
    }

    private fun chopTrees(treeArea: WorldArea, treePoint: WorldPoint, treeName: String, itemName: String) {
        val player = Alfred.api.players().localPlayer

        if (player.isMoving || player.isInteracting) {
            return
        }

        when (scriptState) {
            ScriptState.SETUP -> {
                if (!isWieldingRequiredAxe) {
                    val axe = getRequiredAxeFromInventory
                    if (axe != null) {
                        val inventoryCount = Alfred.api.inventory().count()
                        axe.leftClick()
                        Alfred.sleepUntil({ Alfred.api.inventory().count() == inventoryCount - 1 }, 100, 3000)

                    } else {
                        Alfred.setStatus("Going to get an axe")
                        fetchRequiredItems()
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
                        Alfred.api.walk().walkTo(getBankLocation())
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

    private val isWieldingRequiredAxe: Boolean
        get() {
            Alfred.setStatus("Checking for axe")
            if (!Alfred.api.equipment().isWeaponEquipped) {
                return false
            }
            return getRequiredItems().map { thing: Pair<Int, Int> -> thing.first }.contains(Alfred.api.equipment().weaponId)
        }
    private val getRequiredAxeFromInventory: RSInventoryItem?
        get() {
            val inventoryItems = Alfred.api.inventory().items
            getRequiredItems()
                .map { thing: Pair<Int, Int> -> thing.first }
                .forEach { requiredItemId: Int ->
                    inventoryItems.forEach { rsInventoryItem: RSInventoryItem ->
                        if (rsInventoryItem.id == requiredItemId) {
                            return rsInventoryItem
                        }
                    }
                }
            return null
        }
}
