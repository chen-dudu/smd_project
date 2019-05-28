package mycontroller.strategies;

import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

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
    public Coordinate getNextPosition(float fuel, Coordinate curr, ArrayList<Coordinate> des,
                                      HashMap<Coordinate, MapTile> map, int[][] seenWorld) {
        return searchAlg.search(curr, des, map, pathCost).get(1);
    }

    /**
     * check if parcels are accessible
     * @param map the map representing the world
     * @param currPos the coordinate of current position
     * @param parcels coordinate(s) of parcel(s)
     * @return true if parcels are accessible, false otherwise
     */
    public boolean reachable(HashMap<Coordinate, MapTile> map, Coordinate currPos,
                             ArrayList<Coordinate> parcels) {
        return searchAlg.search(currPos, parcels, map, pathCost) != null;
    }
}
