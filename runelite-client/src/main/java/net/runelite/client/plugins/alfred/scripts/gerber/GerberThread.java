package net.runelite.client.plugins.alfred.scripts.gerber;

import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.bank.RSBank;
import net.runelite.client.plugins.alfred.api.rs.player.RSPlayer;
import net.runelite.client.plugins.alfred.scripts.gerber.tasks.Combat;
import net.runelite.client.plugins.alfred.scripts.gerber.tasks.Mining;
import net.runelite.client.plugins.alfred.scripts.gerber.tasks.Woodcutting;

public class GerberThread extends Thread {

    private final GerberConfig config;

    public GerberThread(GerberConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        login();
        Alfred.getPlayTimer().setRandomTimeout(15, 90);
        Alfred.getPlayTimer().start();


//        System.out.println(Alfred.api.equipment().isWeaponEquipped());

//        Alfred.setTaskStatus("Training Mining");
//        Mining mining = new Mining(config);
//        mining.run();

        Alfred.setTaskStatus("Training Woodcutting");
        Woodcutting woodcutting = new Woodcutting(config);
        woodcutting.run();
//
//        if (trainCombat()) {
//            Alfred.setTaskStatus("Training Combat");
//            Combat combatTask = new Combat(config);
//            combatTask.run();
//        }
//
//        Alfred.setTaskStatus("Stopping");
    }

    private void login() {
        if (Alfred.getClient().getGameState() != GameState.LOGGED_IN) {
            Alfred.api.account().login();
            Alfred.sleep(2000);
        }
    }

    private void logout() {
        if (Alfred.getClient().getGameState() == GameState.LOGGED_IN) {
            Alfred.api.account().logout();
        }
    }

    private boolean trainCombat() {
        RSPlayer player = Alfred.api.players().getLocalPlayer();

        if (player.getSkillLevel(Skill.ATTACK) < config.attackLevel()) {
            return true;
        }

        if (player.getSkillLevel(Skill.STRENGTH) < config.attackLevel()) {
            return true;
        }

        if (player.getSkillLevel(Skill.DEFENCE) < config.attackLevel()) {
            return true;
        }

        return false;
    }

    private boolean trainMining() {
        RSPlayer player = Alfred.api.players().getLocalPlayer();
        return player.getSkillLevel(Skill.MINING) < config.miningLevel();
    }

    private boolean trainWoodcutting() {
        RSPlayer player = Alfred.api.players().getLocalPlayer();
        return player.getSkillLevel(Skill.WOODCUTTING) < config.woodcuttingLevel();
    }
}