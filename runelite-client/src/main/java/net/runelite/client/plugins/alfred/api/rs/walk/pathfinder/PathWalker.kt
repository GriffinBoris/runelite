package net.runelite.client.plugins.alfred.api.rs.walk.pathfinder

import net.runelite.api.Point
import net.runelite.api.Tile
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.math.Calculations
import net.runelite.client.plugins.alfred.api.rs.objects.RSObject
import net.runelite.client.plugins.alfred.api.rs.player.RSPlayer
import net.runelite.client.plugins.alfred.util.Utility

class PathWalker(private val nodes: List<PathNode>) {
    private var player: RSPlayer = Alfred.api.players.localPlayer

    fun walkPath() {
        val skipDistance = 4
        val upperBound = nodes.size - 1
        var previousNode: PathNode? = null
        var nextNode: PathNode? = null

        val player = Alfred.api.players.localPlayer

        for (currentNode in nodes) {
            val index = nodes.indexOf(currentNode)
            val isLastNode = nodes.indexOf(currentNode) == nodes.lastIndex


            Utility.retryFunction(3, true) {
                if (player.runEnergy >= 30 && !player.isRunningActive) {
                    player.toggleRunning(true)
                }
                return@retryFunction true
            }

            if (index + 1 <= upperBound) {
                nextNode = nodes.get(index + 1)
            }

            if (isLastNode) {
                val minimapPoint = getMinimapPoint(currentNode.worldLocation) ?: continue
                clickPoint(minimapPoint)
                previousNode = currentNode
                continue
            }

            if (currentNode.pathTransports.isNotEmpty()) {
                if (previousNode != null) {
                    val minimapPoint = getMinimapPoint(previousNode.worldLocation)
                    if (minimapPoint != null) {
                        clickPoint(minimapPoint)
                    }
                }

                nextNode ?: continue
                handleTransport(currentNode, nextNode)
                previousNode = currentNode
                continue
            }

            if (previousNode == null && nodes.count() < skipDistance) {
                val minimapPoint = getMinimapPoint(currentNode.worldLocation) ?: continue
                clickPoint(minimapPoint)
                previousNode = currentNode
                continue

            } else if (previousNode == null) {
                previousNode = currentNode
                continue
            }

            val distance = Calculations.distanceBetweenPoints(previousNode.worldLocation, currentNode.worldLocation).toInt()
            if (distance >= skipDistance) {
                val minimapPoint = getMinimapPoint(currentNode.worldLocation) ?: continue
                clickPointWhileRunning(minimapPoint, currentNode)
                previousNode = currentNode
            }
        }
    }

    private fun getMinimapPoint(worldPoint: WorldPoint): Point? {
        return Alfred.api.miniMap.getWorldPointToScreenPoint(worldPoint)
    }

    private fun clickPoint(minimapPoint: Point) {
        Alfred.mouse.leftClick(minimapPoint)
        Alfred.sleep(1000)
        Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
    }

    private fun clickPointWhileRunning(minimapPoint: Point, node: PathNode) {
        Alfred.mouse.leftClick(minimapPoint)
        Alfred.sleep(1000)
        Alfred.sleepUntil({
            val distanceToTarget = Calculations.distanceBetweenPoints(player.worldLocation, node.worldLocation).toInt()
            val isStill = !player.isMoving && !player.isInteracting && player.isIdle
            val nearTile = distanceToTarget <= 2
            isStill || nearTile
        }, 200, 1000 * 30)
    }

    private fun handleTransport(pathNode: PathNode, nextNode: PathNode): Boolean {
        Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
        Alfred.sleep(100, 250)

        val transport: PathTransport? = findTransport(pathNode, nextNode)
        if (transport == null) {
            println("cant find transport")
            return false
        }

        val success = operateTransport(transport)
        if (!success) {
            return false
        }

        Alfred.sleep(1000)
        Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 10)
        Alfred.sleep(250, 500)
        return true
    }

    private fun findTransport(pathNode: PathNode, nextNode: PathNode): PathTransport? {
        return if (pathNode.pathTransports.count() == 1) {
            pathNode.pathTransports.first()
        } else {
            pathNode.pathTransports.firstOrNull { pathTransport: PathTransport -> pathTransport.endPathNode?.worldLocation == nextNode.worldLocation }
        }
    }

    private fun operateTransport(pathTransport: PathTransport): Boolean {
//        val transportObject = Alfred.api.objects.objectsFromTiles.firstOrNull { rsObject: RSObject -> rsObject.id == pathTransport.objectId && rsObject.worldLocation == pathTransport.startPathNode.worldLocation }
//        val transportObject = Alfred.api.objects.objectsFromTiles.firstOrNull { rsObject: RSObject -> rsObject.id == pathTransport.objectId }
        val transportObject = Alfred.api.objects.objectsFromTiles
            .filter { rsObject: RSObject -> rsObject.worldLocation != null }
            .filter { rsObject: RSObject -> rsObject.id == pathTransport.objectId }
            .minBy { rsObject: RSObject -> rsObject.worldLocation!!.distanceTo(player.worldLocation) }

        if (transportObject == null) {
            println("No transport: ${pathTransport.name}, found with ID: ${pathTransport.objectId}, at location: ${pathTransport.startPathNode.worldLocation}")
            return false
        }

        println("Operating on Transport: ${pathTransport.name}, with action: ${pathTransport.action}")

        when (pathTransport.objectName.lowercase()) {
            "door" -> Alfred.mouse.rightClick(transportObject.convexHull?.bounds)
            "gate" -> Alfred.mouse.rightClick(transportObject.convexHull?.bounds)
            else -> Alfred.mouse.rightClick(transportObject.clickBox)
        }

        Alfred.sleep(200)

        if (!Alfred.sleepUntil({ Alfred.api.menu.menu.hasAction(pathTransport.action) }, 200, 2000)) {
            println("No menu action: ${pathTransport.action} found")
            return false
        }

        val rsMenu = Alfred.api.menu.menu
        if (!rsMenu.clickAction(pathTransport.action)) {
            println("Failed to click transport")
            return false
        }

        if (pathTransport.endPathNode != null) {
            return Alfred.sleepUntil({ player.worldLocation == pathTransport.endPathNode.worldLocation }, 100, 5000)
        }

        return true
    }


    private fun findTile(pathNode: PathNode): Tile? {
        val tiles = Alfred.api.walk.getAllTiles()
        for (tile in tiles) {
            if (tile.worldLocation == pathNode.worldLocation) {
                return tile.tile
            }
        }
        return null
    }
}