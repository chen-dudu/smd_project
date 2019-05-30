package mycontroller.strategies;

import mycontroller.MyMap;
import utilities.Coordinate;

import java.util.ArrayList;

/**
 * Team: W9-5
 * Description: this is the common interface for all controller strategies
 */
public interface iControllerStrategy {

    /**
     * return the next position based on the current position
     * @param curr the coordinate current position of the car
     * @param des the coordinate(s) of the destination position(s)
     * @param map the map representing the world
     * @return the coordinate of the next position the car will move to
     */
    Coordinate getNextPosition(Coordinate curr, ArrayList<Coordinate> des, MyMap map);
}
