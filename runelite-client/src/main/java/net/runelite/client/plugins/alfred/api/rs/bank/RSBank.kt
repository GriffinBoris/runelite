package net.runelite.client.plugins.alfred.api.rs.bank

import net.runelite.api.GameObject
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import java.awt.Shape

class RSBank(private val bankObject: GameObject) {
    val worldLocation: WorldPoint
        get() = bankObject.worldLocation
    val localLocation: LocalPoint
        get() = bankObject.localLocation
    val clickbox: Shape?
        get() = bankObject.clickbox
}
