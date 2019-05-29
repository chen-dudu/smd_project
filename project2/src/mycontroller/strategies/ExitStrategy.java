package mycontroller.strategies;

import tiles.MapTile;
import utilities.Coordinate;
import mycontroller.algorithms.*;
import mycontroller.adapters.TileType;

import java.util.*;

/**
 * Team: W9-5
 * Description: this is the class representing the exit strategy used by our autoController
 */
public class ExitStrategy implements iControllerStrategy {

    /*
    search algorithm that will be used for finding
    the best path leading to a parcel
     */
    private iSearchStrategy searchAlg;
    // the path cost for different types of tiles
    private HashMap<TileType, Integer> pathCost;

    /**
     * create a new exit strategy
     * @param type the type of search algorithm to be used for searching
     * @param pathCost the path cost for different types of tiles
     */
    public ExitStrategy(SearchAlgorithmType type, HashMap<TileType, Integer> pathCost) {
        searchAlg = SearchStrategyFactory.getInstance().getStrategy(type);
        this.pathCost = pathCost;
    }

    @Override
    public Coordinate getNextPosition(Coordinate curr, ArrayList<Coordinate> des,
                                      HashMap<Coordinate, MapTile> map, int[][] seenWorld) {
        return searchAlg.search(curr, des, map, pathCost).get(1);
    }
}
