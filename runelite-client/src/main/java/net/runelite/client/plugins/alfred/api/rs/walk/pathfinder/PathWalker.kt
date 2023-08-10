package net.runelite.client.plugins.alfred.api.rs.walk.pathfinder

import net.runelite.api.GameObject
import net.runelite.api.Point
import net.runelite.api.Tile
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.math.Calculations
import net.runelite.client.plugins.alfred.api.rs.player.RSPlayer

class PathWalker(private val nodes: List<PathNode>) {
    private var player: RSPlayer = Alfred.api.players().localPlayer

    fun walkPath() {
        val upperBound = nodes.size - 1
        var previousNode: PathNode? = null
        var nextNode: PathNode? = null

        for (currentNode in nodes) {
            val index = nodes.indexOf(currentNode)
            val isLastNode = nodes.indexOf(currentNode) == nodes.lastIndex

            if (index + 1 <= upperBound) {
                nextNode = nodes.get(index + 1)
            }

            if (isLastNode) {
                val minimapPoint = getMinimapPoint(currentNode.worldLocation) ?: continue
                clickPoint(minimapPoint);
                previousNode = currentNode;
                continue;
            }

            if (currentNode.pathTransports.isNotEmpty()) {
                val tile = findTile(currentNode) ?: continue
                nextNode ?: continue
                operateTransport(currentNode, nextNode, tile)
                previousNode = currentNode;
                continue;
            }

            if (previousNode == null) {
                val minimapPoint = getMinimapPoint(currentNode.worldLocation) ?: continue
                clickPoint(minimapPoint);
                previousNode = currentNode;
                continue;
            }

            val distance = Calculations.distanceBetweenPoints(previousNode.worldLocation, currentNode.worldLocation).toInt()
            if (distance >= 5) {
                val minimapPoint = getMinimapPoint(currentNode.worldLocation) ?: continue
                clickPointWhileRunning(minimapPoint, currentNode);
                previousNode = currentNode;
            }
        }
    }

    private fun getMinimapPoint(worldPoint: WorldPoint): Point? {
        return Alfred.api.miniMap().getWorldPointToScreenPoint(worldPoint)
    }

    private fun clickPoint(minimapPoint: Point) {
        Alfred.getMouse().leftClick(minimapPoint)
        Alfred.sleep(1000)
        Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
    }

    private fun clickPointWhileRunning(minimapPoint: Point, node: PathNode) {
        Alfred.getMouse().leftClick(minimapPoint)
        Alfred.sleep(1000)
        Alfred.sleepUntil({
            val distanceToTarget = Calculations.distanceBetweenPoints(player.worldLocation, node.worldLocation).toInt()
            val isStill = !player.isMoving && !player.isInteracting && player.isIdle
            val nearTile = distanceToTarget <= 2
            isStill || nearTile
        }, 200, 1000 * 30)
    }

    private fun operateTransport(pathNode: PathNode, nextNode: PathNode, tile: Tile): Boolean {
        Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
        Alfred.sleep(100, 250)

        val success = with(pathNode.operableName) {
            when {
                this?.startsWith("DOOR") == true -> operateDoorTransport(pathNode, tile)
                this?.startsWith("GATE") == true -> operateDoorTransport(pathNode, tile)
                this?.startsWith("STAIRS") == true -> operateStairTransport(pathNode, nextNode, tile)
                this?.startsWith("STAIRCASE") == true -> operateStairTransport(pathNode, nextNode, tile)
                else -> false
            }
        }

        if (success == false) {
            return false
        }

        Alfred.sleep(1000)
        Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
        Alfred.sleep(250, 500)
        return true
    }

    private fun operateDoorTransport(pathNode: PathNode, tile: Tile): Boolean {
        if (tile.wallObject == null) {
            return false
        }

        Alfred.getMouse().rightClick(tile.wallObject.convexHull.bounds)
        Alfred.sleep(200)

        val rsMenu = Alfred.api.menu().menu
        if (rsMenu == null) {
            println("Menu is null")
            return false
        }

        if (!rsMenu.hasAction("open")) {
            println("Menu does not contain open action");
            return true;
        }

        if (!rsMenu.clickAction("open")) {
            println("Failed to operate action");
            return true;
        }

        return true
    }

    private fun operateStairTransport(pathNode: PathNode, nextNode: PathNode, tile: Tile): Boolean {
        var foundGameObject: GameObject? = null

        for (gameObject in tile.gameObjects) {
            if (gameObject == null) {
                continue
            }

            val tileName = Alfred.api.objects().getObjectIdVariableName(gameObject.getId())
            if (tileName.contains("STAIR")) {
                foundGameObject = gameObject
                break
            }
        }

        if (foundGameObject == null) {
            return false
        }

        Alfred.getMouse().rightClick(foundGameObject.clickbox!!.bounds)
        Alfred.sleep(200)

        if (pathNode.pathTransports.isEmpty()) {
            println("No path transport found")
            return false
        }

        val foundEndPathNode = pathNode.pathTransports
            .map { pathTransport -> pathTransport.endPathNode }
            .filterNotNull()
            .filter { p -> p.worldLocation.equals(nextNode.worldLocation) }
            .firstOrNull()

        if (foundEndPathNode == null) {
            println("No path transport found")
            return false
        }

        val playerZ = player.worldLocation.plane
        val tileZ = foundEndPathNode.worldLocation.plane

        val goingUp = tileZ > playerZ
        val action = when (goingUp) {
            true -> "climb-up"
            false -> "climb-down"
        }

        if (!Alfred.sleepUntil({ Alfred.api.menu().menu.hasAction(action) }, 200, 2000)) {
            println("Menu does not contain action");
            return false
        }

        val rsMenu = Alfred.api.menu().menu
        if (!rsMenu.clickAction(action)) {
            println("Failed to operate on tile");
            return false;
        }

        return Alfred.sleepUntil({ player.worldLocation.plane == tileZ }, 100, 5000);

    }

    private fun findTile(pathNode: PathNode): Tile? {
        val tiles = Alfred.api.walk().getAllTiles()
        for (tile in tiles) {
            if (tile.worldLocation == pathNode.worldLocation) {
                return tile.tile
            }
        }
        return null
    }
}