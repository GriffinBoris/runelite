package net.runelite.client.plugins.alfred

import net.runelite.api.Client
import net.runelite.client.callback.ClientThread
import net.runelite.client.config.ProfileManager
import net.runelite.client.game.WorldService
import net.runelite.client.plugins.alfred.api.rs.BaseAPI
import net.runelite.client.plugins.alfred.api.task.BaseTasks
import net.runelite.client.plugins.alfred.device.Keyboard
import net.runelite.client.plugins.alfred.device.Mouse
import net.runelite.client.plugins.alfred.event.EventHandler
import java.util.function.BooleanSupplier

class Alfred(client: Client, clientThread: ClientThread, worldService: WorldService, profileManager: ProfileManager) {

    companion object {
        var api: BaseAPI = BaseAPI()
        var tasks: BaseTasks = BaseTasks()

        lateinit var client: Client
        lateinit var clientThread: ClientThread
        lateinit var worldService: WorldService
        lateinit var profileManager: ProfileManager

        lateinit var eventHandler: EventHandler
        lateinit var mouse: Mouse
        lateinit var keyboard: Keyboard

        var taskStatus: String? = null
            set(status) {
                println(status)
                field = status
            }
        var taskSubStatus: String? = null
            set(status) {
                println(status)
                field = status
            }
        var status: String? = null
            set(status) {
                println(status)
                field = status
            }

        fun getVarbitValue(varbit: Int): Int {
            return clientThread.invokeOnClientThread { client.getVarbitValue(varbit) }
        }

        fun getVarbitPlayerValue(varbit: Int): Int {
            return clientThread.invokeOnClientThread { client.getVarpValue(varbit) }
        }

        fun sleep(time: Int) {
            if (!client.isClientThread()) {
                try {
                    Thread.sleep(time.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        fun sleep(start: Int, end: Int) {
            if (!client.isClientThread()) {
                try {
                    val time = start + (Math.random() * (end - start)).toInt()
                    Thread.sleep(time.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        fun sleepUntil(awaitedCondition: BooleanSupplier, time: Int, timeout: Int): Boolean {
            if (!client.isClientThread()) {
                val startTime = System.currentTimeMillis()
                do {
                    if (awaitedCondition.asBoolean) {
                        return true
                    }
                    sleep(time)
                } while (System.currentTimeMillis() - startTime < timeout)
            }
            return false
        }

        fun sleepUntilExecution(callback: Runnable, awaitedCondition: BooleanSupplier, time: Int, timeout: Int): Boolean {
            if (!client.isClientThread()) {
                val startTime = System.currentTimeMillis()
                do {
                    if (awaitedCondition.asBoolean) {
                        callback.run()
                        return true
                    }
                    sleep(time)
                } while (System.currentTimeMillis() - startTime < timeout)
            }
            return false
        }
    }

    init {
        Companion.client = client
        Companion.clientThread = clientThread
        Companion.worldService = worldService
        Companion.profileManager = profileManager
        Companion.eventHandler = EventHandler()
        Companion.mouse = Mouse()
        Companion.keyboard = Keyboard()
    }

    fun start() {
    }

    fun stop() {
    }

}
