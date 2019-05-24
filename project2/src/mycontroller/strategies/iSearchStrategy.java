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

    /**
     * ask search algorithm to do a search
     * @param start the position of the starting point
     * @param des the position of the destination
     * @param map the map representing the world
     */
    void search(Coordinate start, Coordinate des, HashMap<Coordinate, MapTile> map);

    /**
     * return the cost to destination
     * @return cost to destination
     */
    int getCost();

    /**
     * return the path to the destination found by the algorithm
     * @returnn path found to destination
     */
    ArrayList<Coordinate> getPath();
}
