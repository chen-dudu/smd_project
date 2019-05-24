package mycontroller.strategies;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Team: W9-5
 * Description:
 */
public class Dijkstra implements iSearchStrategy {

    @Override
    public void search(Coordinate start, Coordinate des, HashMap<Coordinate, MapTile> map) {

    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public ArrayList<Coordinate> getPath() {
        return null;
    }
}
