package net.runelite.client.plugins.alfred.scripts.gerber

import com.google.inject.Provides
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = GerberPlugin.CONFIG_GROUP, enabledByDefault = false)
class GerberPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Alfred Gerber"
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: GerberOverlay

    @Inject
    private lateinit var config: GerberConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): GerberConfig {
        return configManager.getConfig(GerberConfig::class.java)
    }

    private var gerberThread: GerberThread? = null
    override fun startUp() {
        overlayManager.add(overlay)
        gerberThread = GerberThread(config)
        gerberThread!!.start()
    }

    override fun shutDown() {
        gerberThread!!.stop()
        overlayManager.remove(overlay)
    }
}
