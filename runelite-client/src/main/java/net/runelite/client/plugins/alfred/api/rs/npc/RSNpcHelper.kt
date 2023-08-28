package net.runelite.client.plugins.alfred.api.rs.npc

import net.runelite.api.NPC
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred

class RSNpcHelper {
    private fun internalGetNpcs(): List<RSNpc> {
        return Alfred.clientThread.invokeOnClientThread {
            return@invokeOnClientThread Alfred.client.npcs.filterNotNull().map { npc: NPC -> RSNpc(npc) }.toList()
        }
    }

    val npcs: List<RSNpc>
        get() = internalGetNpcs()

    fun getNpcs(name: String): List<RSNpc> {
        return internalGetNpcs().filter { rsNpc: RSNpc -> rsNpc.name.equals(name, ignoreCase = true) }.toList()
    }

    fun getNpcs(id: Int): List<RSNpc> {
        return internalGetNpcs().filter { rsNpc: RSNpc -> rsNpc.id == id }.toList()
    }

    val attackableNpcs: List<RSNpc>
        get() = internalGetNpcs().filter { rsNpc: RSNpc -> !rsNpc.isInteracting && !rsNpc.isDead }.toList()

    fun getAttackableNpcs(name: String): List<RSNpc> {
        return internalGetNpcs().filter { rsNpc: RSNpc -> !rsNpc.isInteracting && !rsNpc.isDead && rsNpc.name.equals(name, ignoreCase = true) }.toList()
    }

    fun getNearestAttackableNpc(name: String, worldPoint: WorldPoint): RSNpc? {
        return getAttackableNpcs(name).sortedBy { rsNpc: RSNpc -> rsNpc.worldLocation.distanceTo(worldPoint) }.firstOrNull()
    }
}
