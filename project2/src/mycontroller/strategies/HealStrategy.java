package mycontroller.strategies;

import tiles.MapTile;
import mycontroller.MyMap;
import utilities.Coordinate;
import mycontroller.CarState;
import mycontroller.algorithms.*;
import mycontroller.adapters.TileType;
import mycontroller.adapters.AdapterFactory;

import java.util.HashMap;
import java.util.ArrayList;

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
     * create a new healing strategy object
     * @param type the type of search algorithm to be used for searching
     * @param pathCost the path cost for different types of tiles
     */
    public HealStrategy(SearchAlgorithmType type, HashMap<TileType, Integer> pathCost) {
        searchAlg = SearchStrategyFactory.getInstance().getStrategy(type);
        this.pathCost = pathCost;
    }

    @Override
    public Coordinate getNextPosition(CarState state, Coordinate currPos, MyMap map) {
        ArrayList<Coordinate> healths = getHealth(map.getMap());
        return searchAlg.search(currPos, healths, map.getMap(), pathCost).get(1);
    }

    @Override
    public void addToStrategy(CarState state, iControllerStrategy strategy) {}

    @Override
    public boolean reachable(CarState state, Coordinate currPos, MyMap map) { return true; }

    @Override
    public void updateTable(TileType type, Integer newValue) {}

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
