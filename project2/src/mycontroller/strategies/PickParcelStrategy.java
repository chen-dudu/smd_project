package mycontroller.strategies;

import mycontroller.CarState;
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
     * create a new parcel collecting strategy object
     * @param type the type of search algorithm to be used for searching
     * @param pathCost the path cost for different types of tiles
     */
    public PickParcelStrategy(SearchAlgorithmType type, HashMap<TileType, Integer> pathCost) {
        searchAlg = SearchStrategyFactory.getInstance().getStrategy(type);
        this.pathCost = pathCost;
    }

    @Override
    public Coordinate getNextPosition(CarState state, Coordinate curr, MyMap map) {
        return searchAlg.search(curr, map.getParcel(), map.getMap(), pathCost).get(1);
    }

    @Override
    public void addToStrategy(CarState state, iControllerStrategy strategy) {}

    @Override
    public boolean reachable(CarState state, Coordinate currPos, MyMap map) {
        return searchAlg.search(currPos, map.getParcel(), map.getMap(), pathCost) != null;
    }
}
