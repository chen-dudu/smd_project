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

    public HealStrategy() {}

    @Override
    public Coordinate getNextPosition(float fuel, Coordinate currPos, Coordinate goal, HashMap<Coordinate, MapTile> map, Integer[][] seenWorld) {
        iSearchStrategy searchAlg = SearchStrategyFactory.getInstance().getStrategy(SearchAlgorithmType.Dijkstra);
        ArrayList<Coordinate> healths = getHealth(map);
        Coordinate closestHealth = healths.get(0);
        int minDist = searchAlg.search(currPos, closestHealth, map).size();
        ArrayList<Coordinate> currPath;
        for(Coordinate next: healths) {
            currPath = searchAlg.search(currPos, next, map);
            if(currPath.size() < minDist) {
                minDist = currPath.size();
                closestHealth = currPath.get(1);
            }
        }
        return closestHealth;
    }

    @Override
    public void updateMap(Coordinate currPos) {}

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
