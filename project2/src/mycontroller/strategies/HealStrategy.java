package mycontroller.strategies;

import mycontroller.adapters.AdapterFactory;
import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Team: W9-5
 * Description:
 */
public class HealStrategy implements iControllerStrategy {

    private HashMap<TileType, Integer> pathCost;

    public HealStrategy(HashMap<TileType, Integer> pathCost) {
        this.pathCost = pathCost;
    }

    @Override
    public Coordinate getNextPosition(float fuel, Coordinate currPos, ArrayList<Coordinate> goal, HashMap<Coordinate, MapTile> map, int[][] seenWorld) {
        iSearchStrategy searchAlg = SearchStrategyFactory.getInstance().getStrategy(SearchAlgorithmType.Dijkstra);
        ArrayList<Coordinate> healths = getHealth(map);
        return searchAlg.search(currPos, healths, map, pathCost).get(1);
//        Coordinate closestHealth = healths.get(0);
//        int minDist = searchAlg.search(currPos, closestHealth, map, pathCost).size();
//        ArrayList<Coordinate> currPath;
//        for(Coordinate next: healths) {
//            currPath = searchAlg.search(currPos, next, map, pathCost);
//            if(currPath.size() < minDist) {
//                minDist = currPath.size();
//                closestHealth = currPath.get(1);
//            }
//        }
//        return closestHealth;
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
