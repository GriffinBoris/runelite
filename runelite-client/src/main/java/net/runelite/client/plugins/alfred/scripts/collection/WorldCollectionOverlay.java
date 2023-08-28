package net.runelite.client.plugins.alfred.scripts.collection;

import net.runelite.api.GameState;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.AlfredOverlayBuilder;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class WorldCollectionOverlay extends Overlay {

    @Inject
    private WorldCollectionOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        drawStatusBox(graphics);
        return null;
    }

    private void drawStatusBox(Graphics2D graphics) {
        if (Alfred.Companion.getClient().getGameState() != GameState.LOGGED_IN) {
            return;
        }

        Widget widget = Alfred.Companion.getApi().getWidgets().getWidget(WidgetInfo.CHATBOX_MESSAGE_LINES);
        if (widget == null || widget.isHidden() || widget.isSelfHidden()) {
            return;
        }

        AlfredOverlayBuilder overlayBuilder = new AlfredOverlayBuilder(graphics, widget.getBounds());
        overlayBuilder.drawBounds();
        overlayBuilder.drawTitle("Alfred World Collection");
        overlayBuilder.drawText("", false);
        overlayBuilder.drawText("Started:", Integer.toString(WorldDataCollectionThread.started), true);
        overlayBuilder.drawText("Completed:", Integer.toString(WorldDataCollectionThread.completed), false);
    }
}
