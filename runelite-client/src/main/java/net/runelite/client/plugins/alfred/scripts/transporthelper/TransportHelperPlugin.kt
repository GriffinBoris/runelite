package net.runelite.client.plugins.alfred.scripts.transporthelper

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.inject.Provides
import net.runelite.api.Perspective
import net.runelite.api.Tile
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.enums.TransportTypes
import net.runelite.client.ui.overlay.OverlayManager
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.inject.Inject

@PluginDescriptor(name = TransportHelperPlugin.CONFIG_GROUP, enabledByDefault = false)
class TransportHelperPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Alfred Transport Helper"
    }

    @Inject
    private lateinit var config: TransportHelperConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): TransportHelperConfig {
        return configManager.getConfig(TransportHelperConfig::class.java)
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: TransportHelperOverlay
    private var mouseListener: MouseListener? = null

    override fun startUp() {
        overlayManager.add(overlay)
        setupMouseListener()
    }

    override fun shutDown() {
        overlayManager.remove(overlay)
        Alfred.client.getCanvas().removeMouseListener(mouseListener)
    }

    private fun setupMouseListener() {
        mouseListener = object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                val client = Alfred.client
                for (tile in Alfred.api.walk.getAllTiles()) {
                    val tileLocalLocation = tile.tile.getLocalLocation()
                    val poly = Perspective.getCanvasTilePoly(client, tileLocalLocation)
                    if (poly != null && poly.contains(client.getMouseCanvasPosition().x, client.getMouseCanvasPosition().y)) {
                        getTileTransportInformation(tile.tile)
                    }
                }
            }

            override fun mousePressed(e: MouseEvent) {}
            override fun mouseReleased(e: MouseEvent) {}
            override fun mouseEntered(e: MouseEvent) {}
            override fun mouseExited(e: MouseEvent) {}
        }
        Alfred.client.getCanvas().addMouseListener(mouseListener)
    }

    private fun getTileTransportInformation(tile: Tile) {
        var objectName: String? = ""
        var objectId = -1
        var objectHash = -1
        val worldPoint = tile.worldLocation
        var eastWest = false

        if (tile.wallObject != null) {
            objectName = Alfred.api.objects.getObjectIdVariableName(tile.wallObject.id)
            objectId = tile.wallObject.getId()
            objectHash = tile.wallObject.hash.toInt()
            if (tile.wallObject.orientationA == 1 || tile.wallObject.orientationA == 4) {
                eastWest = true
            }
        } else {
            for (gameObject in tile.getGameObjects()) {
                if (gameObject == null) {
                    continue
                }
                objectName = Alfred.api.objects.getObjectIdVariableName(gameObject.id)
                objectId = gameObject.getId()
                objectHash = gameObject.hash.toInt()
                println("Game Object Orientation: ${gameObject.orientation}")
                println("Game Object Model Orientation: ${gameObject.modelOrientation}")
                break
            }
        }

        val transport = JsonObject()
        transport.addProperty("transport_name", "")
        transport.addProperty("object_hash", objectHash)
        transport.addProperty("object_id", objectId)
        transport.addProperty("object_name", objectName)

        if (config.transportType() == TransportTypes.DOOR) {
            if (eastWest) {
                transport.addProperty("unblock_north_south", false)
                transport.addProperty("unblock_east_west", true)
            } else {
                transport.addProperty("unblock_north_south", true)
                transport.addProperty("unblock_east_west", false)
            }
        } else {
            transport.addProperty("unblock_north_south", false)
            transport.addProperty("unblock_east_west", false)
        }

        val startTile = JsonObject()
        startTile.addProperty("x", worldPoint.x)
        startTile.addProperty("y", worldPoint.y)
        startTile.addProperty("z", worldPoint.plane)

        val endTile = JsonObject()
        endTile.addProperty("x", 0)
        endTile.addProperty("y", 0)
        endTile.addProperty("z", 0)

        val connection = JsonObject()
        connection.add("start_tile", startTile)
        connection.add("end_tile", endTile)
        connection.addProperty("action", "")

        val connections = JsonArray()
        connections.add(connection)

        transport.add("connections", connections)

        val gson = GsonBuilder().setPrettyPrinting().create()

        val stringSelection = StringSelection(gson.toJson(transport))
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)
        println(gson.toJson(transport))

    }
}
