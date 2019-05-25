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


    public PickParcelStrategy(SearchAlgorithmType type, Coordinate parcel) {
        path = new ArrayList<>();
        factory = SearchStrategyFactory.getInstance();
        strategy = factory.getStrategy(type);
    }

    @Override
    public Coordinate getNextPosition(float fuel, Coordinate curr, Coordinate des, HashMap<Coordinate, MapTile> map, Integer[][] seenWorld) {
        path = strategy.search(curr, des, map);
        return path.remove(1);
    }

    @Override
    public void updateMap(Coordinate currPos) {

    }


    // TODO decide detail implementation
    public boolean reachable(HashMap<Coordinate, MapTile> map, Coordinate coor, Coordinate parcel) {
        path = strategy.search(coor, parcel, map);
        if (path == null) {
            return false;
        } else {
            return true;
        }
    }
}
