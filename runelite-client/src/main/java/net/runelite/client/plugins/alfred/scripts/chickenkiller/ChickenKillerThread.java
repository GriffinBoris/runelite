package net.runelite.client.plugins.alfred.scripts.chickenkiller;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.item.RSGroundItem;
import net.runelite.client.plugins.alfred.api.rs.npc.RSNpc;
import net.runelite.client.plugins.alfred.api.rs.player.RSPlayer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ChickenKillerThread extends Thread {

    @Override
    public void run() {
        Alfred.Companion.getApi().getAccount().login();
        Alfred.Companion.sleep(5000);
        RSPlayer player = Alfred.Companion.getApi().getPlayers().getLocalPlayer();

        if (!Alfred.Companion.getApi().getCombat().isPunchSelected()) {
            Alfred.Companion.setStatus("Setting attack to punch");
            Alfred.Companion.getApi().getCombat().clickPunch();
            Alfred.Companion.getApi().getTabs().clickInventoryTab();
        }

        Alfred.Companion.setStatus("Dropping any eggs");
        Alfred.Companion.getApi().getInventory().dropAll(ItemID.EGG);

        Alfred.Companion.setStatus("Dropping any raw chicken");
        Alfred.Companion.getApi().getInventory().dropAll(ItemID.RAW_CHICKEN);

        Alfred.Companion.setStatus("Dropping any bones");
        Alfred.Companion.getApi().getInventory().interactAll(ItemID.BONES, "bury");

        while (true) {
            List<RSNpc> chickens = Alfred.Companion.getApi().getNpcs().getAttackableNpcs("chicken").stream().sorted(Comparator.comparingInt(c -> c.getWorldLocation().distanceTo(player.getWorldLocation()))).collect(Collectors.toList());

            RSNpc chicken = chickens.stream().findFirst().orElse(null);

            if (chicken != null) {

                Alfred.Companion.setStatus("Looking at chicken");
                Alfred.Companion.getApi().getCamera().lookAt(chicken.getWorldLocation());
                Alfred.Companion.sleep(100);

//                if (Alfred.Companion.getApi().getScreen().isPointOnScreen(chicken.getWorldLocation())) {
                Alfred.Companion.setStatus("Attacking chicken");
                chicken.attack();
                Alfred.Companion.sleep(500);

                Alfred.Companion.setStatus("Waiting until done attacking chicken");
                Alfred.Companion.sleepUntil(() -> !player.isWalking() && !player.isInteracting(), 50, 1000 * 30);
//                }
            }

            Alfred.Companion.setStatus("Checking for feathers");
            List<RSGroundItem> items = Alfred.Companion.getApi().getItems().getItemsFromTiles(15, ItemID.FEATHER);

            for (RSGroundItem rsGroundItem : items) {
                Alfred.Companion.getApi().getCamera().lookAt(rsGroundItem.getWorldLocation());
                Alfred.Companion.sleep(100, 150);
                Alfred.Companion.setStatus("Taking feather");
                rsGroundItem.clickAction("take");
                Alfred.Companion.sleep(500);
                Alfred.Companion.sleepUntil(() -> !player.isMoving() && !player.isInteracting(), 50, 1000 * 30);
                Alfred.Companion.sleep(1000);
            }

            Alfred.Companion.sleep(500);
        }
    }
}
