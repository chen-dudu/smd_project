package mycontroller.strategies;

import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Team: W9-5
 * Description: this is the common interface for all search algorithms
 */
public interface iSearchStrategy {

    /**
     * ask search algorithm to do a search
     * @param start the position of the starting point
     * @param des the position of the destination
     * @param map the map representing the world
     */
    ArrayList<Coordinate> search(Coordinate start, ArrayList<Coordinate> des,
                                 HashMap<Coordinate, MapTile> map,
                                 HashMap<TileType, Integer> pathCost);
}
