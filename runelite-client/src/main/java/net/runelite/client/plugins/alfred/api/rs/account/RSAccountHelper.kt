package net.runelite.client.plugins.alfred.api.rs.account

import net.runelite.api.GameState
import net.runelite.client.config.ConfigProfile
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.util.WorldUtil

class RSAccountHelper {
    companion object {
        private const val LOGOUT_WIDGET_ID = 11927560
    }

    fun logout() {
        Alfred.api.tabs.clickLogoutTab()
        Alfred.api.widgets.leftClickWidget(LOGOUT_WIDGET_ID)
        Alfred.sleepUntil({ Alfred.client.getGameState() == GameState.LOGIN_SCREEN }, 500, 5000)
    }

    private fun internalLogin(worldNumber: Int) {
        val profile = RSAccountHelperAdapter.getProfiles().firstOrNull { configProfile: ConfigProfile -> RSAccountHelperAdapter.getProfileIsActive(configProfile) }
        if (profile == null) {
            println("No active profiles")
        }

        Alfred.sleep(300, 600)
        Alfred.keyboard.pressEnter()

        Alfred.sleep(300, 600)
        Alfred.client.setUsername(RSAccountHelperAdapter.getProfileName(profile))

        Alfred.sleep(300, 600)
        Alfred.client.setPassword(RSAccountHelperAdapter.getProfilePassword(profile))

        Alfred.sleep(300, 600)
        changeWorld(worldNumber)

        Alfred.sleep(300, 600)
        Alfred.keyboard.pressEnter()

        Alfred.sleepUntil({ Alfred.client.getGameState() == GameState.LOGGED_IN }, 100, 1000 * 30)
        Alfred.sleep(500, 2000)

        Alfred.api.widgets.leftClickWidget(24772685)
        Alfred.sleep(1000)
    }

    fun login() {
        internalLogin(305)
    }

    fun login(worldNumber: Int) {
        internalLogin(worldNumber)
    }

    fun changeWorld(worldNumber: Int) {
        val worldResult = Alfred.worldService.getWorlds()
        val world = worldResult!!.findWorld(worldNumber) ?: return

        Alfred.clientThread.invokeOnClientThread<Any?> {
            val newWorld = Alfred.client.createWorld()
            newWorld.setActivity(world.activity)
            newWorld.setAddress(world.address)
            newWorld.setId(world.id)
            newWorld.setPlayerCount(world.players)
            newWorld.setLocation(world.location)
            newWorld.setTypes(WorldUtil.toWorldTypes(world.types))
            Alfred.client.changeWorld(newWorld)
            null
        }
    }
}
