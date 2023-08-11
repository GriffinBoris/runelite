package net.runelite.client.plugins.alfred.api.rs.walk.pathfinder

import net.runelite.api.Tile
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.walk.WorldMovementFlag

class LiveWorldDataLoader {
    private val nodes: MutableList<PathNode> = mutableListOf()


    fun getGrid(): Array<Array<Array<PathNode?>>> {
        readTiles()

        val maxX = nodes.maxBy { pathNode -> pathNode.worldLocation.x }.worldLocation.x
        val maxY = nodes.maxBy { pathNode -> pathNode.worldLocation.y }.worldLocation.y
        val maxZ = nodes.maxBy { pathNode -> pathNode.worldLocation.plane }.worldLocation.plane

        val grid = Array(maxZ + 1) { Array(maxY + 1) { arrayOfNulls<PathNode>(maxX + 1) } }

        for (node in nodes) {
            grid[node.worldLocation.plane][node.worldLocation.y][node.worldLocation.x] = node
        }

        return grid
    }

    private fun readTiles(): MutableList<PathNode> {
        val tiles = Alfred.api.world().tiles
        for (tile in tiles) {
            val collisionMap = getMovementFlagsForTile(tile)
            val blocked = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_OBJECT) || collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_FLOOR_DECORATION) || collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_FLOOR) || collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_FULL)

            val node = PathNode(
                id = -1,
                gCost = 0,
                hCost = 0,
                parent = null,
                penalty = 0,
                pathTransports = mutableListOf(),
                worldLocation = tile.worldLocation,
                operableName = "",
                isOperable = false,
                blocked = blocked,
                blockedMovementNorth = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_NORTH),
                blockedMovementSouth = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_SOUTH),
                blockedMovementEast = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_EAST),
                blockedMovementWest = collisionMap.contains(WorldMovementFlag.BLOCK_MOVEMENT_WEST),
            )
            nodes.add(node)
        }

        return nodes
    }

    private fun getMovementFlagsForTile(tile: Tile): Set<WorldMovementFlag> {
        val client = Alfred.getClient()
        val collisionMap = client.collisionMaps ?: return HashSet()

        val flags: Array<IntArray> = collisionMap[client.getPlane()].getFlags()
        val data = flags[tile.sceneLocation.x][tile.sceneLocation.y]
        return WorldMovementFlag.getSetFlags(data)
    }

}
