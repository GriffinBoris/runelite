package net.runelite.client.plugins.alfred.api.rs.objects

import net.runelite.api.DecorativeObject
import net.runelite.api.GameObject
import net.runelite.api.GroundObject
import net.runelite.api.WallObject
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import java.awt.Rectangle
import java.awt.Shape

class RSObject {
    val rsObject: Any

    constructor(gameObject: GameObject) {
        rsObject = gameObject
    }

    constructor(groundObject: GroundObject) {
        rsObject = groundObject
    }

    constructor(wallObject: WallObject) {
        rsObject = wallObject
    }

    constructor(decorativeObject: DecorativeObject) {
        rsObject = decorativeObject
    }

    val runeliteVariableName: String?
        get() = Alfred.api.objects.getObjectIdVariableName(id)

    val type: String?
        get() = when (rsObject) {
            is GameObject -> GameObject::class.java.getSimpleName()
            is GroundObject -> GroundObject::class.java.getSimpleName()
            is WallObject -> WallObject::class.java.getSimpleName()
            is DecorativeObject -> DecorativeObject::class.java.getSimpleName()
            else -> null
        }
    val id: Int
        get() = when (rsObject) {
            is GameObject -> rsObject.getId()
            is GroundObject -> rsObject.getId()
            is WallObject -> rsObject.getId()
            is DecorativeObject -> rsObject.getId()
            else -> -1
        }
    val name: String
        get() {
            val objectComposition = Alfred.clientThread.invokeOnClientThread { Alfred.client.getObjectDefinition(id) }
            return objectComposition.getName()
        }
    val worldLocation: WorldPoint?
        get() = when (rsObject) {
            is GameObject -> rsObject.getWorldLocation()
            is GroundObject -> rsObject.getWorldLocation()
            is WallObject -> rsObject.getWorldLocation()
            is DecorativeObject -> rsObject.getWorldLocation()
            else -> null
        }
    val localLocation: LocalPoint?
        get() = when (rsObject) {
            is GameObject -> rsObject.getLocalLocation()
            is GroundObject -> rsObject.getLocalLocation()
            is WallObject -> rsObject.getLocalLocation()
            is DecorativeObject -> rsObject.getLocalLocation()
            else -> null
        }
    val plane: Int
        get() = when (rsObject) {
            is GameObject -> rsObject.getPlane()
            is GroundObject -> rsObject.getPlane()
            is WallObject -> rsObject.getPlane()
            is DecorativeObject -> rsObject.getPlane()
            else -> -1
        }
    val clickBox: Rectangle?
        get() = when (rsObject) {
            is GameObject -> rsObject.getClickbox()!!.bounds
            is GroundObject -> rsObject.getClickbox()!!.bounds
            is WallObject -> rsObject.getClickbox()!!.bounds
            is DecorativeObject -> rsObject.getClickbox()!!.bounds
            else -> null
        }
    val convexHull: Shape?
        get() = when (rsObject) {
            is GameObject -> rsObject.getConvexHull()
            is GroundObject -> rsObject.getConvexHull()
            is WallObject -> rsObject.getConvexHull()
            is DecorativeObject -> rsObject.getConvexHull()
            else -> null
        }

    val actions: List<String>
        get() {
            return Alfred.clientThread.invokeOnClientThread {
                val composition = Alfred.client.getObjectDefinition(id)
                return@invokeOnClientThread composition.actions.filterNotNull()
            }
        }

    fun leftClick(): Boolean {
        val localPoint = localLocation
        val plane = plane
        if (Alfred.api.screen.isPointOnScreen(localPoint, plane)) {
            val screenPoint = Alfred.api.screen.getLocalPointToScreenPoint(localPoint, plane)
            Alfred.mouse.leftClick(screenPoint)
            return true
        }
        return false
    }

    fun rightClick(): Boolean {
        val localPoint = localLocation
        val plane = plane
        if (Alfred.api.screen.isPointOnScreen(localPoint, plane)) {
            val screenPoint = Alfred.api.screen.getLocalPointToScreenPoint(localPoint, plane)
            Alfred.mouse.rightClick(screenPoint)
            return true
        }
        return false
    }

    fun clickAction(action: String): Boolean {
        Alfred.status = "Clicking $action on $name"
        if (!rightClick()) {
            return false
        }

        Alfred.sleep(200, 400)
        val rsMenu = Alfred.api.menu.menu ?: return false
        return rsMenu.clickAction(action, name)
    }
}
