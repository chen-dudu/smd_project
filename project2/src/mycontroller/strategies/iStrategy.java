package mycontroller.strategies;

import utilities.Coordinate;
import world.World;

/**
 * Team: W9-5
 * Description: this is the common interface for all strategies
 */
public interface iStrategy {
    Integer[][] map = new Integer[World.MAP_WIDTH][World.MAP_HEIGHT];

    Coordinate getNextPosition(int fuel, Coordinate curr);
}
