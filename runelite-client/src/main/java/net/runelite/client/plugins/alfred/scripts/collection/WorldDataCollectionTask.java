package net.runelite.client.plugins.alfred.scripts.collection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.runelite.api.*;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.walk.RSWalkHelper;
import net.runelite.client.plugins.alfred.api.rs.walk.WorldMovementFlag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldDataCollectionTask implements Runnable {

    private final int num;

    public WorldDataCollectionTask(int num) {
        this.num = num;
    }

    @Override
    public void run() {
        System.out.println("Thread " + num + " started");

        List<Tile> tiles = getTiles();
        JsonArray json = getJson(tiles);
        writeToFile(json);

        System.out.println("Thread " + num + " finished");
        WorldDataCollectionThread.completed++;
    }

    private List<Tile> getTiles() {
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

                    if (player.getLocalLocation().distanceTo(tile.getLocalLocation()) <= 2400) {
                        tileList.add(tile);
                    }
                }
            }

            return tileList;
        });
    }

    public Set<WorldMovementFlag> getMovementFlagsForTile(Tile tile) {
        Client client = Alfred.Companion.getClient();
        if (client.getCollisionMaps() != null) {
            int[][] flags = client.getCollisionMaps()[client.getPlane()].getFlags();
            int data = flags[tile.getSceneLocation().getX()][tile.getSceneLocation().getY()];

            return WorldMovementFlag.getSetFlags(data);
        }

        return new HashSet<>();
    }

    public boolean isOperable(Tile tile) {
        return Alfred.Companion.getClientThread().invokeOnClientThread(() -> {
            boolean wallObjectOperable = false;
            boolean gameObjectOperable = false;

            if (tile.getWallObject() != null) {
                wallObjectOperable = RSWalkHelper.getOperableObjectIds().contains(tile.getWallObject().getId());
            }

            for (GameObject gameObject : tile.getGameObjects()) {
                if (gameObject == null) {
                    continue;
                }

                gameObjectOperable = RSWalkHelper.getOperableObjectIds().contains(gameObject.getId());
                if (gameObjectOperable) {
                    break;
                }
            }

            if (wallObjectOperable && gameObjectOperable) {
                System.out.println("UH OH BOTH ARE OPERABLE");
            }

            return wallObjectOperable || gameObjectOperable;
        });
    }

    public String getOperableName(Tile tile) {
        return Alfred.Companion.getClientThread().invokeOnClientThread(() -> {
            boolean wallObjectOperable = false;
            boolean gameObjectOperable = false;

            WallObject foundWallObject = null;
            GameObject foundGameObject = null;

            if (tile.getWallObject() != null) {
                wallObjectOperable = RSWalkHelper.getOperableObjectIds().contains(tile.getWallObject().getId());
                foundWallObject = tile.getWallObject();
            }

            for (GameObject gameObject : tile.getGameObjects()) {
                if (gameObject == null) {
                    continue;
                }

                gameObjectOperable = RSWalkHelper.getOperableObjectIds().contains(gameObject.getId());
                if (gameObjectOperable) {
                    foundGameObject = gameObject;
                    break;
                }
            }

            if (wallObjectOperable && gameObjectOperable) {
                System.out.println("UH OH BOTH ARE OPERABLE");
            }

            if (wallObjectOperable) {
                return Alfred.Companion.getApi().getObjects().getObjectIdVariableName(foundWallObject.getId());
            }

            if (gameObjectOperable) {
                return Alfred.Companion.getApi().getObjects().getObjectIdVariableName(foundGameObject.getId());
            }

            return null;
        });
    }

    private JsonArray getJson(List<Tile> tiles) {
        JsonArray rows = new JsonArray();
        for (Tile tile : tiles) {

            Set<WorldMovementFlag> movementFlags = getMovementFlagsForTile(tile);
            JsonObject row = new JsonObject();

            row.addProperty("x", tile.getWorldLocation().getX());
            row.addProperty("y", tile.getWorldLocation().getY());
            row.addProperty("z", tile.getWorldLocation().getPlane());
            row.addProperty("is_operable", isOperable(tile));
            row.addProperty("operable_object_name", getOperableName(tile));
            row.addProperty("block_movement_full", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_FULL));
            row.addProperty("block_movement_floor", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_FLOOR));
            row.addProperty("block_movement_floor_decoration", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_FLOOR_DECORATION));
            row.addProperty("block_movement_object", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_OBJECT));
            row.addProperty("block_movement_north", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_NORTH));
            row.addProperty("block_movement_east", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_EAST));
            row.addProperty("block_movement_south", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_SOUTH));
            row.addProperty("block_movement_west", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_WEST));
            row.addProperty("block_movement_north_east", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_NORTH_EAST));
            row.addProperty("block_movement_north_west", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_NORTH_WEST));
            row.addProperty("block_movement_south_east", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_SOUTH_EAST));
            row.addProperty("block_movement_south_west", movementFlags.contains(WorldMovementFlag.BLOCK_MOVEMENT_SOUTH_WEST));

            rows.add(row);
        }

        return rows;
    }

    private void writeToFile(JsonArray rows) {
        try {
            String path = "/home/griffin/PycharmProjects/OSRSWorld/test/data/";
            String name = System.currentTimeMillis() + ".json";

            FileWriter writer = new FileWriter(path + name);
            BufferedWriter out = new BufferedWriter(writer, 32768);
            out.write(rows.toString());
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
