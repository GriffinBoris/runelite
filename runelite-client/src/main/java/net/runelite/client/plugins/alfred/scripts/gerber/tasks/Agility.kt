package net.runelite.client.plugins.alfred.scripts.gerber.tasks

import net.runelite.api.ObjectID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.models.inventory.InventoryRequirements
import net.runelite.client.plugins.alfred.api.rs.objects.RSObject
import net.runelite.client.plugins.alfred.enums.WorldDestinations
import net.runelite.client.plugins.alfred.scripts.gerber.GerberConfig
import net.runelite.client.plugins.alfred.scripts.gerber.GerberThread

class Agility(private val config: GerberConfig) : BaseTask() {
    companion object {
        private val TREE_GNOME_STRONG_HOLD_WORLD_AREA = WorldArea(2468, 3414, 23, 27, 0)
        private val TREE_GNOME_STRONG_HOLD_WORLD_POINT = WorldPoint(2474, 3437, 0)
        private val TREE_GNOME_STRONG_HOLD_WAIT_ARE_ONE = WorldArea(2483, 3432, 6, 4, 0)

    }

    private enum class Course {
        TREE_GNOME_STRONGHOLD
    }

    private enum class ScriptState {
        RUNNING, SETUP
    }

    private var scriptState: ScriptState
    private var course: Course

    private var currentTreeGnomeStrongholdObjectId = 0

    init {
        Alfred.status = "Training Agility"
        scriptState = ScriptState.SETUP
        course = Course.TREE_GNOME_STRONGHOLD
    }

    override fun setup() {
        return
    }

    override fun getBankLocation(): WorldPoint {
        return WorldDestinations.VARROCK_WEST_BANK.worldPoint
    }

    override fun getInventoryRequirements(): InventoryRequirements {
        return InventoryRequirements()
    }

    override fun shouldTrain(): Boolean {
        val player = Alfred.api.players.localPlayer
        val skillLevel = player.getSkillLevel(Skill.AGILITY)
        return skillLevel < config.agilityLevel()
    }

    override fun process(): Boolean {
        val player = Alfred.api.players.localPlayer
        val minimumSkillRequirement = player.getSkillLevel(Skill.AGILITY)

        if (minimumSkillRequirement < 10) {
            Alfred.taskSubStatus = "Tree Gnome Stronghold"
            GerberThread.countLabel = "Loops"

            doAgility(TREE_GNOME_STRONG_HOLD_WORLD_AREA, TREE_GNOME_STRONG_HOLD_WORLD_POINT, Course.TREE_GNOME_STRONGHOLD)

        } else {
            return false
        }

        return true
    }

    private fun doAgility(agilityArea: WorldArea, agilityPoint: WorldPoint, course: Course) {
        val player = Alfred.api.players.localPlayer

        if (player.isMoving || player.isInteracting) {
            return
        }

        when (scriptState) {
            ScriptState.SETUP -> {
                if (!agilityArea.contains(player.worldLocation)) {
                    Alfred.api.walk.walkTo(agilityPoint)
                }
                scriptState = ScriptState.RUNNING
            }

            ScriptState.RUNNING -> {
                if (course == Course.TREE_GNOME_STRONGHOLD) {
                    doTreeGnomeStronghold()
                }
            }
        }
    }

    private fun doTreeGnomeStronghold() {
        if (currentTreeGnomeStrongholdObjectId == 0) {
            currentTreeGnomeStrongholdObjectId = ObjectID.LOG_BALANCE_23145
        }

        val player = Alfred.api.players.localPlayer

        val foundObject = Alfred.api.objects.objectsFromTiles
            .filterNotNull()
            .filter { rsObject: RSObject -> rsObject.id == currentTreeGnomeStrongholdObjectId }
            .firstOrNull()

        if (foundObject != null) {

            if (foundObject.worldLocation.distanceTo(player.worldLocation) >= 4) {
                val minimapPoint = Alfred.api.miniMap.getWorldPointToScreenPoint(foundObject.worldLocation)
                if (minimapPoint != null) {
                    Alfred.mouse.leftClick(minimapPoint)
                    Alfred.sleepUntil({ player.isAnimating }, 100, 1000 * 5)
                    Alfred.sleepUntil({ !player.isMoving && player.isIdle && !player.isAnimating }, 100, 1000 * 5)
                } else {
                    return
                }
            }

            Alfred.mouse.leftClick(foundObject.convexHull.bounds)
            Alfred.sleepUntil({ player.isAnimating }, 100, 1000 * 10)
            Alfred.sleepUntil({ !player.isMoving && player.isIdle && !player.isAnimating }, 100, 1000 * 10)
            Alfred.sleepUntil({ !TREE_GNOME_STRONG_HOLD_WAIT_ARE_ONE.contains(player.worldLocation) }, 100, 1000 * 10)
        } else {
            return
        }

        if (currentTreeGnomeStrongholdObjectId == ObjectID.OBSTACLE_PIPE_23138) {
            GerberThread.count++
        }

        when (currentTreeGnomeStrongholdObjectId) {
            ObjectID.LOG_BALANCE_23145 -> currentTreeGnomeStrongholdObjectId = ObjectID.OBSTACLE_NET_23134
            ObjectID.OBSTACLE_NET_23134 -> currentTreeGnomeStrongholdObjectId = ObjectID.TREE_BRANCH_23559
            ObjectID.TREE_BRANCH_23559 -> currentTreeGnomeStrongholdObjectId = ObjectID.BALANCING_ROPE_23557
            ObjectID.BALANCING_ROPE_23557 -> currentTreeGnomeStrongholdObjectId = ObjectID.TREE_BRANCH_23560
            ObjectID.TREE_BRANCH_23560 -> currentTreeGnomeStrongholdObjectId = ObjectID.OBSTACLE_NET_23135
            ObjectID.OBSTACLE_NET_23135 -> currentTreeGnomeStrongholdObjectId = ObjectID.OBSTACLE_PIPE_23138
            ObjectID.OBSTACLE_PIPE_23138 -> currentTreeGnomeStrongholdObjectId = ObjectID.LOG_BALANCE_23145
        }

    }
}
