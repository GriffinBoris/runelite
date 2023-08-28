package net.runelite.client.plugins.alfred.scripts.quester

import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor

@PluginDescriptor(name = QuesterPlugin.CONFIG_GROUP, enabledByDefault = false)
class QuesterPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Alfred Quester"
    }

    private var questerThread: QuesterThread = QuesterThread()

    override fun startUp() {
        questerThread = QuesterThread()
        questerThread.start()
    }

    override fun shutDown() {
        questerThread.stop()
    }
}
