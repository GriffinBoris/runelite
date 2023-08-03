import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;

@PluginDescriptor(
    name = "Interact Highlight",
    description = "Highlights interactable objects",
    tags = {"highlight", "interact"}
)
public class InteractHighlightPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private InteractHighlightOverlay overlay;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        MenuEntry[] menuEntries = client.getMenuEntries();
        for (MenuEntry entry : menuEntries)
        {
            if (entry.getOption().equals("Examine") && entry.getType() == MenuAction.EXAMINE_OBJECT.getId())
            {
                entry.setOption("Interact");
            }
        }
        client.setMenuEntries(menuEntries);
    }
}
