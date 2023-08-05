package net.runelite.client.plugins.alfred.api.rs.account;

import net.runelite.api.GameState;
import net.runelite.client.config.ConfigProfile;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

public class RSAccountHelper {
    private final int LOGOUT_WIDGET_ID = 11927560;

    public RSAccountHelper() {
    }

    public void logout() {
        Alfred.api.tabs().clickLogoutTab();
        Alfred.api.widgets().leftClickWidget(LOGOUT_WIDGET_ID);
        Alfred.sleepUntil(() -> Alfred.getClient().getGameState() == GameState.LOGIN_SCREEN, 500, 5000);
    }

    public void login() {
        ConfigProfile profile = Alfred.getProfileManager().lock().getProfiles().stream().filter(configProfile -> configProfile.isActive()).findFirst().orElse(null);
        if (profile == null) {
            System.out.println("No active profiles");
        }

        Alfred.sleep(300, 600);
        Alfred.getKeyboard().pressEnter();
        changeWorld(326);
//        changeWorld(316);
        Alfred.sleep(300, 600);
        Alfred.getClient().setUsername(profile.getName());
        Alfred.sleep(300, 600);
        Alfred.getClient().setPassword(profile.getPassword());
        Alfred.sleep(300, 600);
        Alfred.getKeyboard().pressEnter();
        Alfred.sleepUntil(() -> Alfred.getClient().getGameState() == GameState.LOGGED_IN, 100, 1000 * 15);
        Alfred.sleep(500, 2000);
        Alfred.api.widgets().leftClickWidget(24772685);
        Alfred.sleep(1000);
    }

    public void changeWorld(int worldNumber) {
        WorldResult worldResult = Alfred.getWorldService().getWorlds();

        World world = worldResult.findWorld(worldNumber);
        if (world == null) {
            return;
        }

        Alfred.getClientThread().invokeOnClientThread(() -> {
            net.runelite.api.World newWorld = Alfred.getClient().createWorld();
            newWorld.setActivity(world.getActivity());
            newWorld.setAddress(world.getAddress());
            newWorld.setId(world.getId());
            newWorld.setPlayerCount(world.getPlayers());
            newWorld.setLocation(world.getLocation());
            newWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
            Alfred.getClient().changeWorld(newWorld);
            return null;
        });
    }
}
