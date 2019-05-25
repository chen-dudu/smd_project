package mycontroller.strategies;

import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;
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
    public Coordinate getNextPosition(float fuel, Coordinate currPos, HashMap<Coordinate, MapTile> map, Integer[][] seenWorld) {
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
            return nextPos.get(rand.nextInt(nextPos.size()));
        }
    }

    @Override
    public void updateMap(Coordinate currPos) {

    }

    // return the positions next to the given point
    private ArrayList<Coordinate> getPosAround(Coordinate coor, HashMap<Coordinate, MapTile> map) {
        ArrayList<Coordinate> out = new ArrayList<>();
        out.add(new Coordinate(coor.x + 2, coor.y));
        out.add(new Coordinate(coor.x - 2, coor.y));
        out.add(new Coordinate(coor.x, coor.y + 2));
        out.add(new Coordinate(coor.x, coor.y - 2));
        // remove any invalid coors
        ListIterator<Coordinate> i = out.listIterator(0);
        while(i.hasNext()) {
            Coordinate temp = i.next();
            if(!isValidCoor(temp, map)) {
                i.remove();
            }
        }
        return out;
    }

    private boolean isValidCoor(Coordinate coor, HashMap<Coordinate, MapTile> map) {
        return 0 <= coor.x && coor.x < World.MAP_WIDTH &&
                0 <= coor.y && coor.y < World.MAP_HEIGHT &&
                !map.get(coor).isType(MapTile.Type.WALL);
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
