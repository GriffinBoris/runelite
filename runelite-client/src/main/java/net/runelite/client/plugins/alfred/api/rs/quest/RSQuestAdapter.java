package net.runelite.client.plugins.alfred.api.rs.quest;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.questhelper.questhelpers.QuestHelper;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.questhelper.steps.QuestStep;

import java.util.List;

public class RSQuestAdapter {

    public static String getText(QuestStep questStep) {
        List<String> texts = questStep.getText();
        if (texts == null) {
            return "No Text";
        }

        if (texts.isEmpty()) {
            return "No Text";
        }

        return texts.get(0);
    }

    public static WorldPoint getStepWorldPoint(NpcStep questStep) {
        return questStep.getWorldPoint();
    }

    public static WorldPoint getStepWorldPoint(ObjectStep questStep) {
        return questStep.getWorldPoint();
    }

    public static QuestHelper getSelectedQuest() {
        return QuestHelperPlugin.getSelectedQuest();
    }

    public static QuestStep getCurrentStep(QuestHelper questHelper) {
        return questHelper.getCurrentStep();
    }
}
