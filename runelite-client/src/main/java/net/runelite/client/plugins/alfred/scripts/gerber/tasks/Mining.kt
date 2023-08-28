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

class Mining(private val config: GerberConfig) : BaseTask() {
    companion object {
        private val VARROCK_EAST_MINE_WORLD_AREA = WorldArea(3281, 3361, 9, 9, 0)
        private val VARROCK_EAST_MINE_WORLD_POINT = WorldPoint(3284, 3366, 0)
    }

    private enum class ScriptState {
        SETUP, WAITING, MINING, BANKING
    }

    private var scriptState: ScriptState

    init {
        Alfred.status = "Training Mining"
        scriptState = ScriptState.SETUP
    }

    override fun getBankLocation(): WorldPoint {
        return WorldDestinations.VARROCK_EAST_BANK.worldPoint
    }

    override fun getInventoryRequirements(): InventoryRequirements {
        val player = Alfred.api.players.localPlayer
        val skillLevel = player.getSkillLevel(Skill.ATTACK)
        val inventoryRequirements = InventoryRequirements()

        val pickaxes = DynamicItemSet()
        if (skillLevel >= 1) {
            pickaxes.add(ItemID.BRONZE_PICKAXE, 1)
            pickaxes.add(ItemID.IRON_PICKAXE, 1)
        }
        if (skillLevel >= 6) {
            pickaxes.add(ItemID.STEEL_PICKAXE, 1)
        }
        if (skillLevel >= 11) {
            pickaxes.add(ItemID.BLACK_PICKAXE, 1)
        }
        if (skillLevel >= 21) {
            pickaxes.add(ItemID.MITHRIL_PICKAXE, 1)
        }
        if (skillLevel >= 31) {
            pickaxes.add(ItemID.ADAMANT_PICKAXE, 1)
        }
        if (skillLevel >= 41) {
            pickaxes.add(ItemID.RUNE_PICKAXE, 1)
        }

        if (pickaxes.getItems().isNotEmpty()) {
            inventoryRequirements.addItemSet(pickaxes)
        }
        return inventoryRequirements
    }

    override fun shouldTrain(): Boolean {
        val player = Alfred.api.players.localPlayer
        val skillLevel = player.getSkillLevel(Skill.MINING)
        return skillLevel < config.miningLevel()
    }

    override fun process(): Boolean {
        val player = Alfred.api.players.localPlayer
        val minimumSkillRequirement = player.getSkillLevel(Skill.MINING)

        if (minimumSkillRequirement < 15) {
            Alfred.taskSubStatus = "Mining Copper"
            GerberThread.countLabel = "Copper Mined"
            mineOre(VARROCK_EAST_MINE_WORLD_AREA, VARROCK_EAST_MINE_WORLD_POINT, "copper rocks", ItemID.COPPER_ORE)

        } else if (minimumSkillRequirement < 70) {
            Alfred.taskSubStatus = "Mining Iron"
            GerberThread.countLabel = "Iron Mined"
            mineOre(VARROCK_EAST_MINE_WORLD_AREA, VARROCK_EAST_MINE_WORLD_POINT, "iron rocks", ItemID.IRON_ORE)

        } else {
            return false
        }

        return true
    }

    private fun mineOre(oreArea: WorldArea, orePoint: WorldPoint, oreName: String, itemId: Int) {
        val player = Alfred.api.players.localPlayer

        if (player.isMoving || player.isInteracting) {
            return
        }

        when (scriptState) {
            ScriptState.SETUP -> {
                if (!isWieldingRequiredPickaxe) {
                    val pickaxe = getRequiredPickaxeFromInventory
                    if (pickaxe != null) {
                        val inventoryCount = Alfred.api.inventory.count()
                        pickaxe.leftClick()
                        Alfred.sleepUntil({ Alfred.api.inventory.count() == inventoryCount - 1 }, 100, 3000)
                    }
                } else {
                    scriptState = ScriptState.BANKING
                }
            }

            ScriptState.WAITING -> {
                if (!oreArea.contains(player.worldLocation)) {
                    Alfred.api.walk.walkTo(orePoint)
                }
                scriptState = ScriptState.MINING
            }

            ScriptState.MINING -> {
                if (Alfred.tasks.mining.findAndMineOre(oreName)) {
                    GerberThread.count++
                    scriptState = ScriptState.BANKING
                }
            }

            ScriptState.BANKING -> {
                if (config.keepOre()) {
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


    private val isWieldingRequiredPickaxe: Boolean
        get() {
            Alfred.status = "Checking for pickaxe"
            if (!Alfred.api.equipment.isWeaponEquipped) {
                return false
            }
            return getInventoryRequirements().getItemSets().map { dynamicItemSet: DynamicItemSet -> dynamicItemSet.getItems() }.flatten().map { thing: Pair<Int, Int> -> thing.first }.contains(Alfred.api.equipment.weaponId)
        }
    private val getRequiredPickaxeFromInventory: RSInventoryItem?
        get() {
            val inventoryItems = Alfred.api.inventory.items
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
