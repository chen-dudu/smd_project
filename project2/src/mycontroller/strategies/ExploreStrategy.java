package mycontroller.strategies;

import world.Car;
import world.World;
import tiles.MapTile;
import mycontroller.MyMap;
import utilities.Coordinate;
import mycontroller.CarState;
import mycontroller.algorithms.*;
import mycontroller.adapters.TileType;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Team: W9-5
 * Description: this is the class representing the explore strategy used by our autoController
 */
public class ExploreStrategy implements iControllerStrategy {

    /*
    search algorithm that will be used for finding
    the best path leading to a parcel
     */
    private iSearchStrategy searchAlg;
    // the path cost for different types of tiles
    private HashMap<TileType, Integer> pathCost;

    /**
     * create a new explore strategy object
     * @param type the type of search algorithm to be used for searching
     * @param pathCost the path cost for different types of tiles
     */
    public ExploreStrategy(SearchAlgorithmType type, HashMap<TileType, Integer> pathCost) {
        searchAlg = SearchStrategyFactory.getInstance().getStrategy(type);
        this.pathCost = pathCost;
    }

    @Override
    public Coordinate getNextPosition(CarState state, Coordinate currPos, MyMap map) {
        ArrayList<Coordinate> nextPos = getPosAround(currPos, map.getMap());
        ArrayList<Coordinate> unseenPos = new ArrayList<>();
        for(Coordinate next: nextPos) {
            if(map.getExploreMap()[next.y][next.x] == 0) {
                unseenPos.add(next);
            }
        }
        if(unseenPos.size() > 0) {
            // for all unseen points, choose the closest one
            return searchAlg.search(currPos, unseenPos, map.getMap(), pathCost).get(1);
        }
        else {
            // all pos around seen, return the closest unseen pos (may be far away)
            ArrayList<Coordinate> candidate = new ArrayList<>();
            int[][] seenWorld = map.getExploreMap();
            for (int i = 0; i < seenWorld.length; i++) {
                for (int j = 0; j < seenWorld[i].length; j++) {
                    if (seenWorld[i][j] == 0) {
                        candidate.add(new Coordinate(j, i));
                    }
                }
            }
            // all world is explored
            if(candidate.size() == 0) {
                return currPos;
            }
            return searchAlg.search(currPos, candidate, map.getMap(), pathCost).get(1);
        }
    }

    @Override
    public void addToStrategy(CarState state, iControllerStrategy strategy) {}

    @Override
    public boolean reachable(CarState state, Coordinate currPos, MyMap map) { return true; }

    @Override
    public void updateTable(TileType type, Integer newValue) {}

    // return the positions next to the given point
    private ArrayList<Coordinate> getPosAround(Coordinate currPos, HashMap<Coordinate, MapTile> map) {

        ArrayList<Coordinate> out = new ArrayList<>();
        out.add(new Coordinate(currPos.x + Car.VIEW_SQUARE + 1, currPos.y));
        out.add(new Coordinate(currPos.x - Car.VIEW_SQUARE - 1, currPos.y));
        out.add(new Coordinate(currPos.x, currPos.y + Car.VIEW_SQUARE + 1));
        out.add(new Coordinate(currPos.x, currPos.y - Car.VIEW_SQUARE - 1));
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

    // check if the given coordinate is valid for being returned as the next position
    private boolean isValidCoor(Coordinate coor, HashMap<Coordinate, MapTile> map) {
        return onBoard(coor) && !map.get(coor).isType(MapTile.Type.WALL);
    }

    // check if the given coordinate is within the border
    private boolean onBoard(Coordinate coor) {
        return 0 <= coor.x && coor.x < World.MAP_WIDTH &&
                0 <= coor.y && coor.y < World.MAP_HEIGHT;
    }
}
