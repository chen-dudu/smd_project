package mycontroller.strategies;

import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;
import mycontroller.adapters.AdapterFactory;
import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.*;

/**
 * Team: W9-5
 * Description:
 */
public class ExitStrategy implements iControllerStrategy {

//    private HashMap<Coordinate, ArrayList<Coordinate>> exitMap;

    private HashMap<TileType, Integer> pathCost;

    public ExitStrategy(HashMap<Coordinate, MapTile> map, SearchAlgorithmType type,
                        ArrayList<Coordinate> finish, HashMap<TileType, Integer> pathCost) {
        this.pathCost = pathCost;
//        exitMap = new HashMap<> ();
//        iSearchStrategy strategy = SearchStrategyFactory.getInstance().getStrategy(type);

//        AdapterFactory factory = AdapterFactory.getInstance();

//        for(Coordinate next: finish) {
//            System.out.println(next);
//        }

        System.out.println(" ?? " + finish.size() + " ??");

//        for(Coordinate next: map.keySet()) {
//            MapTile tile = map.get(next);
//            if(factory.getAdapter(tile).getType(tile) != TileType.WALL) {
////            if (!map.get(coor).isType(MapTile.Type.WALL)) {
//                exitMap.put(next, strategy.search(next, finish, map, pathCost));
////                if(exitMap.get(next) == null) {
////                    System.out.println("::::::::");
////                }
//            } else {
//                exitMap.put(next, null);
//            }
//        }
    }

    @Override
    public Coordinate getNextPosition(float fuel, Coordinate curr, ArrayList<Coordinate> des,
                                      HashMap<Coordinate, MapTile> map, int[][] seenWorld) {
        iSearchStrategy searchAlg = SearchStrategyFactory.getInstance().getStrategy(SearchAlgorithmType.Dijkstra);
        return searchAlg.search(curr, des, map, pathCost).get(1);
//        if(exitMap.get(curr) == null) {
//            System.out.println("NULL >>>>>>>");
//        }
//        return exitMap.get(curr).get(1);
    }

    // lookup the array, and return the dist to des
//    public int getDistance(Coordinate coor) {
//        return exitMap.get(coor).size();
//    }
}
