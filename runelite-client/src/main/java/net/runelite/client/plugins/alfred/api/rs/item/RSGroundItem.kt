package net.runelite.client.plugins.alfred.api.rs.item

import net.runelite.api.Tile
import net.runelite.api.TileItem
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred

class RSGroundItem(private val tileItem: TileItem, private val tile: Tile) {
    val id: Int
        get() = tileItem.id
    val quantity: Int
        get() = tileItem.quantity
    val worldLocation: WorldPoint
        get() = tile.worldLocation
    val localLocation: LocalPoint
        get() = tile.localLocation
    val name: String
        get() = Alfred.clientThread.invokeOnClientThread {
            val itemComposition = Alfred.client.getItemDefinition(id)
            itemComposition.name
        }
    val membersName: String
        get() = Alfred.clientThread.invokeOnClientThread {
            val itemComposition = Alfred.client.getItemDefinition(id)
            itemComposition.membersName
        }
    val price: Int
        get() = Alfred.clientThread.invokeOnClientThread {
            val itemComposition = Alfred.client.getItemDefinition(id)
            itemComposition.price
        }
    val highAlchemyPrice: Int
        get() = Alfred.clientThread.invokeOnClientThread {
            val itemComposition = Alfred.client.getItemDefinition(id)
            itemComposition.haPrice
        }
    val isTradeable: Boolean
        get() = Alfred.clientThread.invokeOnClientThread {
            val itemComposition = Alfred.client.getItemDefinition(id)
            itemComposition.isTradeable
        }
    val isMembers: Boolean
        get() = Alfred.clientThread.invokeOnClientThread {
            val itemComposition = Alfred.client.getItemDefinition(id)
            itemComposition.isMembers
        }
    val isStackable: Boolean
        get() = Alfred.clientThread.invokeOnClientThread {
            val itemComposition = Alfred.client.getItemDefinition(id)
            itemComposition.isStackable
        }
    val inventoryActions: List<String>
        get() = Alfred.clientThread.invokeOnClientThread<List<String>> {
            val itemComposition = Alfred.client.getItemDefinition(id)
            itemComposition.inventoryActions.filterNotNull().toMutableList()
        }

    fun leftClick(): Boolean {
        val localPoint = tile.getLocalLocation()
        val plane = tile.getPlane()
        if (Alfred.api.screen.isPointOnScreen(localPoint, plane)) {
            val screenPoint = Alfred.api.screen.getLocalPointToScreenPoint(localPoint, plane)
            Alfred.mouse.leftClick(screenPoint)
            return true
        }
        return false
    }

    fun rightClick(): Boolean {
        val localPoint = tile.getLocalLocation()
        val plane = tile.getPlane()
        if (Alfred.api.screen.isPointOnScreen(localPoint, plane)) {
            val screenPoint = Alfred.api.screen.getLocalPointToScreenPoint(localPoint, plane)
            Alfred.mouse.rightClick(screenPoint)
            return true
        }
        return false
    }

    fun clickAction(action: String): Boolean {
        Alfred.status = "Clicking " + action + " on " + name
        if (!rightClick()) {
            return false
        }
        Alfred.sleep(200, 400)
        val rsMenu = Alfred.api.menu.menu ?: return false
        return rsMenu.clickAction(action, name)
    }
}
