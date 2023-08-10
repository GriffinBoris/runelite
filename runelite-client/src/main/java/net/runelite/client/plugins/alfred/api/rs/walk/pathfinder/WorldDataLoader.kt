package net.runelite.client.plugins.alfred.api.rs.walk.pathfinder

import net.runelite.api.coords.WorldPoint
import java.sql.DriverManager

class WorldDataLoader(private val path: String) {
    private val nodes: MutableList<PathNode> = mutableListOf()

    init {
        readTiles()
    }

    fun getGrid(): Array<Array<Array<PathNode?>>> {
        val maxX = nodes.maxBy { pathNode -> pathNode.worldLocation.x }.worldLocation.x
        val maxY = nodes.maxBy { pathNode -> pathNode.worldLocation.y }.worldLocation.y
        val maxZ = nodes.maxBy { pathNode -> pathNode.worldLocation.plane }.worldLocation.plane

        val grid = Array(maxZ + 1) { Array(maxY + 1) { arrayOfNulls<PathNode>(maxX + 1) } }

        for (node in nodes) {
            grid[node.worldLocation.plane][node.worldLocation.y][node.worldLocation.x] = node
        }

        val transports = readTransports()
        for (transport in transports) {
            val transportLocation = transport.startPathNode.worldLocation
            val node = grid[transportLocation.plane][transportLocation.y][transportLocation.x]

            node?.pathTransports?.add(transport)
        }

        return grid
    }

    private fun readTiles(): MutableList<PathNode> {
        val url = "jdbc:sqlite:$path"

        try {
//            Class.forName("org.sqlite.JDBC");
            val connection = DriverManager.getConnection(url)
            val statement = connection.createStatement()
            val rs = statement.executeQuery("select * from tiles_tile;")

            while (rs.next()) {
                val id = rs.getInt("id")
                val x = rs.getInt("x")
                val y = rs.getInt("y")
                val z = rs.getInt("z")
                val operableName = rs.getString("operable_object_name")
                val isOperable = rs.getBoolean("is_operable") && rs.getBoolean("operable_verified") && operableName != null

                val node = PathNode(
                    id = id,
                    gCost = 0,
                    hCost = 0,
                    parent = null,
                    penalty = rs.getInt("penalty"),
                    pathTransports = mutableListOf(),
                    worldLocation = WorldPoint(x, y, z),
                    operableName = operableName,
                    isOperable = isOperable,
                    blocked = rs.getBoolean("blocked"),
                    blockedMovementNorth = rs.getBoolean("blocked_movement_north"),
                    blockedMovementSouth = rs.getBoolean("blocked_movement_south"),
                    blockedMovementEast = rs.getBoolean("blocked_movement_east"),
                    blockedMovementWest = rs.getBoolean("blocked_movement_west"),
                    blockedMovementObject = rs.getBoolean("blocked_movement_object"),
                    blockedMovementFloorDecoration = rs.getBoolean("blocked_movement_floor_decoration"),
                    blockedMovementFloor = rs.getBoolean("blocked_movement_floor"),
                    blockedMovementFull = rs.getBoolean("blocked_movement_full")
                )
                nodes.add(node)

            }
            connection.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return nodes
    }

    private fun readTransports(): MutableList<PathTransport> {
        val transports = mutableListOf<PathTransport>()
        val url = "jdbc:sqlite:$path"

        try {
//            Class.forName("org.sqlite.JDBC");
            val connection = DriverManager.getConnection(url)
            val statement = connection.createStatement()
            val rs = statement.executeQuery("select * from tiles_transport;")

            while (rs.next()) {
                val startTileId = rs.getInt("start_tile_id")
                val endTileId = rs.getInt("end_tile_id")

                val startTile = nodes.filter { pathNode -> pathNode.id == startTileId }.first()
                val endTile = nodes.filter { pathNode -> pathNode.id == endTileId }.firstOrNull()

                val transport = PathTransport(
                    startPathNode = startTile,
                    endPathNode = endTile,
                    name = rs.getString("transport_name"),
                    objectName = rs.getString("object_name"),
                    objectId = rs.getInt("object_id")
                )
                transports.add(transport)

            }
            connection.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return transports
    }
}
