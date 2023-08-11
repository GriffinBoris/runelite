package net.runelite.client.plugins.alfred.scripts.autocollection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.player.RSPlayer;
import net.runelite.client.plugins.alfred.api.rs.walk.RSWalkHelper;
import net.runelite.client.plugins.alfred.api.rs.walk.WorldMovementFlag;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathFinder;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathNode;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathWalker;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.WorldDataLoader;
import net.runelite.http.api.worlds.WorldRegion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AutoCollectionThread extends Thread {

    public static String searching = "";
    private List<WorldPoint> usedPoints;
    private List<WorldPoint> unexploredPoints;

    private WorldArea area = new WorldArea(2957, 3528, 466, 366, 0);

    public AutoCollectionThread() {
        usedPoints = new ArrayList<>();
        unexploredPoints = new ArrayList<>();
    }

    @Override
    public void run() {
        if (Alfred.getClient().getGameState() != GameState.LOGGED_IN) {
            Alfred.api.account().login();
        }

        unexploredPoints = loadAllUnexploredPoints();
        while (!unexploredPoints.isEmpty()) {
            walkToPoint();
            unexploredPoints = loadAllUnexploredPoints();

            for (int i = 0; i < 20; i++) {
                System.out.println(String.format("Sleeping %d/20", i + 1));
                Alfred.sleep(1000);
            }

        }
    }

    private List<WorldPoint> loadAllUnexploredPoints() {
        WorldDataLoader worldDataLoader = new WorldDataLoader("/home/griffin/PycharmProjects/OSRSWorld/world/db.sqlite3");
        PathNode[][][] grid = worldDataLoader.getGrid();

        List<WorldPoint> unexploredPoints = new ArrayList<>();

        for (int z = 0; z < grid.length - 1; z++) {
            for (int y = 0; y < grid[z].length - 1; y++) {
                for (int x = 0; x < grid[z][y].length - 1; x++) {
                    PathNode node = grid[z][y][x];
                    if (node != null) {
                        continue;
                    }

                    WorldPoint newPoint = new WorldPoint(x, y, z);
                    if (!area.contains(newPoint)) {
                        continue;
                    }

                    unexploredPoints.add(newPoint);
                }
            }
        }

        RSPlayer player = Alfred.api.players().getLocalPlayer();
        WorldPoint playerLocation = player.getWorldLocation();

        unexploredPoints = unexploredPoints.stream().filter(worldPoint -> worldPoint.getPlane() == playerLocation.getPlane()).collect(Collectors.toList());
        unexploredPoints = unexploredPoints.stream().sorted(Comparator.comparingInt(worldPoint -> worldPoint.distanceTo(playerLocation))).collect(Collectors.toList());
//        Collections.reverse(unexploredPoints);
        return unexploredPoints;
    }

    private void walkToPoint() {
        RSPlayer player = Alfred.api.players().getLocalPlayer();
        WorldPoint start = player.getWorldLocation();

        WorldDataLoader worldDataLoader = new WorldDataLoader("/home/griffin/PycharmProjects/OSRSWorld/world/db.sqlite3");
        PathNode[][][] grid = worldDataLoader.getGrid();

        List<PathNode> nodes = null;

        int unexploredCount = unexploredPoints.size() - 1;
        int counter = 0;
        for (WorldPoint unexploredPoint : unexploredPoints) {
            counter++;

            if (usedPoints.contains(unexploredPoint)) {
                continue;
            }

            WorldPoint target = getNearestPoint(grid, unexploredPoint);

            PathFinder pathFinder = new PathFinder(grid);
            nodes = pathFinder.findPath(start, target);

            searching = String.format("%d/%d", counter, unexploredCount);

            if (!nodes.isEmpty()) {
                break;
            }

            usedPoints.add(unexploredPoint);
        }

        PathWalker pathWalker = new PathWalker(nodes);
        pathWalker.walkPath();
    }

    private WorldPoint getNearestPoint(PathNode[][][] grid, WorldPoint target) {
        RSPlayer player = Alfred.api.players().getLocalPlayer();
        WorldPoint playerLocation = player.getWorldLocation();

        List<PathNode> validNodes = new ArrayList<>();

        for (int z = 0; z < grid.length - 1; z++) {
            for (int y = 0; y < grid[z].length - 1; y++) {
                for (int x = 0; x < grid[z][y].length - 1; x++) {
                    PathNode node = grid[z][y][x];
                    if (node == null) {
                        continue;
                    }

                    if (!area.contains(node.getWorldLocation())) {
                        continue;
                    }

                    validNodes.add(node);
                }
            }
        }
        validNodes = validNodes.stream().filter(pathNode -> pathNode.getWorldLocation().getPlane() == playerLocation.getPlane()).collect(Collectors.toList());
        validNodes = validNodes.stream().filter(pathNode -> !pathNode.getBlocked()).sorted(Comparator.comparingInt(pathNode -> pathNode.getWorldLocation().distanceTo(target))).collect(Collectors.toList());
//        Collections.reverse(validNodes);
        return validNodes.get(0).getWorldLocation();
    }
}
