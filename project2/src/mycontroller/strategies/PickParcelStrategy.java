package mycontroller.strategies;

import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Team: W9-5
 * Description:
 */
public class PickParcelStrategy implements iControllerStrategy {

    private iSearchStrategy strategy;
    private ArrayList<Coordinate> des;
    private HashMap<TileType, Integer> pathCost;

    public PickParcelStrategy(SearchAlgorithmType type, HashMap<TileType, Integer> pathCost) {
        strategy = SearchStrategyFactory.getInstance().getStrategy(type);
        this.pathCost = pathCost;
    }

    @Override
    public Coordinate getNextPosition(float fuel, Coordinate curr, ArrayList<Coordinate> des,
                                      HashMap<Coordinate, MapTile> map, int[][] seenWorld) {
        return strategy.search(curr, des, map, pathCost).get(1);
    }

    public boolean reachable(HashMap<Coordinate, MapTile> map, Coordinate currPos,
                             ArrayList<Coordinate> parcels) {
        return strategy.search(currPos, parcels, map, pathCost) != null;
    }
}
