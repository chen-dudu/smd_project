package mycontroller.strategies;

import mycontroller.CarState;
import mycontroller.MyMap;
import mycontroller.adapters.TileType;
import mycontroller.algorithms.SearchAlgorithmType;
import utilities.Coordinate;
import world.Car;

import java.util.HashMap;

/**
 * Team: W9-5
 * Description: this is the class representing the health strategy used by
 *              our autoController
 */
public class HealthStrategy implements iControllerStrategy {

    // record the path cost for different types of tiles
    private HashMap<TileType, Integer> costTable;

    // strategies included in this composite strategy
    private HashMap<CarState, iControllerStrategy> strategies;

    // type of search algorithm to be used for finding next position
    private SearchAlgorithmType algorithmType = SearchAlgorithmType.Dijkstra;

    /**
     * create a new health strategy object
     */
    public HealthStrategy() {
        costTable = new HashMap<>();
        strategies = new HashMap<>();
        costTable.put(TileType.LAVA, 99);
        costTable.put(TileType.ROAD, 1);
        // take health only when it is necessary, so high cost
        costTable.put(TileType.WATER, 999);
        costTable.put(TileType.HEALTH, 999);
    }

    @Override
    public Coordinate getNextPosition(CarState state, Coordinate curr, MyMap map) {
        if(!strategies.containsKey(state)) {
            iControllerStrategy newStrategy = ControllerStrategyFactory.getInstance().getStrategy(state, algorithmType, costTable);
            addToStrategy(state, newStrategy);
        }
        return strategies.get(state).getNextPosition(state, curr, map);
    }

    @Override
    public void addToStrategy(CarState state, iControllerStrategy newStrategy) {
        strategies.put(state, newStrategy);
    }

    @Override
    public boolean reachable(CarState state, Coordinate currPos, MyMap map) {
        if(!strategies.containsKey(state)) {
            iControllerStrategy newStrategy = ControllerStrategyFactory.getInstance().getStrategy(state, algorithmType, costTable);
            addToStrategy(state, newStrategy);
        }
        return strategies.get(state).reachable(state, currPos, map);
    }
}
