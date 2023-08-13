package net.runelite.client.plugins.alfred.scripts.autocollection;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(name = AutoCollectionPlugin.CONFIG_GROUP, enabledByDefault = false)
@Slf4j
public class AutoCollectionPlugin extends Plugin {
    static final String CONFIG_GROUP = "Alfred Auto World Collection";
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoCollectionOverlay overlay;
    private AutoCollectionThread autoCollectionThread;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        autoCollectionThread = new AutoCollectionThread();
        autoCollectionThread.start();
    }

    @Override
    protected void shutDown() throws Exception {
        autoCollectionThread.stop();
        overlayManager.remove(overlay);
    }
}
