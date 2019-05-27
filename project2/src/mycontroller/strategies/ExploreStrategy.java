package mycontroller.strategies;

import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import mycontroller.adapters.AdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Random;

/**
 * Team: W9-5
 * Description:
 */
public class ExploreStrategy implements iControllerStrategy {

    private HashMap<TileType, Integer> pathCost;

    public ExploreStrategy(HashMap<TileType, Integer> pathCost) {
        this.pathCost = pathCost;
    }

    @Override
    public Coordinate getNextPosition(float fuel, Coordinate currPos, ArrayList<Coordinate> goals, HashMap<Coordinate, MapTile> map, int[][] seenWorld) {
//        System.out.println("~~~~~~~~~`");
        iSearchStrategy searchAlg = SearchStrategyFactory.getInstance().getStrategy(SearchAlgorithmType.Dijkstra);


//        Coordinate des = null;
//        for(Coordinate next: map.keySet()) {
//            if(map.get(next).isType(MapTile.Type.FINISH)) {
//                des = next;
//                break;
//            }
//        }
        ArrayList<Coordinate> nextPos = getPosAround(currPos, map);
        ArrayList<Coordinate> unseenPos = new ArrayList<>();
        for(Coordinate next: nextPos) {
            if(seenWorld[next.y][next.x] == 0) {
                unseenPos.add(next);
            }
        }
        Random rand = new Random();
        if(unseenPos.size() > 0) {
            // randomly choose one in all unseen next pos
//            return unseenPos.get(rand.nextInt(unseenPos.size()));
            return searchAlg.search(currPos, unseenPos, map, pathCost).get(1);
        }
        else {
            // all pos around seen, return the closest unseen pos (may be far away)
//            return nextPos.get(rand.nextInt(nextPos.size()));
            System.out.println("all seennnnnnn");
//            Coordinate temp = new Coordinate(3, 5);
//            int minDist = manhattan_dist(currPos, des);
//            Coordinate temp = null;
//            for(int i = 0; i < seenWorld.length; i++) {
//                for(int j = 0; j < seenWorld[i].length; j++) {
//                    if(seenWorld[i][j] == 0) {
//                        temp = new Coordinate(j, i);
//                        break;
//                    }
//                }
//            }
//            int currDist;
//            Coordinate bestChoice = temp;
//            int minDist = manhattan_dist(currPos, temp);
//            for(int i = 0; i < seenWorld.length; i++) {
//                for(int j = 0; j < seenWorld[i].length; j++) {
//                    if (seenWorld[i][j] == 0) {
//                        temp = new Coordinate(j, i);
//                        if ((currDist = manhattan_dist(currPos, temp)) < minDist &&
//                                SearchStrategyFactory.getInstance().getStrategy(SearchAlgorithmType.Dijkstra).search(currPos, temp, map, pathCost) != null) {
////                            System.out.println("&&&&&&&&&&&&&&&&&&&");
//                            minDist = currDist;
//                            bestChoice = temp;
////                            System.out.println(bestChoice);
//                        }
//                    }
//                }
//            }

//            Coordinate temp, bestChoice = null;
//            double minDist = Double.POSITIVE_INFINITY;
//            ArrayList<Coordinate> path;
//            for(int i = 0; i < seenWorld.length; i++) {
//                for(int j = 0; j < seenWorld[i].length; j++) {
//                    temp = new Coordinate(j, i);
//                    path = searchAlg.search(temp, goals, map, pathCost);
//                    if(path == null) {
//                        continue;
//                    }
//                    if(path.size() < minDist) {
//                        bestChoice = path.get(1);
//                        minDist = path.size();
//                    }
//                }
//            }
//            return bestChoice;

//            if(!unseenPos.isEmpty()) {
//                System.out.println(">>>>\n<<<<<\n>>>>>");
//            }
            ArrayList<Coordinate> candidate = new ArrayList<>();
            for(int i = 0; i < seenWorld.length; i++) {
                for(int j = 0; j < seenWorld[i].length; j++) {
                    if(seenWorld[i][j] == 0) {
                        candidate.add(new Coordinate(j, i));
                    }
                }
            }
            return searchAlg.search(currPos, candidate, map, pathCost).get(1);

//            System.out.println("best >>>>>>> " + bestChoice);
//            if(currPos.equals(bestChoice)) {
//                return currPos;
//            }
//            return SearchStrategyFactory.getInstance().getStrategy(SearchAlgorithmType.Dijkstra).search(currPos, bestChoice, map, pathCost).get(1);
        }
    }

    private int manhattan_dist(Coordinate c1, Coordinate c2) {
        return Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y);
    }

    // return the positions next to the given point
    private ArrayList<Coordinate> getPosAround(Coordinate currPos, HashMap<Coordinate, MapTile> map) {

        ArrayList<Coordinate> out = new ArrayList<>();
//        for(int i = currPos.x - Car.VIEW_SQUARE; i <= currPos.x + Car.VIEW_SQUARE; i ++) {
//            out.add(new Coordinate(i, currPos.y - Car.VIEW_SQUARE - 1));
//            out.add(new Coordinate(i, currPos.y + Car.VIEW_SQUARE + 1));
//        }
//        for(int i = currPos.y - Car.VIEW_SQUARE; i <= currPos.y + Car.VIEW_SQUARE; i ++) {
//            out.add(new Coordinate(currPos.x - Car.VIEW_SQUARE - 1, i));
//            out.add(new Coordinate(currPos.x + Car.VIEW_SQUARE + 1, i));
//        }


//        Coordinate vertex1 = new Coordinate(currPos.x, currPos.y);
        out.add(new Coordinate(currPos.x + Car.VIEW_SQUARE + 1, currPos.y));
        out.add(new Coordinate(currPos.x - Car.VIEW_SQUARE - 1, currPos.y));
        out.add(new Coordinate(currPos.x, currPos.y + Car.VIEW_SQUARE + 1));
        out.add(new Coordinate(currPos.x, currPos.y - Car.VIEW_SQUARE - 1));
        // remove any invalid coors
        ListIterator<Coordinate> i = out.listIterator(0);
        while(i.hasNext()) {
            Coordinate temp = i.next();
            if(!isValidCoor(temp, currPos, map)) {
//                System.out.println(manhattan_dist(temp, coor, map));
                i.remove();
            }
        }
//        for(Coordinate next: out) {
////            System.out.print(map.get(next).isType(MapTile.Type.WALL) + "  ");
//            System.out.print(next + "  ");
//        }
//        System.out.println("");
//        System.out.println(" enter pos aroundddddd   " + out.size());
        return out;
    }

    private boolean isValidCoor(Coordinate coor, Coordinate curr, HashMap<Coordinate, MapTile> map) {
        return onBoard(coor) && !map.get(coor).isType(MapTile.Type.WALL);// &&
//                !hasWallBetween(coor, curr, map);
    }

    private boolean onBoard(Coordinate coor) {
        return 0 <= coor.x && coor.x < World.MAP_WIDTH &&
                0 <= coor.y && coor.y < World.MAP_HEIGHT;
    }

//    private boolean hasWallBetween(Coordinate c1, Coordinate c2, HashMap<Coordinate, MapTile> map) {
//        iSearchStrategy searchStrategy = SearchStrategyFactory.getInstance().getStrategy(SearchAlgorithmType.Dijkstra);
//        ArrayList<Coordinate> path = searchStrategy.search(c1, c2, map);
//        return path == null || manhattan_dist(c1, c2) != path.size() - 1;
//    }

    // 1 for explored and 0 for not
//    public boolean isExplored(Coordinate coor) {
//        return exploreMap[coor.x][coor.y] == 1;
//    }

    // TODO decide detail implementation, input parameter
    // v1. update for seen world
//    public void updateMap(HashMap<Coordinate, MapTile> view) {
//        for(Coordinate next: view.keySet()) {
//            if(exploreMap[next.x][next.y] == 0) {
//                exploreMap[next.x][next.y] = 1;
//            }
//        }
//    }

    // v2. update for visited world
//    public void updateMap(Coordinate currPos, Integer[][] seenWorld) {
//        if(exploreMap[currPos.x][currPos.y] == 0) {
//            exploreMap[currPos.x][currPos.y] = 1;
//        }
//    }

    public boolean canExit(int fuel, Coordinate coor, Integer[][] distMap) {
        return distMap[coor.x][coor.y] <= fuel;
    }

    // return true if the car can see parcel, false otherwise
    public boolean seeParcel(HashMap<Coordinate, MapTile> view, AdapterFactory factory) {
        for(Coordinate next: view.keySet()) {
            MapTile tile = view.get(next);
            if(factory.getAdapter(tile).getType(tile) == TileType.PARCEL) {
                return true;
            }
        }
        return false;
    }
}
