package net.runelite.client.plugins.alfred.scripts.autocollection;

import net.runelite.api.GameState;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.alfred.Alfred;
import net.runelite.client.plugins.alfred.api.rs.player.RSPlayer;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.LiveWorldDataLoader;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathFinder;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathNode;
import net.runelite.client.plugins.alfred.api.rs.walk.pathfinder.PathWalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCollectionThread extends Thread {

    public static String searching = "";
    private List<WorldPoint> usedPoints;
    private List<WorldPoint> unexploredPoints = List.of(
            new WorldPoint(2682, 3635, 0),
            new WorldPoint(2701, 3649, 0),
            new WorldPoint(2699, 3674, 0),
            new WorldPoint(2699, 3705, 0),
            new WorldPoint(2698, 3726, 0),
            new WorldPoint(2706, 3734, 0),
            new WorldPoint(2718, 3730, 0),
            new WorldPoint(2714, 3710, 0),
            new WorldPoint(2716, 3697, 0),
            new WorldPoint(2725, 3683, 0),
            new WorldPoint(2733, 3674, 0),
            new WorldPoint(2748, 3648, 0),
            new WorldPoint(2753, 3634, 0),
            new WorldPoint(2755, 3621, 0),
            new WorldPoint(2754, 3600, 0),
            new WorldPoint(2745, 3592, 0),
            new WorldPoint(2734, 3596, 0),
            new WorldPoint(2714, 3600, 0),
            new WorldPoint(2693, 3603, 0),
            new WorldPoint(2680, 3608, 0),
            new WorldPoint(2681, 3620, 0),
            new WorldPoint(2721, 3617, 0),
            new WorldPoint(2736, 3616, 0),
            new WorldPoint(2732, 3641, 0),
            new WorldPoint(2715, 3671, 0),
            new WorldPoint(2710, 3637, 0)
    );


    private WorldArea area = new WorldArea(2957, 3528, 466, 366, 0);

    public AutoCollectionThread() {
        usedPoints = new ArrayList<>();
    }

    @Override
    public void run() {
        if (Alfred.getClient().getGameState() != GameState.LOGGED_IN) {
            Alfred.api.account().login();
        }

        RSPlayer player = Alfred.api.players().getLocalPlayer();
        LiveWorldDataLoader liveWorldDataLoader = new LiveWorldDataLoader();

        for (WorldPoint unexploredPoint : unexploredPoints) {
            int distance = player.getWorldLocation().distanceTo(unexploredPoint);

            while (distance > 5) {
                PathNode[][][] grid = liveWorldDataLoader.getGrid();
                WorldPoint farthestPoint = getFarthestWalkablePoint(unexploredPoint, grid);

                if (farthestPoint == null) {
                    break;
                }

                PathFinder pathFinder = new PathFinder(grid);
                List<PathNode> path = pathFinder.findPath(player.getWorldLocation(), farthestPoint);

                PathWalker pathWalker = new PathWalker(path);
                pathWalker.walkPath();

                distance = player.getWorldLocation().distanceTo(unexploredPoint);
            }

        }

//            unexploredPoints = loadAllUnexploredPoints();
//        while (!unexploredPoints.isEmpty()) {
//            walkToPoint();
//            unexploredPoints = loadAllUnexploredPoints();
//
//            for (int i = 0; i < 20; i++) {
//                System.out.println(String.format("Sleeping %d/20", i + 1));
//                Alfred.sleep(1000);
//            }

    }

    private WorldPoint getFarthestWalkablePoint(WorldPoint target, PathNode[][][] grid) {
        int z = target.getPlane();
        List<WorldPoint> walkablePoints = new ArrayList<>();

        for (int y = 0; y < grid[z].length - 1; y++) {
            for (int x = 0; x < grid[z][y].length - 1; x++) {
                PathNode node = grid[z][y][x];
                if (node == null) {
                    continue;
                }

                if (node.getBlocked()) {
                    continue;
                }

                if (node.getBlockedMovementEast() && node.getBlockedMovementWest() && node.getBlockedMovementNorth() && node.getBlockedMovementSouth()) {
                    continue;
                }

                walkablePoints.add(node.getWorldLocation());
            }
        }

        walkablePoints = walkablePoints.stream().sorted(Comparator.comparingInt(wp -> wp.distanceTo(target))).collect(Collectors.toList());
        return walkablePoints.get(0);
    }

//    private List<WorldPoint> loadAllUnexploredPoints() {
//        SavedWorldDataLoader savedWorldDataLoader = new SavedWorldDataLoader("/home/griffin/PycharmProjects/OSRSWorld/world/db.sqlite3");
//        PathNode[][][] grid = savedWorldDataLoader.getGrid();
//
//        List<WorldPoint> unexploredPoints = new ArrayList<>();
//
//        for (int z = 0; z < grid.length - 1; z++) {
//            for (int y = 0; y < grid[z].length - 1; y++) {
//                for (int x = 0; x < grid[z][y].length - 1; x++) {
//                    PathNode node = grid[z][y][x];
//                    if (node != null) {
//                        continue;
//                    }
//
//                    WorldPoint newPoint = new WorldPoint(x, y, z);
//                    if (!area.contains(newPoint)) {
//                        continue;
//                    }
//
//                    unexploredPoints.add(newPoint);
//                }
//            }
//        }
//
//        RSPlayer player = Alfred.api.players().getLocalPlayer();
//        WorldPoint playerLocation = player.getWorldLocation();
//
//        unexploredPoints = unexploredPoints.stream().filter(worldPoint -> worldPoint.getPlane() == playerLocation.getPlane()).collect(Collectors.toList());
//        unexploredPoints = unexploredPoints.stream().sorted(Comparator.comparingInt(worldPoint -> worldPoint.distanceTo(playerLocation))).collect(Collectors.toList());
////        Collections.reverse(unexploredPoints);
//        return unexploredPoints;
//    }
//
//    private void walkToPoint() {
//        RSPlayer player = Alfred.api.players().getLocalPlayer();
//        WorldPoint start = player.getWorldLocation();
//
//        SavedWorldDataLoader savedWorldDataLoader = new SavedWorldDataLoader("/home/griffin/PycharmProjects/OSRSWorld/world/db.sqlite3");
//        PathNode[][][] grid = savedWorldDataLoader.getGrid();
//
//        List<PathNode> nodes = null;
//
//        int unexploredCount = unexploredPoints.size() - 1;
//        int counter = 0;
//        for (WorldPoint unexploredPoint : unexploredPoints) {
//            counter++;
//
//            if (usedPoints.contains(unexploredPoint)) {
//                continue;
//            }
//
//            WorldPoint target = getNearestPoint(grid, unexploredPoint);
//
//            PathFinder pathFinder = new PathFinder(grid);
//            nodes = pathFinder.findPath(start, target);
//
//            searching = String.format("%d/%d", counter, unexploredCount);
//
//            if (!nodes.isEmpty()) {
//                break;
//            }
//
//            usedPoints.add(unexploredPoint);
//        }
//
//        PathWalker pathWalker = new PathWalker(nodes);
//        pathWalker.walkPath();
//    }
//
//    private WorldPoint getNearestPoint(PathNode[][][] grid, WorldPoint target) {
//        RSPlayer player = Alfred.api.players().getLocalPlayer();
//        WorldPoint playerLocation = player.getWorldLocation();
//
//        List<PathNode> validNodes = new ArrayList<>();
//
//        for (int z = 0; z < grid.length - 1; z++) {
//            for (int y = 0; y < grid[z].length - 1; y++) {
//                for (int x = 0; x < grid[z][y].length - 1; x++) {
//                    PathNode node = grid[z][y][x];
//                    if (node == null) {
//                        continue;
//                    }
//
//                    if (!area.contains(node.getWorldLocation())) {
//                        continue;
//                    }
//
//                    validNodes.add(node);
//                }
//            }
//        }
//        validNodes = validNodes.stream().filter(pathNode -> pathNode.getWorldLocation().getPlane() == playerLocation.getPlane()).collect(Collectors.toList());
//        validNodes = validNodes.stream().filter(pathNode -> !pathNode.getBlocked()).sorted(Comparator.comparingInt(pathNode -> pathNode.getWorldLocation().distanceTo(target))).collect(Collectors.toList());
////        Collections.reverse(validNodes);
//        return validNodes.get(0).getWorldLocation();
//    }
}
