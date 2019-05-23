package mycontroller.strategies;

import utilities.Coordinate;

import java.util.ArrayList;

/**
 * Team: W9-5
 * Description:
 */
public class PickParcelStrategy implements iStrategy{

    // shortest path to the parcel
    private ArrayList<Coordinate> path;

    public PickParcelStrategy() {
        path = new ArrayList<>();
    }

    @Override
    public Coordinate getNextPosition(int fuel, Coordinate curr) {
        return path.remove(0);
    }

    // TODO decide detail implementation
    public boolean reachable(Integer[][] map, Coordinate coor, Coordinate parcel) {
        return true;
    }
}
