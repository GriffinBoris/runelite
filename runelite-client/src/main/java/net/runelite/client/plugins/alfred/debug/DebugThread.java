package net.runelite.client.plugins.alfred.debug;


import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.bank.RSBank;
import net.runelite.client.plugins.alfred.device.Keyboard;
import net.runelite.client.plugins.alfred.device.Mouse;

import java.awt.*;
import java.util.Optional;

public class DebugThread extends Thread {

    Mouse mouse = Alfred.getMouse();
    Keyboard keyboard = Alfred.getKeyboard();
    Canvas canvas = Alfred.getClient().getCanvas();

    public DebugThread() {
        this.setDaemon(true);
    }


    @Override
    public void run() {
//        Optional<RSBank> rsBank = Alfred.api.banks().getNearest().stream().findFirst();
//        rsBank.get().open();
//        while (true) {
//            Alfred.sleep(2000);
//            int randomX = (int) (Math.random() * canvas.getWidth());
//            int randomY = (int) (Math.random() * canvas.getHeight());
//            mouse.move(randomX, randomY);

//            Alfred.sleep(1000);
//            Player player = Alfred.getClient().getLocalPlayer();
//            PlayerAPI playerAPI = new PlayerAPI(player);
//            playerAPI.setRunning();
//            keyboard.sendKeys("Hello world! ");

//            TabAPI.test();
//            BankAPI.test();

//            RSBank.getNearestBanks().forEach(bankAPI -> {
//                bankAPI.openBank();
//            });
//        }
    }
}