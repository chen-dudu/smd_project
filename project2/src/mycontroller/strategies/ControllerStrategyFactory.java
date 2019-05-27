package mycontroller.strategies;

import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

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
     * @param map the map representing the world
     * @param type type of search algorithm to use
     * @param goals the destinations for search algorithm
     * @param pathCost the path cost for different types of tiles
     * @return a controller strategy with specified type
     */
    public iControllerStrategy getStrategy(CarState carState, HashMap<Coordinate, MapTile> map,
                                           SearchAlgorithmType type, ArrayList<Coordinate> goals,
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
