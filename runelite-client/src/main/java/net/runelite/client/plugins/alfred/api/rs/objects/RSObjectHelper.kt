package net.runelite.client.plugins.alfred.api.rs.objects

import net.runelite.api.*
import net.runelite.client.plugins.alfred.Alfred.Companion.client
import net.runelite.client.plugins.alfred.Alfred.Companion.clientThread
import net.runelite.client.plugins.alfred.api.rs.item.RSGroundItem

class RSObjectHelper {
    companion object {
        private const val MAX_DISTANCE = 2400
    }

    private fun internalGetTiles(): List<Tile> {
        return clientThread.invokeOnClientThread<List<Tile>> {
            val tileList: MutableList<Tile> = mutableListOf()
            val player = client.getLocalPlayer()
            val tiles = client.getScene().getTiles()
            val z = client.getPlane()

            for (x in 0 until Constants.SCENE_SIZE) {
                for (y in 0 until Constants.SCENE_SIZE) {
                    val tile = tiles[z][x][y] ?: continue
                    if (player.getLocalLocation().distanceTo(tile.getLocalLocation()) <= MAX_DISTANCE) {
                        tileList.add(tile)
                    }
                }
            }
            return@invokeOnClientThread tileList
        }
    }

    val itemsFromTiles: List<RSGroundItem>
        get() {
            val rsGroundItemList: MutableList<RSGroundItem> = mutableListOf()
            val tileList = internalGetTiles()

            for (tile in tileList) {
                val itemLayer = tile.getItemLayer() ?: continue
                var current: Node = itemLayer.getTop()

                while (current is TileItem) {
                    rsGroundItemList.add(RSGroundItem(current, tile))
                    current = current.getNext()
                }
            }
            return rsGroundItemList
        }

    val objectsFromTiles: List<RSObject>
        get() {
            val player = client.getLocalPlayer()
            val rsObjectList: MutableList<RSObject> = mutableListOf()
            val tileList = internalGetTiles()

            for (tile in tileList) {
                if (tile.getWallObject() != null) {
                    if (player.getLocalLocation().distanceTo(tile.getWallObject().getLocalLocation()) <= MAX_DISTANCE) {
                        rsObjectList.add(RSObject(tile.getWallObject()))
                    }
                }

                if (tile.getGroundObject() != null) {
                    if (player.getLocalLocation().distanceTo(tile.getGroundObject().getLocalLocation()) <= MAX_DISTANCE) {
                        rsObjectList.add(RSObject(tile.getGroundObject()))
                    }
                }

                for (gameObject in tile.getGameObjects()) {
                    if (gameObject == null) {
                        continue
                    }
                    if (gameObject.getSceneMinLocation() != tile.getSceneLocation()) {
                        continue
                    }
                    if (player.getLocalLocation().distanceTo(gameObject.getLocalLocation()) <= MAX_DISTANCE) {
                        rsObjectList.add(RSObject(gameObject))
                    }
                }
            }
            return rsObjectList
        }

    fun getObjectsFromTiles(name: String?): List<RSObject> {
        return clientThread.invokeOnClientThread<List<RSObject>> {
            val objects: MutableList<RSObject> = ArrayList()
            val rsObjectList = objectsFromTiles
            for (rsObject in rsObjectList) {
                if (rsObject.name.equals(name, ignoreCase = true)) {
                    objects.add(rsObject)
                }
            }
            objects
        }
    }

    fun getObjectIdVariableName(objectId: Int): String? {
        val fields = ObjectID::class.java.getDeclaredFields()
        for (field in fields) {
            field.setAccessible(true)
            if (field.type == Int::class.javaPrimitiveType) {
                try {
                    val fieldValue = field.getInt(null)
                    if (fieldValue == objectId) {
                        return field.name
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

}
