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
        Alfred.setStatus("Training Mining")
        scriptState = ScriptState.SETUP
    }

    override fun getBankLocation(): WorldPoint {
        return WorldDestinations.VARROCK_EAST_BANK.worldPoint
    }

    override fun getRequiredItems(): List<Pair<Int, Int>> {
        val player = Alfred.api.players().localPlayer
        val skillLevel = player.getSkillLevel(Skill.ATTACK)
        val requiredItems: MutableList<Pair<Int, Int>> = mutableListOf()

        if (skillLevel >= 1) {
            requiredItems.add(Pair(ItemID.BRONZE_PICKAXE, 1))
            requiredItems.add(Pair(ItemID.IRON_PICKAXE, 1))
        }
        if (skillLevel >= 6) {
            requiredItems.add(Pair(ItemID.STEEL_PICKAXE, 1))
        }
        if (skillLevel >= 11) {
            requiredItems.add(Pair(ItemID.BLACK_PICKAXE, 1))
        }
        if (skillLevel >= 21) {
            requiredItems.add(Pair(ItemID.MITHRIL_PICKAXE, 1))
        }
        if (skillLevel >= 31) {
            requiredItems.add(Pair(ItemID.ADAMANT_PICKAXE, 1))
        }
        if (skillLevel >= 41) {
            requiredItems.add(Pair(ItemID.RUNE_PICKAXE, 1))
        }
        return requiredItems
    }

    override fun shouldTrain(): Boolean {
        val player = Alfred.api.players().localPlayer
        val skillLevel = player.getSkillLevel(Skill.MINING)
        return skillLevel < config.miningLevel()
    }

    override fun process(): Boolean {
        val player = Alfred.api.players().localPlayer
        val minimumSkillRequirement = player.getSkillLevel(Skill.MINING)

        if (minimumSkillRequirement < 15) {
            Alfred.setTaskSubStatus("Mining Copper")
            GerberThread.countLabel = "Copper Mined"
            mineOre(VARROCK_EAST_MINE_WORLD_AREA, VARROCK_EAST_MINE_WORLD_POINT, "copper rocks", "copper ore")

        } else if (minimumSkillRequirement < 70) {
            Alfred.setTaskSubStatus("Mining Iron")
            GerberThread.countLabel = "Iron Mined"
            mineOre(VARROCK_EAST_MINE_WORLD_AREA, VARROCK_EAST_MINE_WORLD_POINT, "iron rocks", "iron ore")

        } else {
            return false
        }

        return true
    }

    private fun mineOre(oreArea: WorldArea, orePoint: WorldPoint, oreName: String, itemName: String) {
        val player = Alfred.api.players().localPlayer

        if (player.isMoving || player.isInteracting) {
            return
        }

        when (scriptState) {
            ScriptState.SETUP -> {
                if (!isWieldingRequiredPickaxe) {
                    val pickaxe = getRequiredPickaxeFromInventory
                    if (pickaxe != null) {
                        val inventoryCount = Alfred.api.inventory().count()
                        pickaxe.leftClick()
                        Alfred.sleepUntil({ Alfred.api.inventory().count() == inventoryCount - 1 }, 100, 3000)
                    }
                } else {
                    scriptState = ScriptState.BANKING
                }
            }

            ScriptState.WAITING -> {
                if (!oreArea.contains(player.worldLocation)) {
                    Alfred.api.walk().walkTo(orePoint)
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


    private val isWieldingRequiredPickaxe: Boolean
        get() {
            Alfred.setStatus("Checking for pickaxe")
            if (!Alfred.api.equipment().isWeaponEquipped) {
                return false
            }
            return getRequiredItems().map { thing: Pair<Int, Int> -> thing.first }.contains(Alfred.api.equipment().weaponId)
        }
    private val getRequiredPickaxeFromInventory: RSInventoryItem?
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
