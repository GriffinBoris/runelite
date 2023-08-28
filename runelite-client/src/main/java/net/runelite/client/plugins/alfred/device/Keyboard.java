package net.runelite.client.plugins.alfred.device;

import net.runelite.client.plugins.alfred.Alfred;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Keyboard {

    private static final Map<Integer, Boolean> pressedKeys = new HashMap<>();

    static {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
            synchronized (Keyboard.class) {
                if (event.getID() == KeyEvent.KEY_PRESSED) {
                    pressedKeys.put(event.getKeyCode(), true);
                } else if (event.getID() == KeyEvent.KEY_RELEASED) {
                    pressedKeys.put(event.getKeyCode(), false);
                }
            }
            return false;
        });
    }

    private final Canvas gameCanvas = Alfred.Companion.getClient().getCanvas();

    public boolean isKeyPressed(int key) {
        return pressedKeys.getOrDefault(key, false);
    }


    public void sendKeys(String text) {
        for (char c : text.toCharArray()) {

            boolean isUpperCase = Character.isUpperCase(c);

            if (isUpperCase) {
                holdShift();
            }
            KeyEvent keyEvent = new KeyEvent(gameCanvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, c);
            Alfred.Companion.getEventHandler().dispatchUnblockedEvent(keyEvent);

//            pressKey(c);
            Alfred.Companion.sleep(100, 200);
//            releaseKey(c);

            if (isUpperCase) {
                releaseShift();
            }
        }
    }

    public void pressKey(int key) {
        char c = (char) key;
        long time = System.currentTimeMillis();

        KeyEvent keyEventPressed = new KeyEvent(gameCanvas, KeyEvent.KEY_PRESSED, time, 0, key, c, KeyEvent.KEY_LOCATION_STANDARD);
        KeyEvent keyEventTyped = new KeyEvent(gameCanvas, KeyEvent.KEY_TYPED, time, 0, 0, c, KeyEvent.KEY_LOCATION_UNKNOWN);

        Alfred.Companion.getEventHandler().dispatchUnblockedEvent(keyEventPressed);
        Alfred.Companion.getEventHandler().dispatchUnblockedEvent(keyEventTyped);
    }

    public void releaseKey(int key) {
        char c = (char) key;
        KeyEvent keyEvent = new KeyEvent(gameCanvas, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, key, c, KeyEvent.KEY_LOCATION_STANDARD);
        Alfred.Companion.getEventHandler().dispatchUnblockedEvent(keyEvent);
    }

    public void holdShift() {
        Alfred.Companion.sleep(30, 200);
        KeyEvent keyEvent = new KeyEvent(gameCanvas, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_LEFT);
        Alfred.Companion.getEventHandler().dispatchUnblockedEvent(keyEvent);
    }

    public void releaseShift() {
        Alfred.Companion.sleep(30, 200);
        KeyEvent keyEvent = new KeyEvent(gameCanvas, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_LEFT);
        Alfred.Companion.getEventHandler().dispatchUnblockedEvent(keyEvent);
    }

    public void pressEnter() {
        pressKey(KeyEvent.VK_ENTER);
        Alfred.Companion.sleep(20, 100);
        releaseKey(KeyEvent.VK_ENTER);
    }

    public void pressSpace() {
        pressKey(KeyEvent.VK_SPACE);
        Alfred.Companion.sleep(20, 100);
        releaseKey(KeyEvent.VK_SPACE);
    }

    public void holdArrowKey(int key) {
        Alfred.Companion.sleep(30, 100);
        KeyEvent keyEvent = new KeyEvent(gameCanvas, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, key);
        Alfred.Companion.getEventHandler().dispatchUnblockedEvent(keyEvent);
    }

    public void releaseArrowKey(int key) {
        Alfred.Companion.sleep(30, 100);
        KeyEvent keyEvent = new KeyEvent(gameCanvas, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, key);
        Alfred.Companion.getEventHandler().dispatchUnblockedEvent(keyEvent);
    }

}
