package net.runelite.client.plugins.alfred.scripts.vialbuyer;

import net.runelite.api.GameState;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.AlfredOverlayBuilder;
import net.runelite.client.plugins.alfred.util.PlayTimer;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class VialBuyerOverlay extends Overlay {

    PlayTimer playTimer = new PlayTimer();
    public static int vialsBought;
    public static int vialsPerHour;

    @Inject
    private VialBuyerOverlay() {
        vialsBought = 0;
        vialsPerHour = 0;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGHEST);
        playTimer.start();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (Alfred.Companion.getClient().getGameState() != GameState.LOGGED_IN) {
            return null;
        }

        Widget widget = Alfred.Companion.getApi().getWidgets().getWidget(WidgetInfo.CHATBOX_MESSAGE_LINES);
        if (widget == null || widget.isHidden() || widget.isSelfHidden()) {
            return null;
        }

        AlfredOverlayBuilder overlayBuilder = new AlfredOverlayBuilder(graphics, widget.getBounds());
        overlayBuilder.drawBounds();
        overlayBuilder.drawTitle("Alfred Vial Buyer");
        overlayBuilder.drawText("Elapsed Run Time:", playTimer.getElapsedTimeString(), false);
        overlayBuilder.drawText("", false);
        overlayBuilder.drawText("Vials Bought:", Integer.toString(vialsBought), false);
        overlayBuilder.drawText("Vials Per Hour:", Integer.toString(vialsPerHour), false);

//        overlayBuilder.drawText("", true);
//        overlayBuilder.drawText("", false);

        overlayBuilder.drawText("Task:", Alfred.Companion.getTaskStatus(), true);
        overlayBuilder.drawText("Task Status:", Alfred.Companion.getTaskSubStatus(), true);

        overlayBuilder.drawText("", false);
        overlayBuilder.drawText("Current Status:", Alfred.Companion.getStatus(), true);
        return null;
    }
}
