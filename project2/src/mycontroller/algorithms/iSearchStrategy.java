package mycontroller.algorithms;

import tiles.MapTile;
import utilities.Coordinate;
import mycontroller.adapters.TileType;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Team: W9-5
 * Description: this is the common interface for all search algorithms
 */
public interface iSearchStrategy {

    /**
     * ask search algorithm to do a search
     * @param start the position of the starting point
     * @param des the positions of the destination
     * @param map the map representing the world
     * @param pathCost a hashMap storing path cost for different types of tiles
     * @return the shortest path
     */
    ArrayList<Coordinate> search(Coordinate start, ArrayList<Coordinate> des,
                                 HashMap<Coordinate, MapTile> map,
                                 HashMap<TileType, Integer> pathCost);
}
