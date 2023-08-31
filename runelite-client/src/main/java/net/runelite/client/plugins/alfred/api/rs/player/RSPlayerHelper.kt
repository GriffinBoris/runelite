package net.runelite.client.plugins.alfred.api.rs.player

import net.runelite.api.Player
import net.runelite.client.plugins.alfred.Alfred

class RSPlayerHelper {
    private fun internalGetPlayers(): List<RSPlayer> {
        return Alfred.clientThread.invokeOnClientThread {
            return@invokeOnClientThread Alfred.client.players.filterNotNull().map { player: Player -> RSPlayer(player) }
        }
    }

    val players: List<RSPlayer>
        get() = internalGetPlayers()

    fun getPlayerByName(name: String): RSPlayer? {
        return internalGetPlayers().firstOrNull { rsPlayer: RSPlayer -> rsPlayer.name == name }
    }

    val localPlayer: RSPlayer
        get() = RSPlayer(Alfred.client.localPlayer)
}
