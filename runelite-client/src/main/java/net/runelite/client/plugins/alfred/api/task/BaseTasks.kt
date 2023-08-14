package net.runelite.client.plugins.alfred.api.task

class BaseTasks {
    val banking: Banking = Banking()
    private val npcTasks: NPCTasks = NPCTasks()
    private val itemTasks: ItemTasks = ItemTasks()
    private val objectTasks: ObjectTasks = ObjectTasks()
    val woodcutting: Woodcutting = Woodcutting()
    val mining: Mining = Mining()

    fun npcs(): NPCTasks {
        return npcTasks
    }

    fun items(): ItemTasks {
        return itemTasks
    }

    fun objects(): ObjectTasks {
        return objectTasks
    }
}
