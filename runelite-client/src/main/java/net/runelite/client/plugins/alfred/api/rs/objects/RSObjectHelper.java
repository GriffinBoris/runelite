package net.runelite.client.plugins.alfred.api.rs.objects;

import net.runelite.api.*;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.item.RSGroundItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RSObjectHelper {

    private static final int MAX_DISTANCE = 2400;

    public RSObjectHelper() {
    }

    private List<Tile> internalGetTiles() {
        return Alfred.Companion.getClientThread().invokeOnClientThread(() -> {
            List<Tile> tileList = new ArrayList<>();

            Player player = Alfred.Companion.getClient().getLocalPlayer();
            Tile[][][] tiles = Alfred.Companion.getClient().getScene().getTiles();
            int z = Alfred.Companion.getClient().getPlane();

            for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
                for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                    Tile tile = tiles[z][x][y];
                    if (tile == null) {
                        continue;
                    }

                    if (player.getLocalLocation().distanceTo(tile.getLocalLocation()) <= MAX_DISTANCE) {
                        tileList.add(tile);
                    }
                }
            }

            return tileList;
        });
    }

    public List<RSGroundItem> getItemsFromTiles() {
        List<RSGroundItem> rsGroundItemList = new ArrayList<>();
        List<Tile> tileList = internalGetTiles();

        for (Tile tile : tileList) {
            ItemLayer itemLayer = tile.getItemLayer();
            if (itemLayer == null) {
                continue;
            }

            Node current = itemLayer.getTop();
            while (current instanceof TileItem) {
                TileItem tileItem = (TileItem) current;
                rsGroundItemList.add(new RSGroundItem(tileItem, tile));
                current = current.getNext();
            }
        }

        return rsGroundItemList;
    }


    public List<RSObject> getObjectsFromTiles() {
        Player player = Alfred.Companion.getClient().getLocalPlayer();
        List<RSObject> rsObjectList = new ArrayList<>();
        List<Tile> tileList = internalGetTiles();

        for (Tile tile : tileList) {
            if (tile.getWallObject() != null) {
                if (player.getLocalLocation().distanceTo(tile.getWallObject().getLocalLocation()) <= MAX_DISTANCE) {
                    rsObjectList.add(new RSObject(tile.getWallObject()));
                }
            }

            if (tile.getGroundObject() != null) {
                if (player.getLocalLocation().distanceTo(tile.getGroundObject().getLocalLocation()) <= MAX_DISTANCE) {
                    rsObjectList.add(new RSObject(tile.getGroundObject()));
                }
            }

            for (GameObject gameObject : tile.getGameObjects()) {
                if (gameObject == null) {
                    continue;
                }

                if (!gameObject.getSceneMinLocation().equals(tile.getSceneLocation())) {
                    continue;
                }

                if (player.getLocalLocation().distanceTo(gameObject.getLocalLocation()) <= MAX_DISTANCE) {
                    rsObjectList.add(new RSObject(gameObject));
                }
            }
        }

        return rsObjectList;
    }

    public List<RSObject> getObjectsFromTiles(String name) {
        return Alfred.Companion.getClientThread().invokeOnClientThread(() -> {
            List<RSObject> objects = new ArrayList<>();
            List<RSObject> rsObjectList = getObjectsFromTiles();

            for (RSObject rsObject : rsObjectList) {
                if (rsObject.getName().equalsIgnoreCase(name)) {
                    objects.add(rsObject);
                }
            }

            return objects;
        });
    }

    public String getObjectIdVariableName(int objectId) {
        Field[] fields = ObjectID.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getType() == int.class) {
                try {
                    int fieldValue = field.getInt(null);
                    if (fieldValue == objectId) {
                        return field.getName();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
