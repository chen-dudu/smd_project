package mycontroller.strategies;

import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;
import world.World;
import mycontroller.adapters.AdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Team: W9-5
 * Description:
 */
public class ExploreStrategy implements iControllerStrategy {

    // 0 for not explored, 1 for yes
    private Integer[][] exploreMap;

    public ExploreStrategy() {
        // init map
        exploreMap = new Integer[World.MAP_WIDTH][World.MAP_HEIGHT];
        for(int i = 0; i < exploreMap.length; i++) {
            for(int j = 0; j < exploreMap[i].length; j++) {
                exploreMap[i][j] = 0;
            }
        }
    }

    @Override
    public Coordinate getNextPosition(int fuel, Coordinate currPos) {
        ArrayList<Coordinate> nextPos = getPosAround(currPos);
        ArrayList<Coordinate> unseenPos = new ArrayList<>();
        for(Coordinate next: nextPos) {
            if(exploreMap[next.x][next.y] == 0) {
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
            return nextPos.get(rand.nextInt(nextPos.size()));
        }
    }

    // return the positions next to the given point
    private ArrayList<Coordinate> getPosAround(Coordinate coor) {
        ArrayList<Coordinate> out = new ArrayList<>();
        out.add(new Coordinate(coor.x + 1, coor.y));
        out.add(new Coordinate(coor.x - 1, coor.y));
        out.add(new Coordinate(coor.x, coor.y + 1));
        out.add(new Coordinate(coor.x, coor.y - 1));
        // remove any invalid coors
        for(Coordinate next: out) {
            if(!isValidCoor(next)) {
                out.remove(next);
            }
        }
        return out;
    }

    private boolean isValidCoor(Coordinate coor) {
        return 0 <= coor.x && coor.x < World.MAP_WIDTH &&
                0 <= coor.y && coor.y < World.MAP_HEIGHT;
    }

    // 1 for explored and 0 for not
    public boolean isExplored(Coordinate coor) {
        return exploreMap[coor.x][coor.y] == 1;
    }

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
//    public void updateMap(Coordinate currPos) {
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
