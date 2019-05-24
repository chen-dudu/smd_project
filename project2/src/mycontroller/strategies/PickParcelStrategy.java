package mycontroller.strategies;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Team: W9-5
 * Description:
 */
public class PickParcelStrategy implements iControllerStrategy {

    // shortest path to the parcel
    private ArrayList<Coordinate> path;
    private iSearchStrategy strategy;
    private SearchStrategyFactory factory;

    public PickParcelStrategy(SearchAlgorithmType type) {
        path = new ArrayList<>();
        factory = SearchStrategyFactory.getInstance();
        strategy = factory.getStrategy(type);
    }

    @Override
    public Coordinate getNextPosition(int fuel, Coordinate curr) {
        return path.remove(0);
    }

    // TODO decide detail implementation
    public boolean reachable(HashMap<Coordinate, MapTile> map, Coordinate coor, Coordinate parcel) {
        strategy.search(coor, parcel, map);
        path = strategy.getPath();
        if (path == null) {
            return false;
        } else {
            return true;
        }
    }
}
