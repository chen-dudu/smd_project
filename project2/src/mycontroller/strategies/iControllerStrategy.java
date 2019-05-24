package mycontroller.strategies;

import utilities.Coordinate;

/**
 * Team: W9-5
 * Description: this is the common interface for all controller strategies
 */
public interface iControllerStrategy {

    Coordinate getNextPosition(int fuel, Coordinate curr);
}
