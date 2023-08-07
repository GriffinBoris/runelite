package net.runelite.client.plugins.alfred.api.rs.walk;

import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.menu.RSMenu;
import net.runelite.client.plugins.alfred.api.rs.player.RSPlayer;
import net.runelite.client.plugins.alfred.api.rs.walk.astar.AStarNode;

import java.util.List;

public class Path {
    @Getter
    private WorldPoint start;
    @Getter
    private WorldPoint end;
    @Getter
    private AStarNode node;
    private RSPlayer player;

    public Path(WorldPoint start, WorldPoint end, AStarNode node) {
        this.start = start;
        this.end = end;
        this.node = node;
        player = Alfred.api.players().getLocalPlayer();
    }

    public boolean walk() {
        Point minimapPoint = Alfred.api.miniMap().getWorldPointToScreenPoint(start);
        if (minimapPoint == null) {
            return false;
        }

        if (node.getIsOperable()) {
            RSTile realTile = findTile(node);
            if (realTile == null) {
                return false;
            }

            return operateOnTile(node, realTile.getTile());

        } else {
            Alfred.getMouse().leftClick(minimapPoint);
            Alfred.sleep(1000);
            return Alfred.sleepUntil(() -> !player.isMoving() && !player.isInteracting() && player.isIdle(), 200, 1000 * 10);
        }
    }

    private RSTile findTile(AStarNode node) {
        List<RSTile> tiles = Alfred.api.walk().getAllTiles();
        for (RSTile tile : tiles) {
            if (tile.getWorldLocation().equals(node.getWorldLocation())) {
                return tile;
            }
        }
        return null;
    }

    private boolean operateOnTile(AStarNode node, Tile tile) {
        Alfred.sleepUntil(() -> !player.isMoving() && !player.isInteracting() && player.isIdle(), 200, 1000 * 10);
        boolean success = false;
        if (node.getOperableName().equals("DOOR") || node.getOperableName().equals("GATE")) {
            success = operateOnDoor(tile);

        } else if (node.getOperableName().equals("STAIRS") || node.getOperableName().equals("STAIRCASE")) {
            success = operateOnStair(tile);
        }

        if (!success) {
            return false;
        }

        Alfred.sleep(1000);
        Alfred.sleepUntil(() -> !player.isMoving() && !player.isInteracting() && player.isIdle(), 200, 1000 * 10);
        Alfred.sleep(250, 500);
        return true;
    }

    private boolean operateOnDoor(Tile tile) {
        WallObject wallObject = tile.getWallObject();
        if (wallObject == null) {
            return false;
        }

        Alfred.getMouse().rightClick(wallObject.getConvexHull().getBounds());
        Alfred.sleep(200);

        RSMenu rsMenu = Alfred.api.menu().getMenu();
        if (rsMenu == null) {
            System.out.println("Menu is null");
            return false;
        }

        if (!rsMenu.hasAction("open")) {
            System.out.println("Menu does not contain open action");
            return true;
        }

        if (!rsMenu.clickAction("open")) {
            System.out.println("Failed to operate on tile");
            return false;
        }
        return true;
    }

    private boolean operateOnStair(Tile tile) {
        int startZ = start.getPlane();
        int endZ = end.getPlane();

        if (startZ == endZ) {
            return true;
        }

        boolean goingUp = endZ > startZ;
        String action = goingUp ? "climb-up" : "climb-down";

        RSPlayer player = Alfred.api.players().getLocalPlayer();
        GameObject foundGameObject = null;

        for (GameObject gameObject : tile.getGameObjects()) {
            if (gameObject == null) {
                continue;
            }

            String tileName = Alfred.api.objects().getObjectIdVariableName(gameObject.getId());
            if (tileName.contains("STAIRS") || tileName.contains("STAIRCASE")) {
                foundGameObject = gameObject;
                break;
            }
        }

        if (foundGameObject == null) {
            return false;
        }

        Alfred.getMouse().rightClick(foundGameObject.getClickbox().getBounds());
        Alfred.sleep(200);

        RSMenu rsMenu = Alfred.api.menu().getMenu();
        if (rsMenu == null) {
            System.out.println("Menu is null");
            return false;
        }

        if (!rsMenu.hasAction(action)) {
            System.out.println("Menu does not contain action");
            return true;
        }

        if (!rsMenu.clickAction(action)) {
            System.out.println("Failed to operate on tile");
            return false;
        }

        return Alfred.sleepUntil(() -> player.getWorldLocation().getPlane() == endZ, 100, 5000);
    }
}
