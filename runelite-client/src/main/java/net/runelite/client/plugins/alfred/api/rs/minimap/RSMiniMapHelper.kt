package net.runelite.client.plugins.alfred.api.rs.minimap

import net.runelite.api.Perspective
import net.runelite.api.Point
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred

class RSMiniMapHelper {
    fun getLocalPointToScreenPoint(localPoint: LocalPoint?, plane: Int): Point? {
        return Perspective.localToMinimap(Alfred.client, localPoint!!, plane)
    }

    fun getWorldPointToScreenPoint(worldPoint: WorldPoint?): Point? {
        val localPoint = LocalPoint.fromWorld(Alfred.client, worldPoint) ?: return null
        return Perspective.localToMinimap(Alfred.client, localPoint)
    }
}
