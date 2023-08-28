package net.runelite.client.plugins.alfred.scripts.transporthelper;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.walk.RSTile;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class TransportHelperOverlay extends Overlay {

    @Inject
    private TransportHelperOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Client client = Alfred.Companion.getClient();
        for (RSTile tile : Alfred.Companion.getApi().getWalk().getAllTiles()) {
            final LocalPoint tileLocalLocation = tile.getTile().getLocalLocation();
            Polygon poly = Perspective.getCanvasTilePoly(client, tileLocalLocation);

            if (poly != null && poly.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY())) {
                OverlayUtil.renderPolygon(graphics, poly, Color.GREEN);
            }
        }
        return null;
    }
}
