package net.runelite.client.plugins.alfred.scripts.collection;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(name = "Alfred World Collections", enabledByDefault = false)
@Slf4j
public class WorldCollectionPlugin extends Plugin {
    static final String CONFIG_GROUP = "Alfred World Collection";
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WorldCollectionOverlay overlay;
    private WorldDataCollectionThread worldDataCollectionThread;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        worldDataCollectionThread = new WorldDataCollectionThread();
        worldDataCollectionThread.start();
    }

    @Override
    protected void shutDown() throws Exception {
        worldDataCollectionThread.executor.shutdown();
        worldDataCollectionThread.executor.shutdownNow();
        while (!worldDataCollectionThread.executor.isTerminated()) {
        }
        worldDataCollectionThread.stop();
        overlayManager.remove(overlay);
    }
}
