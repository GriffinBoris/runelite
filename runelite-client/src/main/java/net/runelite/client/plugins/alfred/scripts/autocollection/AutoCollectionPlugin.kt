package net.runelite.client.plugins.alfred.scripts.autocollection

import com.google.inject.Provides
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.alfred.scripts.autocollection.AutoCollectionConfig
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = AutoCollectionPlugin.CONFIG_GROUP, enabledByDefault = false)
class AutoCollectionPlugin : Plugin() {
    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: AutoCollectionOverlay

    @Inject
    private lateinit var config: AutoCollectionConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): AutoCollectionConfig {
        return configManager.getConfig(AutoCollectionConfig::class.java)
    }

    private var autoCollectionThread: AutoCollectionThread? = null

    @Throws(Exception::class)
    override fun startUp() {
        overlayManager.add(overlay)
        autoCollectionThread = AutoCollectionThread(config)
        autoCollectionThread!!.start()
    }

    @Throws(Exception::class)
    override fun shutDown() {
        autoCollectionThread!!.stop()
        overlayManager.remove(overlay)
    }

    companion object {
        const val CONFIG_GROUP = "Alfred Auto World Collection"
    }
}
