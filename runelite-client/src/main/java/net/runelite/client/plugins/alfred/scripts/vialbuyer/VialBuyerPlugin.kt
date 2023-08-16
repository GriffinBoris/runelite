package net.runelite.client.plugins.alfred.scripts.vialbuyer

import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import javax.inject.Inject

@PluginDescriptor(name = VialBuyerPlugin.CONFIG_GROUP, enabledByDefault = false)
class VialBuyerPlugin : Plugin() {
    companion object {
        const val CONFIG_GROUP = "Alfred Vial Buyer"
    }

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var overlay: VialBuyerOverlay

    private var vialBuyerThread: VialBuyerThread = VialBuyerThread()

    @Throws(Exception::class)
    override fun startUp() {
        overlayManager.add(overlay)
        vialBuyerThread = VialBuyerThread()
        vialBuyerThread.start()
    }

    @Throws(Exception::class)
    override fun shutDown() {
        vialBuyerThread.stop()
        overlayManager.remove(overlay)
    }
}
