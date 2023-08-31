package net.runelite.client.plugins.alfred.api.rs.npc

import net.runelite.api.NPC
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import java.awt.Rectangle
import java.awt.Shape

class RSNpc(private val npc: NPC) {
    val id: Int
        get() = npc.id
    val name: String?
        get() = npc.name
    val localLocation: LocalPoint
        get() = npc.localLocation
    val worldLocation: WorldPoint
        get() = npc.worldLocation
    val worldArea: WorldArea
        get() = npc.worldArea
    val convexHull: Shape
        get() = npc.convexHull
    val clickBox: Rectangle
        get() = npc.convexHull.bounds
    val actions: List<String>
        get() {
            return npc.composition.actions.filterNotNull().map { action: String -> action.lowercase() }.toList()
        }

    fun hasAction(action: String): Boolean {
        return actions.contains(action)
    }

    val combatLevel: Int
        get() = npc.combatLevel
    val isAnimating: Boolean
        get() = npc.animation != -1
    val isVisible: Boolean
        get() = npc.composition.isVisible
    val isClickable: Boolean
        get() = npc.composition.isClickable
    val isDead: Boolean
        get() = npc.isDead
    val isInteracting: Boolean
        get() = npc.isInteracting

    fun attack(): Boolean {
        if (npc.isInteracting || npc.isDead) {
            return false
        }

        val clickBox = clickBox ?: return false
        Alfred.mouse.leftClick(clickBox)

        return Alfred.sleepUntil({
            val interactingActor = npc.interacting ?: return@sleepUntil false
            interactingActor.name ?: return@sleepUntil false
            return@sleepUntil interactingActor.name == Alfred.client.localPlayer.name
        }, 100, 3000)
    }

    fun interact(action: String): Boolean {
        Alfred.status = "Interacting with $name"
        if (!actions.contains(action)) {
            return false
        }

        val clickBox = clickBox ?: return false
        Alfred.mouse.rightClick(clickBox)

        Alfred.sleep(200, 600)
        val menu = Alfred.api.menu.menu
        return menu.clickAction(action)
    }
}
