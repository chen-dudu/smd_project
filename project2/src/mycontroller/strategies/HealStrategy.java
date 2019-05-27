package mycontroller.strategies;

import mycontroller.adapters.AdapterFactory;
import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Team: W9-5
 * Description: this is the class representing the healing strategy used by our autoController
 */
public class HealStrategy implements iControllerStrategy {

    /*
    search algorithm that will be used for finding
    the best path leading to a parcel
     */
    private iSearchStrategy searchAlg;
    // the path cost for different types of tiles
    private HashMap<TileType, Integer> pathCost;

    /**
     * create a new healing strategy
     * @param type the type of search algorithm to be used for searching
     * @param pathCost the path cost for different types of tiles
     */
    public HealStrategy(SearchAlgorithmType type, HashMap<TileType, Integer> pathCost) {
        searchAlg = SearchStrategyFactory.getInstance().getStrategy(type);
        this.pathCost = pathCost;
    }

    @Override
    public Coordinate getNextPosition(float fuel, Coordinate currPos, ArrayList<Coordinate> goal, HashMap<Coordinate, MapTile> map, int[][] seenWorld) {
        ArrayList<Coordinate> healths = getHealth(map);
        return searchAlg.search(currPos, healths, map, pathCost).get(1);
    }

    // find all health tile in the explored map
    private ArrayList<Coordinate> getHealth(HashMap<Coordinate, MapTile> map) {
        ArrayList<Coordinate> healthCoor = new ArrayList<>();
        for(Coordinate next: map.keySet()) {
            MapTile tile = map.get(next);
            if(AdapterFactory.getInstance().getAdapter(tile).getType(tile) == TileType.HEALTH ||
                    AdapterFactory.getInstance().getAdapter(tile).getType(tile) == TileType.WATER) {
                healthCoor.add(next);
            }
        }
        return healthCoor;
    }
}
