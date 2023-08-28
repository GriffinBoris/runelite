package net.runelite.client.plugins.alfred.scripts.ashpicker

import com.google.inject.Provides
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = AshPickerPlugin.CONFIG_GROUP, enabledByDefault = false)
class AshPickerPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Alfred Ash Picker"
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: AshPickerOverlay

    @Inject
    private lateinit var config: AshPickerConfig

    @Provides
    fun provideConfig(configManager: ConfigManager): AshPickerConfig {
        return configManager.getConfig(AshPickerConfig::class.java)
    }

    private lateinit var ashPickerThread: AshPickerThread

    override fun startUp() {
        overlayManager.add(overlay)
        ashPickerThread = AshPickerThread(config)
        ashPickerThread.start()
    }

    override fun shutDown() {
        ashPickerThread.stop()
        overlayManager.remove(overlay)
    }


}
