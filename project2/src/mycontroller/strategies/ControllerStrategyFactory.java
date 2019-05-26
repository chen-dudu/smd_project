package mycontroller.strategies;

import tiles.MapTile;
import utilities.Coordinate;

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

    public iControllerStrategy getStrategy(CarState carState, HashMap<Coordinate, MapTile> map, SearchAlgorithmType type, Coordinate finish) {
        switch (carState) {
            case EXPLORING:
                return new ExploreStrategy();
            case EXITING:
                return new ExitStrategy(map, type, finish);
            case COLLECTING:
                return new PickParcelStrategy(type, finish);
            case HEALING:
                return new HealStrategy();
        }
        return null;
    }

}
