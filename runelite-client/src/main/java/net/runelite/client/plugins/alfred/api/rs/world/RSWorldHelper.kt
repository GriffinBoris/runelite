package net.runelite.client.plugins.alfred.api.rs.world

import net.runelite.api.Constants
import net.runelite.api.Tile
import net.runelite.client.plugins.alfred.Alfred

class RSWorldHelper {
    companion object {
        private const val MAX_DISTANCE = 2400
    }

    private fun internalGetTiles(): List<Tile> {
        return Alfred.getClientThread().invokeOnClientThread<List<Tile>> {
            val tileList: MutableList<Tile> = ArrayList()
            val player = Alfred.getClient().localPlayer
            val tiles = Alfred.getClient().scene.tiles
            val z = Alfred.getClient().getPlane()

            for (x in 0 until Constants.SCENE_SIZE) {
                for (y in 0 until Constants.SCENE_SIZE) {
                    val tile = tiles[z][x][y] ?: continue
                    if (player.localLocation.distanceTo(tile.localLocation) <= MAX_DISTANCE) {
                        tileList.add(tile)
                    }
                }
            }
            return@invokeOnClientThread tileList
        }
    }

    val tiles: List<Tile>
        get() = internalGetTiles()
}
