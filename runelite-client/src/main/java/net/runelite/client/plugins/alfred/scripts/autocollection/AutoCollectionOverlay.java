package net.runelite.client.plugins.alfred.scripts.autocollection;

import net.runelite.api.GameState;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.AlfredOverlayBuilder;
import net.runelite.client.plugins.alfred.api.rs.walk.RSTile;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathFinder;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathNode;
import net.runelite.client.plugins.alfred.scripts.collection.WorldDataCollectionThread;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class AutoCollectionOverlay extends Overlay {

    @Inject
    private AutoCollectionOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGHEST);
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        for (RSTile tile : Alfred.Companion.getApi().getWalk().getAllTiles()) {
            for (PathNode node : PathFinder.Companion.getPath()) {
                if (tile.getWorldLocation().equals(node.getWorldLocation())) {
                    Polygon poly = tile.getCanvasPolygon();

                    if (poly == null) {
                        continue;
                    }

                    graphics.setColor(Color.BLUE);
                    if (!node.getPathTransports().isEmpty()) {
                        graphics.setColor(Color.RED);
                    }

                    graphics.drawPolygon(poly);
                }
            }
        }

        if (Alfred.Companion.getClient().getGameState() != GameState.LOGGED_IN) {
            return null;
        }

        Widget widget = Alfred.Companion.getApi().getWidgets().getWidget(WidgetInfo.CHATBOX_MESSAGE_LINES);
        if (widget == null || widget.isHidden() || widget.isSelfHidden()) {
            return null;
        }

        AlfredOverlayBuilder overlayBuilder = new AlfredOverlayBuilder(graphics, widget.getBounds());
        overlayBuilder.drawBounds();
        overlayBuilder.drawTitle("Alfred Auto World Collection");
        overlayBuilder.drawText("", false);
        overlayBuilder.drawText("Searching:", AutoCollectionThread.Companion.getSearching(), true);
//        overlayBuilder.drawText("Walking To:", AutoCollectionThread.searching, false);
        return null;
    }
}
