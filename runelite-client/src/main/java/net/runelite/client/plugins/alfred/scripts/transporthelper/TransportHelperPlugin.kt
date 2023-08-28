package net.runelite.client.plugins.alfred.scripts.transporthelper

import net.runelite.api.Perspective
import net.runelite.api.Tile
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.ui.overlay.OverlayManager
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.inject.Inject

@PluginDescriptor(name = "Alfred Transport Helper", enabledByDefault = false)
class TransportHelperPlugin : Plugin() {
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
        val worldPoint = tile.getWorldLocation()
        if (tile.getWallObject() != null) {
            objectName = Alfred.api.objects.getObjectIdVariableName(tile.getWallObject().getId())
            objectId = tile.getWallObject().getId()
        } else {
            for (gameObject in tile.getGameObjects()) {
                if (gameObject == null) {
                    continue
                }
                objectName = Alfred.api.objects.getObjectIdVariableName(gameObject.getId())
                objectId = gameObject.getId()
                break
            }
        }
        val stringBuilder = StringBuilder()
        stringBuilder.append("(")

        // Start tile
        stringBuilder.append(String.format("%d, %d, %d", worldPoint.x, worldPoint.y, worldPoint.plane))
        stringBuilder.append(", ")

        // End tile
        stringBuilder.append("None, None, None")
        stringBuilder.append(", ")

        // Name
        stringBuilder.append("'Name'")
        stringBuilder.append(", ")

        // Object ID
        stringBuilder.append(String.format("%d", objectId))
        stringBuilder.append(", ")

        // Object Name
        stringBuilder.append(String.format("'%s'", objectName))
        stringBuilder.append(", ")

        // start direction unblocks
        stringBuilder.append("[]")
        stringBuilder.append(", ")

        // end direction unblocks
        stringBuilder.append("[]")
        stringBuilder.append("),")
        println(stringBuilder.toString())
    }
}
