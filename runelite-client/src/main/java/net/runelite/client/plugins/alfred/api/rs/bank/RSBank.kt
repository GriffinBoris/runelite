package net.runelite.client.plugins.alfred.api.rs.bank

import net.runelite.api.GameObject
import net.runelite.api.coords.WorldPoint
import java.awt.Shape

class RSBank(private val bankObject: GameObject) {
    val worldLocation: WorldPoint
        get() = bankObject.worldLocation
    val clickbox: Shape?
        get() = bankObject.clickbox
}
