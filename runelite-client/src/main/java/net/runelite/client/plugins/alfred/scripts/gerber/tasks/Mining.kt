package net.runelite.client.plugins.alfred.scripts.gerber.tasks

import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.enums.WorldDestinations
import net.runelite.client.plugins.alfred.scripts.gerber.GerberConfig

class Mining(private val config: GerberConfig) : BaseTask() {
    companion object {
        private val VARROCK_EAST_MINE_WORLD_AREA = WorldArea(3281, 3361, 9, 9, 0)
        private val VARROCK_EAST_MINE_WORLD_POINT = WorldPoint(3287, 3365, 0);
    }

    private enum class ScriptState {
        SETUP, WAITING, MINING, BANKING
    }

    private var scriptState: ScriptState

    init {
        scriptState = ScriptState.WAITING
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
                Alfred.setTaskSubStatus("Mining Copper")
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
            ScriptState.SETUP -> TODO()
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
}
