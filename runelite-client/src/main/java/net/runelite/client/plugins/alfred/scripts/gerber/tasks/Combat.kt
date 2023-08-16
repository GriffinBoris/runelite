package net.runelite.client.plugins.alfred.scripts.gerber.tasks

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.enums.WorldDestinations
import net.runelite.client.plugins.alfred.scripts.gerber.GerberConfig
import net.runelite.client.plugins.alfred.scripts.gerber.GerberThread.Companion.taskTimer

class Combat(private val config: GerberConfig) : BaseTask() {
    companion object {
        private val LUMBRIDGE_CHICKENS_WORLD_AREA = WorldArea(3225, 3287, 12, 15, 0)
        private val LUMBRIDGE_COWS_WORLD_AREA = WorldArea(3255, 3258, 9, 37, 0)
    }

    private enum class ScriptState {
        WAITING,
        FIGHTING,
        LOOTING
    }

    private var scriptState: ScriptState

    init {
        scriptState = ScriptState.WAITING
    }

    fun run() {
        while (!taskTimer.isTimerComplete()) {
            val minimumSkillRequirement = minimumSkillRequirement

            if (minimumSkillRequirement < 10) {
                Alfred.setTaskSubStatus("Fighting Chickens")
                fightNPC(LUMBRIDGE_CHICKENS_WORLD_AREA, WorldDestinations.LUMBRIDGE_CHICKENS.worldPoint, "chicken", listOf(ItemID.FEATHER, ItemID.BONES))

            } else if (minimumSkillRequirement < 20) {
                Alfred.setTaskSubStatus("Fighting Cows")
                fightNPC(LUMBRIDGE_COWS_WORLD_AREA, WorldDestinations.LUMBRIDGE_COWS.worldPoint, "cow", listOf(ItemID.COWHIDE, ItemID.BONES))

            } else {
                return
            }
            Alfred.sleep(100)
        }
    }

    override fun getBankLocation(): WorldPoint {
        return WorldDestinations.LUMBRIDGE_BANK.worldPoint
    }

    override fun getRequiredItems(): List<Pair<Int, Int>> {
        return emptyList()
    }

    private val minimumSkillRequirement: Int
        get() {
            val player = Alfred.api.players().localPlayer
            return listOf(player.getSkillLevel(Skill.ATTACK), player.getSkillLevel(Skill.STRENGTH), player.getSkillLevel(Skill.DEFENCE)).min()
        }

    private fun fightNPC(worldArea: WorldArea, worldPoint: WorldPoint, npcName: String, itemLootNames: List<Int>) {
        val player = Alfred.api.players().localPlayer

        if (player.isMoving || player.isInteracting) {
            return
        }

        when (scriptState) {
            ScriptState.WAITING -> {
                if (!worldArea.contains(player.worldLocation)) {
                    Alfred.api.walk().walkTo(worldPoint)
                }
                scriptState = ScriptState.FIGHTING
            }

            ScriptState.FIGHTING -> {
                setCombatStyle()
                if (Alfred.tasks.npc.findAndAttack(npcName)) {
                    scriptState = ScriptState.LOOTING
                }
            }

            ScriptState.LOOTING -> {
                if (config.collectItems()) {
                    if (Alfred.api.inventory().isFull) {
                        buryBones()
                        Alfred.sleep(200)
                    }

                    if (Alfred.api.inventory().isFull) {
                        Alfred.api.walk().walkTo(getBankLocation())
                        Alfred.tasks.banking.depositInventory()
                        Alfred.sleep(200)
                    }

                    Alfred.tasks.item.findAndLootItems(itemLootNames, 2)
                }
                scriptState = ScriptState.WAITING
            }
        }

    }

    private fun setCombatStyle() {
        val player = Alfred.api.players().localPlayer

        val attackLevel = player.getSkillLevel(Skill.ATTACK)
        val strengthLevel = player.getSkillLevel(Skill.STRENGTH)
        val defenceLevel = player.getSkillLevel(Skill.DEFENCE)

        if (attackLevel < config.attackLevel()) {
            if (!Alfred.api.combat().isPunchSelected) {
                Alfred.api.combat().clickPunch()
            }
        } else if (strengthLevel < config.strengthLevel()) {
            if (!Alfred.api.combat().isKickSelected) {
                Alfred.api.combat().clickKick()
            }
        } else if (defenceLevel < config.defenseLevel()) {
            if (!Alfred.api.combat().isBlockSelected) {
                Alfred.api.combat().clickBlock()
            }
        }
    }

    private fun buryBones() {
        val player = Alfred.api.players().localPlayer
        if (!config.buryBones() || player.getSkillLevel(Skill.PRAYER) >= config.prayerLevel()) {
            return
        }
        for (item in Alfred.api.inventory().getItems("bones")) {
            item.leftClick()
            Alfred.sleep(1000)
            Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 30)
        }
    }
}
