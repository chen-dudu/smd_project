package mycontroller.strategies;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Team: W9-5
 * Description: this is the common interface for all search algorithms
 */
public interface iSearchStrategy {

    // do the search
    void search(Coordinate start, Coordinate des, HashMap<Coordinate, MapTile> map);
    // return the total cost to get from start to des
    int getCost();
    // return the path found
    ArrayList<Coordinate> getPath();
}
