package mycontroller.strategies;

import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Team: W9-5
 * Description: this is the singleton strategy factory class
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

    public iControllerStrategy getStrategy(CarState carState, HashMap<Coordinate, MapTile> map,
                                           SearchAlgorithmType type, ArrayList<Coordinate> goals,
                                           HashMap<TileType, Integer> pathCost) {
        switch (carState) {
            case EXPLORING:
                return new ExploreStrategy(pathCost);
            case EXITING:
                return new ExitStrategy(map, type, goals, pathCost);
            case COLLECTING:
                return new PickParcelStrategy(type, pathCost);
            case HEALING:
                return new HealStrategy(pathCost);
        }
        return null;
    }

}
