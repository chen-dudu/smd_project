package mycontroller.strategies;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.HashMap;

/**
 * Team: W9-5
 * Description: this is the common interface for all controller strategies
 */
public interface iControllerStrategy {

    /**
     * return the next position based on the current position and fuel left
     * @param fuel the fuel the car has
     * @param curr the current position of the car
     * @return the position the car will move to
     */
    Coordinate getNextPosition(float fuel, Coordinate curr, HashMap<Coordinate, MapTile> map, Integer[][] seenWorld);

    void updateMap(Coordinate currPos);
}
