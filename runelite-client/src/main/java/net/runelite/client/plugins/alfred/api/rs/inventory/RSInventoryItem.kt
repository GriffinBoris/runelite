package net.runelite.client.plugins.alfred.api.rs.inventory

import net.runelite.api.widgets.Widget
import net.runelite.client.plugins.alfred.Alfred
import java.awt.Rectangle

class RSInventoryItem(private val item: Widget) {
    val id: Int
        get() = item.itemId
    val quantity: Int
        get() = item.itemQuantity
    val name: String
        get() = Alfred.getClientThread().invokeOnClientThread {
            val itemComposition = Alfred.getClient().getItemDefinition(id)
            itemComposition.name
        }
    val membersName: String
        get() = Alfred.getClientThread().invokeOnClientThread {
            val itemComposition = Alfred.getClient().getItemDefinition(id)
            itemComposition.membersName
        }
    val price: Int
        get() = Alfred.getClientThread().invokeOnClientThread {
            val itemComposition = Alfred.getClient().getItemDefinition(id)
            itemComposition.price
        }
    val highAlchemyPrice: Int
        get() = Alfred.getClientThread().invokeOnClientThread {
            val itemComposition = Alfred.getClient().getItemDefinition(id)
            itemComposition.haPrice
        }
    val isTradeable: Boolean
        get() = Alfred.getClientThread().invokeOnClientThread {
            val itemComposition = Alfred.getClient().getItemDefinition(id)
            itemComposition.isTradeable
        }
    val isMembers: Boolean
        get() = Alfred.getClientThread().invokeOnClientThread {
            val itemComposition = Alfred.getClient().getItemDefinition(id)
            itemComposition.isMembers
        }
    val isStackable: Boolean
        get() = Alfred.getClientThread().invokeOnClientThread {
            val itemComposition = Alfred.getClient().getItemDefinition(id)
            itemComposition.isStackable
        }
    val inventoryActions: List<String>
        get() = Alfred.getClientThread().invokeOnClientThread {
            val itemComposition = Alfred.getClient().getItemDefinition(id)
            itemComposition.inventoryActions.filterNotNull().toList()
        }
    val bounds: Rectangle
        get() = item.bounds

    fun drop(): Boolean {
        return Alfred.api.inventory().drop(this)
    }

    fun leftClick(): Boolean {
        Alfred.getMouse().leftClick(bounds)
        return true
    }

    fun rightClick(): Boolean {
        Alfred.getMouse().rightClick(bounds)
        return true
    }

    fun interact(action: String): Boolean {
        if (!rightClick()) {
            return false
        }
        if (!Alfred.sleepUntil({ Alfred.api.menu().menu.hasAction(action) }, 200, 2000)) {
            return false
        }
        val rsMenu = Alfred.api.menu().menu
        return rsMenu.clickAction(action)
    }
}
