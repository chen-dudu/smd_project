package mycontroller.strategies;

import tiles.MapTile;
import utilities.Coordinate;
import world.World;

import java.util.*;

/**
 * Team: W9-5
 * Description:
 */
public class ExitStrategy implements iControllerStrategy {

    private HashMap<Coordinate, ArrayList<Coordinate>> exitMap;
    private SearchStrategyFactory factory;
    private iSearchStrategy strategy;

    public ExitStrategy(HashMap<Coordinate, MapTile> map, SearchAlgorithmType type, Coordinate finish) {
        exitMap = new HashMap<> ();

        // TODO initilase exit map using search algorithm
        factory = SearchStrategyFactory.getInstance();
        strategy = factory.getStrategy(type);
        for(Coordinate coor: map.keySet()) {
            if (!map.get(coor).isType(MapTile.Type.WALL)) {
                exitMap.put(coor, strategy.search(coor, finish, map));
            } else {
                exitMap.put(coor, null);
            }
        }

    }

    @Override
    public Coordinate getNextPosition(float fuel, Coordinate curr, HashMap<Coordinate, MapTile> map, Integer[][] seenWorld) {
        return exitMap.get(curr).remove(0);
    }

    @Override
    public void updateMap(Coordinate currPos) {

    }

    // lookup the array, and return the dist to des
    public int getDistance(Coordinate coor) {
        return exitMap.get(coor).size();
    }
}
