package mycontroller.strategies;

import mycontroller.MyMap;
import utilities.Coordinate;
import mycontroller.CarState;

/**
 * Team: W9-5
 * Description: this is the common interface for all controller strategies
 */
public interface iControllerStrategy {

    /**
     * return the next position based on the current position
     * @param curr the coordinate current position of the car
     * @param map the map representing the world
     * @return the coordinate of the next position the car will move to
     */
    Coordinate getNextPosition(CarState state, Coordinate curr, MyMap map);

    /**
     * add a strategy compatible with the specified car state to the composite strategy
     * @param state the state of the car
     * @param strategy strategy to be added
     */
    void addToStrategy(CarState state, iControllerStrategy strategy);

    /**
     * check if parcels can be accessed
     * @param state the state of the car
     * @param currPos the coordinate of the current position
     * @param map the map representing the world
     * @return true if parcels are accessible, false otherwise
     */
    boolean reachable(CarState state, Coordinate currPos, MyMap map);
}
