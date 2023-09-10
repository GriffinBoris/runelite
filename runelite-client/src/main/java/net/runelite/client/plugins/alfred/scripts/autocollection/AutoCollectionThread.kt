package net.runelite.client.plugins.alfred.scripts.autocollection

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.runelite.api.GameState
import net.runelite.api.Tile
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.objects.RSObject
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.LiveWorldDataLoader
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathFinder
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathNode
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathWalker
import java.util.stream.Collectors

class AutoCollectionThread(private var config: AutoCollectionConfig) : Thread() {
    companion object {
        var searching = ""
    }

    private val usedPoints: MutableList<WorldPoint>
    private val unexploredPoints: List<WorldPoint>
    private val exploredOperables: MutableList<WorldPoint>

    init {
        usedPoints = mutableListOf()
        unexploredPoints = parseCoordinates(config.points())
        exploredOperables = mutableListOf()
    }

    override fun run() {
        if (Alfred.client.getGameState() != GameState.LOGGED_IN) {
            Alfred.api.account.login()
        }

        val player = Alfred.api.players.localPlayer
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

                val nearestOperable = getOperables().sortedBy { rsObject: RSObject -> rsObject.worldLocation!!.distanceTo(player.worldLocation) }.firstOrNull()
                if (nearestOperable != null) {
                    if (!exploredOperables.contains(nearestOperable.worldLocation!!)) {
                        if (nearestOperable.worldLocation!!.distanceTo(player.worldLocation) >= 4) {
                            Alfred.api.walk.walkTo(nearestOperable.worldLocation)
                        }
                        Alfred.sleep(2000)
                        handleDoorOperable(nearestOperable)
                        exploredOperables.add(nearestOperable.worldLocation!!)
                    } else {
                        println("explored operable")
                    }
                } else {
                    println("no operable")
                }

                distance = player.worldLocation.distanceTo(unexploredPoint)
            }
        }

    }


//    override fun run() {
//        if (Alfred.client.getGameState() != GameState.LOGGED_IN) {
//            Alfred.api.account.login()
//        }
//        val player = Alfred.api.players.localPlayer
//        val liveWorldDataLoader = LiveWorldDataLoader()
//
//        for (unexploredPoint in unexploredPoints) {
//            var distance = player.worldLocation.distanceTo(unexploredPoint)
//            while (distance > 5) {
//                val grid = liveWorldDataLoader.getGrid()
//                val farthestPoint = getFarthestWalkablePoint(unexploredPoint, grid) ?: break
//                val pathFinder = PathFinder(grid)
//                val path = pathFinder.findPath(player.worldLocation, farthestPoint)
//                val pathWalker = PathWalker(path)
//                pathWalker.walkPath()
//                distance = player.worldLocation.distanceTo(unexploredPoint)
//            }
//        }
//    }

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

    private fun getOperables(): MutableList<RSObject> {
        val objects: MutableList<RSObject> = mutableListOf()

        Alfred.api.objects.objectsFromTiles.filterNotNull().forEach { rsObject: RSObject ->
            val name = Alfred.api.objects.getObjectIdVariableName(rsObject.id)
            if (name != null) {
                //            if (name.startsWith("stair", ignoreCase = true) || name.startsWith("door", ignoreCase = true) || name.startsWith("gate", ignoreCase = true)) {
                if (name.startsWith("door", ignoreCase = true) || name.startsWith("gate", ignoreCase = true)) {
                    objects.add(rsObject)
                }
            }
        }
        return objects
    }

    private fun handleDoorOperable(rsObject: RSObject) {
        val player = Alfred.api.players.localPlayer
        var isClosed = false
        var search = false
        var finalRsObject: RSObject? = null

        if (rsObject.actions.isEmpty()) {
            Alfred.mouse.rightClick(rsObject.clickBox)
            Alfred.sleepUntil({ Alfred.api.menu.menu != null }, 100, 3000)
            val menu = Alfred.api.menu.menu
            if (menu.hasAction("open")) {
                isClosed = true
            } else if (menu.hasAction("close")) {
                isClosed = false
            }
        } else if (rsObject.actions.contains("open")) {
            isClosed = true
        }

        if (!isClosed) {
            Alfred.mouse.rightClick(rsObject.clickBox)
            Alfred.sleepUntil({ Alfred.api.menu.menu != null }, 100, 3000)
            val menu = Alfred.api.menu.menu
            if (menu.hasAction("close")) {
                menu.clickAction("close")
                Alfred.sleep(1000)
                Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
                Alfred.sleep(1000)
                search = true
            } else {
                println("Cannot find close action")
            }
        }

        if (search) {
            val operable = getOperables().filter { foundObject: RSObject -> foundObject.worldLocation!!.distanceTo(rsObject.worldLocation) <= 2 }.firstOrNull()
            if (operable != null) {
                finalRsObject = operable
            } else {
                println("Cannot find closed neighbor")
                return
            }
        } else {
            finalRsObject = rsObject
        }

        val northWorldPoint = WorldPoint(finalRsObject.worldLocation!!.x, finalRsObject.worldLocation!!.y + 1, finalRsObject.plane)
        val southWorldPoint = WorldPoint(finalRsObject.worldLocation!!.x, finalRsObject.worldLocation!!.y - 1, finalRsObject.plane)

        val northTile = Alfred.api.world.tiles.firstOrNull { tile: Tile -> tile.worldLocation == northWorldPoint }
        val southTile = Alfred.api.world.tiles.firstOrNull { tile: Tile -> tile.worldLocation == southWorldPoint }

        var unblockEastWest = false
        if (southTile != null && northTile != null) {
            if (southTile.wallObject != null && northTile.wallObject != null) {
                unblockEastWest = true
            } else if (southTile.wallObject == null && northTile.wallObject == null) {
                unblockEastWest = false
            } else {
                println("failed to determine direction")
                return
            }
        } else {
            println("failed to get north and south tile")
            return
        }

        val objectTile = Alfred.api.world.tiles.firstOrNull { tile: Tile -> tile.worldLocation == finalRsObject.worldLocation }

        if (objectTile == null) {
            println("failed to get final object tile")
            return
        }

        var objectName = Alfred.api.objects.getObjectIdVariableName(objectTile.wallObject.id)
        val objectId = objectTile.wallObject.id

        objectName = objectName!!.split("_")[0].lowercase().capitalize()

        val transport = JsonObject()
        transport.addProperty("transport_name", "")
        transport.addProperty("object_hash", objectTile.wallObject.hash)
        transport.addProperty("object_id", objectId)
        transport.addProperty("object_name", objectName)

        if (unblockEastWest) {
            transport.addProperty("unblock_north_south", false)
            transport.addProperty("unblock_east_west", true)
        } else {
            transport.addProperty("unblock_north_south", true)
            transport.addProperty("unblock_east_west", false)
        }

        val startTile = JsonObject()
        startTile.addProperty("x", objectTile.worldLocation.x)
        startTile.addProperty("y", objectTile.worldLocation.y)
        startTile.addProperty("z", objectTile.worldLocation.plane)

        val endTile = JsonObject()
        endTile.add("x", null)
        endTile.add("y", null)
        endTile.add("z", null)

        val connection = JsonObject()
        connection.add("start_tile", startTile)
        connection.add("end_tile", endTile)
        connection.addProperty("action", "open")

        val connections = JsonArray()
        connections.add(connection)

        transport.add("connections", connections)

//        println(transport)
        val gson = GsonBuilder().setPrettyPrinting().create()
        println(gson.toJson(transport))

        Alfred.mouse.rightClick(finalRsObject.clickBox)
        Alfred.sleepUntil({ Alfred.api.menu.menu != null }, 100, 3000)
        val menu = Alfred.api.menu.menu
        if (menu.hasAction("open")) {
            menu.clickAction("open")
            Alfred.sleep(1000)
            Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
            Alfred.sleep(1000)
        }
    }
}
