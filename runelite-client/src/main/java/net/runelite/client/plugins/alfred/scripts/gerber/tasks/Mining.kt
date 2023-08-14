package net.runelite.client.plugins.alfred.scripts.gerber.tasks

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.inventory.RSInventoryItem
import net.runelite.client.plugins.alfred.enums.WorldDestinations
import net.runelite.client.plugins.alfred.scripts.gerber.GerberConfig

class Mining(private val config: GerberConfig) : BaseTask() {
    companion object {
        private val VARROCK_EAST_MINE_WORLD_AREA = WorldArea(3281, 3361, 9, 9, 0)
        private val VARROCK_EAST_MINE_WORLD_POINT = WorldPoint(3284, 3366, 0)
    }

    private enum class ScriptState {
        SETUP, WAITING, MINING, BANKING
    }

    private var scriptState: ScriptState
    private val recommendedItems: MutableList<List<Int>>

    init {
        scriptState = ScriptState.SETUP
        recommendedItems = ArrayList()
        recommendedItems.add(recommendedPickaxes)
    }

    public override fun getBankLocation(): WorldPoint {
        return WorldDestinations.VARROCK_EAST_BANK.worldPoint
    }

    fun run() {
        val player = Alfred.api.players().localPlayer

        while (!Alfred.getPlayTimer().isTimerComplete()) {
            val minimumSkillRequirement = player.getSkillLevel(Skill.MINING)

            if (minimumSkillRequirement < 15) {
                Alfred.setTaskSubStatus("Mining Copper")
                mineOre(VARROCK_EAST_MINE_WORLD_AREA, VARROCK_EAST_MINE_WORLD_POINT, "copper rocks", "copper ore")

            } else if (minimumSkillRequirement < 70) {
                Alfred.setTaskSubStatus("Mining Iron")
                mineOre(VARROCK_EAST_MINE_WORLD_AREA, VARROCK_EAST_MINE_WORLD_POINT, "iron rocks", "iron ore")

            } else {
                return
            }
            Alfred.sleep(100)
        }
    }

    private fun mineOre(oreArea: WorldArea, orePoint: WorldPoint, oreName: String, itemName: String) {
        val player = Alfred.api.players().localPlayer

        if (player.isMoving || player.isInteracting) {
            return
        }

        when (scriptState) {
            ScriptState.SETUP -> {
                if (!isWieldingRecommendedPickaxe) {
                    val pickaxe = recommendedPickaxeFromInventory
                    if (pickaxe != null) {
                        val inventoryCount = Alfred.api.inventory().count()
                        pickaxe.leftClick()
                        Alfred.sleepUntil({ Alfred.api.inventory().count() == inventoryCount - 1 }, 100, 3000)

                    } else {
                        Alfred.setStatus("Going to get an pickaxe")
                        Alfred.api.walk().walkTo(bankLocation)

                        val itemsAndQuantities: MutableList<Pair<Int, Int>> = mutableListOf()

                        for (itemIds in recommendedItems) {
                            for (itemId in itemIds) {
                                itemsAndQuantities.add(Pair(itemId, 1))
                            }
                        }

                        Alfred.tasks.banking.withdrawItems(itemsAndQuantities)
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
                Alfred.tasks.mining.findAndMineOre(oreName)
                Alfred.tasks
                scriptState = ScriptState.BANKING
            }

            ScriptState.BANKING -> {
                if (config.keepOre()) {
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

    private val recommendedPickaxes: List<Int>
        get() {
            val player = Alfred.api.players().localPlayer
            val skillLevel = player.getSkillLevel(Skill.MINING)
            val itemIds: MutableList<Int> = ArrayList()

            if (skillLevel >= 1) {
                itemIds.add(ItemID.BRONZE_PICKAXE)
                itemIds.add(ItemID.IRON_PICKAXE)
            }
            if (skillLevel >= 6) {
                itemIds.add(ItemID.STEEL_PICKAXE)
            }
            if (skillLevel >= 11) {
                itemIds.add(ItemID.BLACK_PICKAXE)
            }
            if (skillLevel >= 21) {
                itemIds.add(ItemID.MITHRIL_PICKAXE)
            }
            if (skillLevel >= 31) {
                itemIds.add(ItemID.ADAMANT_PICKAXE)
            }

            if (skillLevel >= 41) {
                itemIds.add(ItemID.RUNE_PICKAXE)
            }
            return itemIds
        }

    private val isWieldingRecommendedPickaxe: Boolean
        get() {
            Alfred.setStatus("Checking for pickaxe")
            return if (!Alfred.api.equipment().isWeaponEquipped) {
                false
            } else recommendedPickaxes.contains(Alfred.api.equipment().weaponId)
        }
    private val recommendedPickaxeFromInventory: RSInventoryItem?
        get() {
            val inventoryItems = Alfred.api.inventory().items
            for (recommendedItemId in recommendedPickaxes) {
                for (rsInventoryItem in inventoryItems) {
                    if (rsInventoryItem.id == recommendedItemId) {
                        return rsInventoryItem
                    }
                }
            }
            return null
        }
}
