package mycontroller.strategies;

import mycontroller.MyMap;
import utilities.Coordinate;

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
    Coordinate getNextPosition(Coordinate curr, MyMap map);
}
