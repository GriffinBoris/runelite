package net.runelite.client.plugins.alfred.api.rs.quest

import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.npc.RSNpc
import net.runelite.client.plugins.alfred.api.rs.objects.RSObject
import net.runelite.client.plugins.questhelper.steps.NpcStep
import net.runelite.client.plugins.questhelper.steps.ObjectStep
import net.runelite.client.plugins.questhelper.steps.QuestStep
import java.awt.event.KeyEvent

class RSQuestHelper {
//    click here to continue, text, Id	14221317
//    dialogue_player_text, text
//    dialogue_option_options, children, text
    fun doQuest(): Boolean {
//        val quest = RomeoAndJuliet()
//        quest.setupConditions()
//        val steps = quest.loadSteps()
//        val sortedSteps = steps.toSortedMap()
//
//        for (step in sortedSteps.values) {
//
//            if (step is ConditionalStep) {
//                for (conditionalStep in step.steps) {
//                    performStep(step)
//                }
//
//            } else {
//                performStep(step)
//            }
//
//        }


//        RSQuestAdapter.getCurrentStep(quest)

        val quest = RSQuestAdapter.getSelectedQuest()
        while (true) {
            val step = RSQuestAdapter.getCurrentStep(quest)

            if (Alfred.clientThread.invokeOnClientThread { quest.isCompleted }) {
                break
            }

            performStep(step)

        }

        return true
    }

    private fun performStep(step: QuestStep) {
        while (true) {
            println("Performing Step: ${RSQuestAdapter.getText(step)}")

            if (clickContinue()) {
                continue
            }

            if (clickOption()) {
                continue
            }

            var success = false
            if (step is ObjectStep) {
                success = performObjectStep(step)

            } else if (step is NpcStep) {
                success = performNpcStep(step)
            }

            if (success) {
                return
            }

            Alfred.sleep(500)
        }
    }

    private fun performObjectStep(step: ObjectStep): Boolean {
        val objectName = Alfred.api.objects.getObjectIdVariableName(step.objectID)
        if (objectName!!.startsWith("stair", ignoreCase = true) || objectName.startsWith("door", ignoreCase = true)) {
            return true
        }

        val worldPoint = RSQuestAdapter.getStepWorldPoint(step)
        val player = Alfred.api.players.localPlayer

        val rsObject = Alfred.api.objects.objectsFromTiles
            .filter { rsObject: RSObject -> rsObject.id == step.objectID }
            .firstOrNull()

        if (rsObject == null) {
            Alfred.api.walk.walkTo(worldPoint)
            return false
        }

        if (worldPoint.distanceTo(Alfred.api.players.localPlayer.worldLocation) >= 3) {
            Alfred.api.walk.walkTo(worldPoint)
        }

        return if (!rsObject.leftClick()) {
            Alfred.sleep(500)
            Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)

        } else {
            false
        }
    }

    private fun performNpcStep(step: NpcStep): Boolean {
        val worldPoint = RSQuestAdapter.getStepWorldPoint(step)
        val player = Alfred.api.players.localPlayer

        val rsNpc = Alfred.api.npcs.npcs.firstOrNull { rsNpc: RSNpc -> rsNpc.id == step.npcID }
        if (rsNpc == null) {
            Alfred.api.walk.walkTo(worldPoint)
            return false
        }

        if (worldPoint.distanceTo(Alfred.api.players.localPlayer.worldLocation) >= 3) {
            Alfred.api.walk.walkTo(worldPoint)
        }

        return if (Alfred.tasks.npc.findAndInteract(rsNpc.id, "talk-to")) {
            Alfred.sleep(500)
            Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)

        } else {
            false
        }
    }

    private fun clickContinue(): Boolean {
        val success = Alfred.clientThread.invokeOnClientThread {
            val clickToContinueWidget = Alfred.api.widgets.allWidgets
                .filter { widget: Widget -> !widget.isHidden && !widget.isSelfHidden }
                .filter { widget: Widget -> widget.text.contains("click here to continue", ignoreCase = true) }
                .firstOrNull() ?: return@invokeOnClientThread false

            Alfred.mouse.leftClick(clickToContinueWidget.bounds)
            return@invokeOnClientThread true
        }

        Alfred.sleep(1000)
        return success
    }

    private fun clickOption(): Boolean {
        val success = Alfred.clientThread.invokeOnClientThread {
            val optionsWidget = Alfred.api.widgets.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS) ?: return@invokeOnClientThread false

            Alfred.keyboard.pressKey(KeyEvent.VK_1)
            Alfred.sleep(20, 100)
            Alfred.keyboard.releaseKey(KeyEvent.VK_1)
            return@invokeOnClientThread true
        }

        Alfred.sleep(1000)
        return success
    }
}