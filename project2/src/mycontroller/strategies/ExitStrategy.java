package mycontroller.strategies;

import utilities.Coordinate;
import world.World;

/**
 * Team: W9-5
 * Description:
 */
public class ExitStrategy implements iControllerStrategy {

    private Integer[][] exitMap;

    public ExitStrategy() {
        exitMap = new Integer[World.MAP_WIDTH][World.MAP_HEIGHT];
        // TODO initilase exit map using search algorithm
    }

    @Override
    public Coordinate getNextPosition(int fuel, Coordinate curr) {
        return null;
    }

    // lookup the array, and return the dist to des
    public int getDistance(Coordinate coor) {
        return exitMap[coor.x][coor.y];
    }
}
