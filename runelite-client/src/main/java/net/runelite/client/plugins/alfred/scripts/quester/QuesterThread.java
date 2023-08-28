package net.runelite.client.plugins.alfred.scripts.quester;

import net.runelite.api.GameState;
import net.runelite.api.ObjectID;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.bank.RSBank;
import net.runelite.client.plugins.alfred.api.rs.npc.RSNpc;
import net.runelite.client.plugins.alfred.api.rs.objects.RSObject;
import net.runelite.client.plugins.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.questhelper.steps.QuestStep;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class QuesterThread extends Thread {

    public static List<ItemRequirement> itemRequirements = new ArrayList<>();
    public static List<ItemRequirement> itemsMissing = new ArrayList<>();

    @Override
    public void run() {
        if (Alfred.client.getGameState() != GameState.LOGGED_IN) {
            Alfred.Companion.getApi().getAccount().login();
            Alfred.Companion.sleep(2000);
        }
        Alfred.Companion.getApi().getCamera().setPitch(1.0f);
        Alfred.Companion.getApi().getCamera().setYaw(315);

        Alfred.Companion.getApi().getQuest().doQuest();

//        while (true) {
//            if (QuestHelperPlugin.getSelectedQuest() != null && !Alfred.Companion.getClientThread().invokeOnClientThread(() -> QuestHelperPlugin.getSelectedQuest().isCompleted())) {
//                if (Alfred.Companion.getApi().getWidgets().findWidget("click here to continue", null, false) != null) {
//                    Alfred.Companion.getKeyboard().pressKey(KeyEvent.VK_ENTER);
//                    Alfred.Companion.sleep(20, 100);
//                    Alfred.Companion.getKeyboard().releaseKey(KeyEvent.VK_ENTER);
//                    continue;
//                }
//
//                if (Alfred.Companion.getApi().getWidgets().findWidget("select an option", null, false) != null) {
//                    Alfred.Companion.getKeyboard().pressKey(KeyEvent.VK_1);
//                    Alfred.Companion.sleep(20, 100);
//                    Alfred.Companion.getKeyboard().releaseKey(KeyEvent.VK_1);
//                    continue;
//                }
//
//                if (!applyStep(null)) return;
//
//                if (QuestHelperPlugin.getSelectedQuest().getCurrentStep() instanceof ConditionalStep) {
//                    ConditionalStep conditionalStep = (ConditionalStep) QuestHelperPlugin.getSelectedQuest().getCurrentStep();
//                    for (QuestStep step : conditionalStep.getSteps()) {
//                        applyStep(step);
//                    }
//                }
//
//                for (ItemRequirement itemRequirement : QuestHelperPlugin.getSelectedQuest().getItemRequirements()) {
//                    if (Alfred.Companion.getApi().getInventory().getItems(itemRequirement.getId()).stream().count() < itemRequirement.getQuantity()) {
//                        itemsMissing.add(itemRequirement);
//                    }
//                }
//
//                if (itemsMissing.size() > 0) {
//                    RSBank nearestBank = Alfred.Companion.getApi().getBanks().getNearestBanks().stream().findFirst().orElse(null);
//                    Alfred.Companion.getApi().getWalk().walkTo(nearestBank.getWorldLocation());
//                    Alfred.tasks.getBanking().openBank();
//                    Alfred.tasks.getBanking().depositInventoryAndEquipment();
//
//                    for (ItemRequirement itemRequirement : QuesterThread.itemsMissing) {
//                        if (!Alfred.Companion.getApi().getBanks().containsItem(itemRequirement.getId())) {
//                            System.out.println("missing item");
//                        }
//                        Alfred.tasks.getBanking().withdrawItemX(itemRequirement.getId(), itemRequirement.getQuantity());
//                    }
//                }
//            }
//
//            Alfred.Companion.sleep(500);
//        }
    }

//    public boolean applyStep(QuestStep step) {
//        QuestStep questStep = null;
//        if (step != null)
//            questStep = step;
//        else
//            questStep = QuestHelperPlugin.getSelectedQuest().getCurrentStep();
//
//        if (questStep instanceof ObjectStep) {
//            return applyObjectStep(step);
//        } else if (questStep instanceof NpcStep) {
//            return applyNpcStep(step);
//        }
//        return true;
//    }
//
//    public boolean applyNpcStep(QuestStep step) {
//        NpcStep questStep = null;
//        if (step != null)
//            questStep = (NpcStep) step;
//        else
//            questStep = (NpcStep) QuestHelperPlugin.getSelectedQuest().getCurrentStep();
//
//        RSNpc npc = Alfred.Companion.getApi().getNpcs().getNpcs(questStep.npcID).stream().findFirst().orElse(null);
//
//        if (npc != null) {
//            npc.interact("talk-to");
////            Alfred.tasks.getNpc().findAndInteract()
////        } else {
//            if (questStep.getWorldPoint().distanceTo(Alfred.Companion.getClient().getLocalPlayer().getWorldLocation()) > 3) {
//                Alfred.Companion.getApi().getWalk().walkTo(questStep.getWorldPoint());
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public boolean applyObjectStep(QuestStep step) {
//        ObjectStep questStep = null;
//        if (step != null)
//            questStep = (ObjectStep) step;
//        else
//            questStep = (ObjectStep) QuestHelperPlugin.getSelectedQuest().getCurrentStep();
//
//        String objectName = Alfred.Companion.getApi().getObjects().getObjectIdVariableName(questStep.objectID);
//        if (objectName.toLowerCase().startsWith("stair") || objectName.toLowerCase().startsWith("door")) {
//            return true;
//        }
//
//        ObjectStep finalQuestStep = questStep;
//        RSObject rsFinalObject = Alfred.Companion.getApi().getObjects().getObjectsFromTiles().stream().filter(rsObject -> rsObject.getId() == finalQuestStep.objectID).findFirst().orElse(null);
//
//        if (rsFinalObject != null) {
//            if (!rsFinalObject.leftClick()) {
//                if (questStep.getWorldPoint().distanceTo(Alfred.Companion.getClient().getLocalPlayer().getWorldLocation()) > 3) {
//                    Alfred.Companion.getApi().getWalk().walkTo(questStep.getWorldPoint());
//                    return false;
//                }
//            }
//        } else {
//            Alfred.Companion.getApi().getWalk().walkTo(questStep.getWorldPoint());
//            return false;
//        }
//
//        return true;
//    }
}
