package mycontroller.strategies;

import mycontroller.MyMap;
import utilities.Coordinate;
import mycontroller.algorithms.*;
import mycontroller.adapters.TileType;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Team: W9-5
 * Description: this is the class representing the parcel
 *              collecting strategy used by our autoController
 */
public class PickParcelStrategy implements iControllerStrategy {

    /*
    search algorithm that will be used for finding
    the best path leading to a parcel
     */
    private iSearchStrategy searchAlg;
    // the path cost for different types of tiles
    private HashMap<TileType, Integer> pathCost;

    /**
     * create a new parcel collecting strategy
     * @param type the type of search algorithm to be used for searching
     * @param pathCost the path cost for different types of tiles
     */
    public PickParcelStrategy(SearchAlgorithmType type, HashMap<TileType, Integer> pathCost) {
        searchAlg = SearchStrategyFactory.getInstance().getStrategy(type);
        this.pathCost = pathCost;
    }

    @Override
    public Coordinate getNextPosition(Coordinate curr, ArrayList<Coordinate> des, MyMap map) {
        return searchAlg.search(curr, des, map.getMap(), pathCost).get(1);
    }

    /**
     * check if parcels are accessible
     * @param map the map representing the world
     * @param currPos the coordinate of current position
     * @param parcels coordinate(s) of parcel(s)
     * @return true if parcels are accessible, false otherwise
     */
    public boolean reachable(MyMap map, Coordinate currPos,
                             ArrayList<Coordinate> parcels) {
        return searchAlg.search(currPos, parcels, map.getMap(), pathCost) != null;
    }
}
