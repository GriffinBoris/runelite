import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;

@PluginDescriptor(
    name = "Instance Map Input Listener",
    description = "Highlights objects on the instance map when interacted with",
    tags = {"highlight", "instance", "map"}
)
public class InstanceMapInputListener extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private InstanceMapOverlay overlay;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private KeyManager keyManager;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        mouseManager.registerMouseListener(mouseAdapter);
        keyManager.registerKeyListener(keyAdapter);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        mouseManager.unregisterMouseListener(mouseAdapter);
        keyManager.unregisterKeyListener(keyAdapter);
    }

    private final MouseAdapter mouseAdapter = new MouseAdapter()
    {
        @Override
        public MouseEvent mousePressed(MouseEvent event)
        {
            if (event.getButton() == MouseEvent.BUTTON1)
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
            return event;
        }
    };

    private final KeyAdapter keyAdapter = new KeyAdapter()
    {
        @Override
        public KeyEvent keyPressed(KeyEvent event)
        {
            if (event.getKeyCode() == KeyEvent.VK_SPACE)
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
            return event;
        }
    };

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
