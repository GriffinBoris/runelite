package net.runelite.client.plugins.alfred.api.rs.player

import net.runelite.api.Player
import net.runelite.client.plugins.alfred.Alfred

class RSPlayerHelper {
    private fun internalGetPlayers(): List<RSPlayer> {
        return Alfred.clientThread.invokeOnClientThread {
            return@invokeOnClientThread Alfred.client.players.filterNotNull().map { player: Player -> RSPlayer(player) }.toList()
        }
    }

    val players: List<RSPlayer>
        get() = internalGetPlayers()

    fun getPlayerByName(name: String): RSPlayer? {
        return internalGetPlayers().filter { rsPlayer: RSPlayer -> rsPlayer.name == name }.firstOrNull()
    }

    val localPlayer: RSPlayer
        get() = RSPlayer(Alfred.client.localPlayer)
}
