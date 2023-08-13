package net.runelite.client.plugins.alfred.scripts.transporthelper;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.walk.RSTile;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@PluginDescriptor(name = "Alfred Transport Helper", enabledByDefault = false)
@Slf4j
public class TransportHelperPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TransportHelperOverlay overlay;
    private MouseListener mouseListener;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        setupMouseListener();
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        Alfred.getClient().getCanvas().removeMouseListener(mouseListener);
    }

    private void setupMouseListener() {
        mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Client client = Alfred.getClient();
                for (RSTile tile : Alfred.api.walk().getAllTiles()) {
                    final LocalPoint tileLocalLocation = tile.getTile().getLocalLocation();
                    Polygon poly = Perspective.getCanvasTilePoly(client, tileLocalLocation);
                    if (poly != null && poly.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY())) {
                        getTileTransportInformation(tile.getTile());
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };

        Alfred.getClient().getCanvas().addMouseListener(mouseListener);
    }

    private void getTileTransportInformation(Tile tile) {
        String objectName = "";
        int objectId = -1;

        WorldPoint worldPoint = tile.getWorldLocation();


        if (tile.getWallObject() != null) {
            objectName = Alfred.api.objects().getObjectIdVariableName(tile.getWallObject().getId());
            objectId = tile.getWallObject().getId();
        } else {
            for (GameObject gameObject : tile.getGameObjects()) {
                if (gameObject == null) {
                    continue;
                }

                objectName = Alfred.api.objects().getObjectIdVariableName(gameObject.getId());
                objectId = gameObject.getId();
                break;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");

        // Start tile
        stringBuilder.append(String.format("%d, %d, %d", worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane()));
        stringBuilder.append(", ");

        // End tile
        stringBuilder.append("None, None, None");
        stringBuilder.append(", ");

        // Name
        stringBuilder.append("'Name'");
        stringBuilder.append(", ");

        // Object ID
        stringBuilder.append(String.format("%d", objectId));
        stringBuilder.append(", ");

        // Object Name
        stringBuilder.append(String.format("'%s'", objectName));
        stringBuilder.append("),");

        System.out.println(stringBuilder.toString());
    }
}
