package net.runelite.client.plugins.alfred.scripts.gerber.tasks;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.bank.RSBank;
import net.runelite.client.plugins.alfred.api.rs.player.RSPlayer;

import java.util.List;

public abstract class BaseTask {
    abstract WorldPoint getBankLocation();

    void retrieveRecommendedItems(List<List<Integer>> listOfItems) {

        RSPlayer player = Alfred.api.players().getLocalPlayer();

        // if we are too far away from the bank we don't have it open
        if (player.getWorldLocation().distanceTo(getBankLocation()) > 5) {
            Alfred.api.walk().walkTo(getBankLocation());
        }

        // if the bank is not open then open it
        if (!Alfred.api.banks().isOpen()) {
            RSBank bank = Alfred.api.banks().getNearestBanks().stream().findFirst().orElse(null);
            Alfred.api.banks().open(bank);
        }

        for (List<Integer> itemIds : listOfItems) {
            for (Integer itemId : itemIds) {
                if (Alfred.api.banks().containsItem(itemId)) {
                    if (Alfred.api.banks().withdrawItem(itemId)) {
                        Alfred.sleep(100, 200);
                        break;
                    }
                }
            }
        }

        Alfred.sleep(250, 750);
        Alfred.api.banks().close();
        Alfred.sleep(250, 750);
    }
}
