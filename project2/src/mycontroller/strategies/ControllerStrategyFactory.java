package mycontroller.strategies;

import tiles.MapTile;
import utilities.Coordinate;
import mycontroller.CarState;
import mycontroller.adapters.TileType;
import mycontroller.algorithms.SearchAlgorithmType;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Team: W9-5
 * Description: this is the singleton controller strategy factory class
 */
public class ControllerStrategyFactory {

    private static ControllerStrategyFactory factory = null;

    private ControllerStrategyFactory() {}

    /**
     * return an instance of the factory, create one if none exists
     * @return an instance of the factory
     */
    public static ControllerStrategyFactory getInstance() {
        if(factory == null) {
            factory = new ControllerStrategyFactory();
        }
        return factory;
    }

    /**
     * return a controller strategy with the specified type
     * @param carState the type of controller strategy to be returned
     * @param type type of search algorithm to use
     * @param pathCost the path cost for different types of tiles
     * @return a controller strategy with specified type
     */
    public iControllerStrategy getStrategy(CarState carState, SearchAlgorithmType type,
                                           HashMap<TileType, Integer> pathCost) {
        switch (carState) {
            case EXPLORING:
                return new ExploreStrategy(type, pathCost);
            case EXITING:
                return new ExitStrategy(type, pathCost);
            case COLLECTING:
                return new PickParcelStrategy(type, pathCost);
            case HEALING:
                return new HealStrategy(type, pathCost);
        }
        return null;
    }
}
