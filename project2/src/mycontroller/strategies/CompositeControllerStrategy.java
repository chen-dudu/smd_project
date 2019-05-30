package mycontroller.strategies;

import mycontroller.MyMap;
import utilities.Coordinate;
import mycontroller.CarState;
import mycontroller.adapters.TileType;
import mycontroller.algorithms.SearchAlgorithmType;

import java.util.HashMap;

/**
 * Team: W9-5
 * Description: this is the super class for the two composite strategy classes
 */
public class CompositeControllerStrategy implements iControllerStrategy {

    // record the path cost for different types of tiles
    HashMap<TileType, Integer> costTable;

    // strategies included in this composite strategy
    private HashMap<CarState, iControllerStrategy> strategies;

    // type of search algorithm to be used for finding next position
    private SearchAlgorithmType algorithmType = SearchAlgorithmType.Dijkstra;

    /**
     * create a new composite controller strategy object
     */
    public CompositeControllerStrategy() {
        costTable = new HashMap<>();
        strategies = new HashMap<>();
    }

    @Override
    public Coordinate getNextPosition(CarState state, Coordinate curr, MyMap map) {
        if (!strategies.containsKey(state)) {
            iControllerStrategy newStrategy = ControllerStrategyFactory.getInstance().getStrategy(state, algorithmType, costTable);
            addToStrategy(state, newStrategy);
        }
        return strategies.get(state).getNextPosition(state, curr, map);
    }

    @Override
    public void addToStrategy(CarState state, iControllerStrategy strategy) {
        strategies.put(state, strategy);
    }

    @Override
    public boolean reachable(CarState state, Coordinate currPos, MyMap map) {
        if(!strategies.containsKey(state)) {
            iControllerStrategy newStrategy = ControllerStrategyFactory.getInstance().getStrategy(state, algorithmType, costTable);
            addToStrategy(state, newStrategy);
        }
        return strategies.get(state).reachable(state, currPos, map);
    }

    @Override
    public void updateTable(TileType type, Integer newValue) {
        costTable.put(type, newValue);
    }
}
