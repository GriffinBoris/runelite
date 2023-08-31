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
        WAITING, CHOPPING, BANKING, SETUP
    }

    private var scriptState: ScriptState

    init {
        Alfred.status = "Training Woodcutting"
        scriptState = ScriptState.SETUP
    }

    override fun getBankLocation(): WorldPoint {
        val player = Alfred.api.players.localPlayer
        val minimumSkillRequirement = player.getSkillLevel(Skill.WOODCUTTING)

        return if (minimumSkillRequirement < 30) {
            WorldDestinations.VARROCK_WEST_BANK.worldPoint
        } else {
            WorldDestinations.SEERS_VILLAGE_BANK.worldPoint
        }
    }
//    override fun getBankLocation(): WorldPoint {
//        return WorldDestinations.VARROCK_WEST_BANK.worldPoint
//    }

    override fun getInventoryRequirements(): InventoryRequirements {
        val player = Alfred.api.players.localPlayer
        val skillLevel = player.getSkillLevel(Skill.ATTACK)
        val inventoryRequirements = InventoryRequirements()

        val axes = DynamicItemSet()
        if (skillLevel >= 1) {
            axes.add(ItemID.BRONZE_AXE, 1)
            axes.add(ItemID.IRON_AXE, 1)
        }
        if (skillLevel >= 6) {
            axes.add(ItemID.STEEL_AXE, 1)
        }
        if (skillLevel >= 11) {
            axes.add(ItemID.BLACK_AXE, 1)
        }
        if (skillLevel >= 21) {
            axes.add(ItemID.MITHRIL_AXE, 1)
        }
        if (skillLevel >= 31) {
            axes.add(ItemID.ADAMANT_AXE, 1)
        }
        if (skillLevel >= 41) {
            axes.add(ItemID.RUNE_AXE, 1)
        }

        if (axes.getItems().isNotEmpty()) {
            inventoryRequirements.addItemSet(axes)
        }

        return inventoryRequirements
    }

    override fun shouldTrain(): Boolean {
        val player = Alfred.api.players.localPlayer
        val skillLevel = player.getSkillLevel(Skill.WOODCUTTING)
        return skillLevel < config.woodcuttingLevel()
    }

    override fun process(): Boolean {
        val player = Alfred.api.players.localPlayer
        val minimumSkillRequirement = player.getSkillLevel(Skill.WOODCUTTING)

        if (minimumSkillRequirement < 15) {
            Alfred.taskSubStatus = "Chopping Trees"
            GerberThread.countLabel = "Logs Collected"
            chopTrees(VARROCK_WEST_TREE_WORLD_AREA, VARROCK_WEST_TREE_WORLD_POINT, "tree", ItemID.LOGS)

        } else if (minimumSkillRequirement < 30) {
            Alfred.taskSubStatus = "Chopping Oak Trees"
            GerberThread.countLabel = "Oak Logs Collected"
            chopTrees(VARROCK_WEST_OAK_TREE_WORLD_AREA, VARROCK_WEST_OAK_TREE_WORLD_POINT, "oak tree", ItemID.OAK_LOGS)

        } else if (minimumSkillRequirement < 99) {
            Alfred.taskSubStatus = "Chopping Willow Trees"
            GerberThread.countLabel = "Willow Logs Collected"
            chopTrees(SEERS_WILLOW_TREE_WORLD_AREA, SEERS_WILLOW_TREE_WORLD_POINT, "willow tree", ItemID.WILLOW_LOGS)

        } else {
            return false
        }

        return true
    }

    private fun chopTrees(treeArea: WorldArea, treePoint: WorldPoint, treeName: String, itemId: Int) {
        val player = Alfred.api.players.localPlayer

        if (player.isMoving || player.isInteracting) {
            return
        }

        when (scriptState) {
            ScriptState.SETUP -> {
                if (!isWieldingRequiredAxe) {
                    val axe = getRequiredAxeFromInventory
                    if (axe != null) {
                        val inventoryCount = Alfred.api.inventory.count()
                        axe.leftClick()
                        Alfred.sleepUntil({ Alfred.api.inventory.count() == inventoryCount - 1 }, 100, 3000)
                    }
                } else {
                    scriptState = ScriptState.BANKING
                }
            }

            ScriptState.WAITING -> {
                if (!treeArea.contains(player.worldLocation)) {
                    Alfred.api.walk.walkTo(treePoint)
                }
                scriptState = ScriptState.CHOPPING
            }

            ScriptState.CHOPPING -> {
                val countBefore = Alfred.api.inventory.getItems(itemId).count()

                if (Alfred.tasks.woodcutting.findAndChopTree(treeName)) {
                    val countAfter = Alfred.api.inventory.getItems(itemId).count()
                    GerberThread.count += countAfter - countBefore
                    scriptState = ScriptState.BANKING
                }
            }

            ScriptState.BANKING -> {
                if (config.keepLogs()) {
                    if (Alfred.api.inventory.isFull) {
                        Alfred.api.walk.walkTo(getBankLocation())
                        Alfred.tasks.banking.depositInventory()
                        Alfred.sleep(200)
                    }
                } else {
                    for (item in Alfred.api.inventory.getItems(itemId)) {
                        val count = Alfred.api.inventory.count()
                        item.drop()
                        Alfred.sleepUntil({ Alfred.api.inventory.count() == count - 1 }, 200, 1000 * 5)
                    }
                }
                scriptState = ScriptState.WAITING
            }
        }
    }

    private val isWieldingRequiredAxe: Boolean
        get() {
            Alfred.status = "Checking for axe"
            if (!Alfred.api.equipment.isWeaponEquipped) {
                return false
            }
            return getInventoryRequirements().getItemSets().map { dynamicItemSet: DynamicItemSet -> dynamicItemSet.getItems() }.flatten().map { thing: Pair<Int, Int> -> thing.first }.contains(Alfred.api.equipment.weaponId)
        }
    private val getRequiredAxeFromInventory: RSInventoryItem?
        get() {
            val inventoryItems = Alfred.api.inventory.items.filterNotNull()
            getInventoryRequirements().getItemSets().map { dynamicItemSet: DynamicItemSet -> dynamicItemSet.getItems() }.flatten().map { thing: Pair<Int, Int> -> thing.first }.forEach { requiredItemId: Int ->
                inventoryItems.forEach { rsInventoryItem: RSInventoryItem ->
                    if (rsInventoryItem.id == requiredItemId) {
                        return rsInventoryItem
                    }
                }
            }
            return null
        }
}
