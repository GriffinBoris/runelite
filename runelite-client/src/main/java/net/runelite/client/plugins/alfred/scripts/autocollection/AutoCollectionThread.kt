package net.runelite.client.plugins.alfred.scripts.autocollection

import net.runelite.api.GameState
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.LiveWorldDataLoader
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathFinder
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathNode
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathWalker
import java.util.stream.Collectors

class AutoCollectionThread(private val config: AutoCollectionConfig) : Thread() {
    companion object {
        @JvmField
        var searching = ""
    }

    private val usedPoints: List<WorldPoint>
    private val unexploredPoints: List<WorldPoint>

    init {
        usedPoints = ArrayList()
        unexploredPoints = parseCoordinates(config.points())
    }

    override fun run() {
        if (Alfred.getClient().getGameState() != GameState.LOGGED_IN) {
            Alfred.api.account().login()
        }
        val player = Alfred.api.players().localPlayer
        val liveWorldDataLoader = LiveWorldDataLoader()
        for (unexploredPoint in unexploredPoints) {
            var distance = player.worldLocation.distanceTo(unexploredPoint)
            while (distance > 5) {
                val grid = liveWorldDataLoader.getGrid()
                val farthestPoint = getFarthestWalkablePoint(unexploredPoint, grid) ?: break
                val pathFinder = PathFinder(grid)
                val path = pathFinder.findPath(player.worldLocation, farthestPoint)
                val pathWalker = PathWalker(path)
                pathWalker.walkPath()
                distance = player.worldLocation.distanceTo(unexploredPoint)
            }
        }
    }

    private fun getFarthestWalkablePoint(target: WorldPoint, grid: Array<Array<Array<PathNode?>>>): WorldPoint {
        val z = target.plane
        var walkablePoints: MutableList<WorldPoint> = ArrayList()
        for (y in 0 until grid[z].size - 1) {
            for (x in 0 until grid[z][y].size - 1) {
                val node = grid[z][y][x] ?: continue
                if (node.blocked) {
                    continue
                }
                if (node.blockedMovementEast && node.blockedMovementWest && node.blockedMovementNorth && node.blockedMovementSouth) {
                    continue
                }
                walkablePoints.add(node.worldLocation)
            }
        }
        walkablePoints = walkablePoints.stream().sorted(Comparator.comparingInt { wp: WorldPoint -> wp.distanceTo(target) }).collect(Collectors.toList())
        return walkablePoints[0]
    }

    private fun parseCoordinates(input: String): List<WorldPoint> {
        val coordinates: MutableList<WorldPoint> = ArrayList()
        val lines = input.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (line in lines) {
            val parts = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size == 3) {
                val x = parts[0].toInt()
                val y = parts[1].toInt()
                val z = parts[2].toInt()
                coordinates.add(WorldPoint(x, y, z))
            }
        }
        return coordinates
    }
}
