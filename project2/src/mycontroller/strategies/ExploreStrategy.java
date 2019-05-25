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



    public ExploreStrategy() {

    }

    @Override
    public Coordinate getNextPosition(float fuel, Coordinate currPos, Coordinate des, HashMap<Coordinate, MapTile> map, Integer[][] seenWorld) {
        System.out.println("~~~~~~~~~`");
        ArrayList<Coordinate> nextPos = getPosAround(currPos, map);
        ArrayList<Coordinate> unseenPos = new ArrayList<>();
        for(Coordinate next: nextPos) {
            if(seenWorld[next.x][next.y] == 0) {
                unseenPos.add(next);
            }
        }
        Random rand = new Random();
        if(unseenPos.size() > 0) {
            // randomly choose in all unseen next pos
            return unseenPos.get(rand.nextInt(unseenPos.size()));
        }
        else {
            // all next pos seen, randomly choose one
//            return nextPos.get(rand.nextInt(nextPos.size()));
            System.out.println("all seennnnnnn");
            Coordinate temp = new Coordinate(3, 5);
            int minDist = manhattan_dist(currPos, temp);
            int currDist;
            Coordinate bestChoice = temp;
            for(int i = 0; i < seenWorld.length; i++) {
                for(int j = 0; j < seenWorld[i].length; j++) {
                    if (seenWorld[i][j] == 0) {
                        temp = new Coordinate(i, j);
                        if ((currDist = manhattan_dist(currPos, temp)) < minDist) {
                            System.out.println("&&&&&&&&&&&&&&&&&&&");
                            minDist = currDist;
                            bestChoice = temp;
//                            System.out.println(bestChoice);
                        }
                    }
                }
            }
            System.out.println("best >>>>>>> " + bestChoice);
            if(currPos.equals(bestChoice)) {
                return bestChoice;
            }
            return SearchStrategyFactory.getInstance().getStrategy(SearchAlgorithmType.Dijkstra).search(currPos, bestChoice, map).get(1);
        }
    }

    private int manhattan_dist(Coordinate c1, Coordinate c2) {
        return Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y);
    }


    @Override
    public void updateMap(Coordinate currPos) {}

    // return the positions next to the given point
    private ArrayList<Coordinate> getPosAround(Coordinate coor, HashMap<Coordinate, MapTile> map) {
        ArrayList<Coordinate> out = new ArrayList<>();
        out.add(new Coordinate(coor.x + Car.VIEW_SQUARE + 1, coor.y));
        out.add(new Coordinate(coor.x - Car.VIEW_SQUARE - 1, coor.y));
        out.add(new Coordinate(coor.x, coor.y + Car.VIEW_SQUARE + 1));
        out.add(new Coordinate(coor.x, coor.y - Car.VIEW_SQUARE - 1));
        // remove any invalid coors
        ListIterator<Coordinate> i = out.listIterator(0);
        while(i.hasNext()) {
            Coordinate temp = i.next();
            if(!isValidCoor(temp, coor, map)) {
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
        return 0 <= coor.x && coor.x < World.MAP_WIDTH &&
                0 <= coor.y && coor.y < World.MAP_HEIGHT &&
                !map.get(coor).isType(MapTile.Type.WALL) &&
                !hasWallBetween(coor, curr, map);
    }

    private boolean hasWallBetween(Coordinate c1, Coordinate c2, HashMap<Coordinate, MapTile> map) {
        iSearchStrategy searchStrategy = SearchStrategyFactory.getInstance().getStrategy(SearchAlgorithmType.Dijkstra);
        ArrayList<Coordinate> path = searchStrategy.search(c1, c2, map);
        return path == null || manhattan_dist(c1, c2) != path.size() - 1;
    }

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
