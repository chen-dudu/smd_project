package mycontroller;

import mycontroller.adapters.AdapterFactory;
import mycontroller.adapters.TileType;
import tiles.*;
import utilities.Coordinate;
import world.World;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Team: W9-5
 * Description: this is the class representing the map of our autoController.
 *              It has different maps for different purposes.
 */
public class MyMap {

    /*
    record which tile has been visited, which has not
    0 for not visited, 1 for visited
     */
    private int[][] exploreMap;
    // the world the car has seen so far
    private HashMap<Coordinate, MapTile> myMap;
    // coordinates of all finish tiles
    private ArrayList<Coordinate> destinations;

    /**
     * create a new MyMap object using the given map
     * @param emptyMap the map used to initialise MyMap object
     */
    public MyMap(HashMap<Coordinate, MapTile> emptyMap) {
        exploreMap = new int[World.MAP_HEIGHT][World.MAP_WIDTH];
        myMap = new HashMap<>();
        destinations = new ArrayList<>();

        AdapterFactory factory = AdapterFactory.getInstance();
        for(Coordinate next: emptyMap.keySet()) {
            MapTile tile = emptyMap.get(next);
            if(factory.getAdapter(tile).getType(tile) == TileType.WALL) {
                myMap.put(next, new MapTile(MapTile.Type.WALL));
                // all walls are marked as visited
                exploreMap[next.y][next.x] = 1;
            }
            else if(factory.getAdapter(tile).getType(tile) == TileType.ROAD) {
                myMap.put(next, new MapTile(MapTile.Type.ROAD));
            }
            else if(factory.getAdapter(tile).getType(tile) == TileType.FINISH) {
                myMap.put(next, new MapTile(MapTile.Type.FINISH));
                destinations.add(next);
            }
            else if(factory.getAdapter(tile).getType(tile) == TileType.START) {
                myMap.put(next, new MapTile(MapTile.Type.START));
            }
        }
    }

    /**
     * this is the method called each time the car sees something new
     * @param view the world the car can see
     */
    public void updateMap(HashMap<Coordinate, MapTile> view) {
        AdapterFactory factory = AdapterFactory.getInstance();
        for(Coordinate next: view.keySet()) {
            if(onBoard(next)) {
                // everything the car can see is marked as visited
                exploreMap[next.y][next.x] = 1;
            }
            MapTile tile = view.get(next);
            // update the map recorded using new information
            switch (factory.getAdapter(tile).getType(tile)) {
                case WALL:
                    myMap.put(next, new MapTile(MapTile.Type.WALL));
                    break;
                case ROAD:
                    myMap.put(next, new MapTile(MapTile.Type.ROAD));
                    break;
                case START:
                    myMap.put(next, new MapTile(MapTile.Type.START));
                    break;
                case FINISH:
                    myMap.put(next, new MapTile(MapTile.Type.FINISH));
                    break;
                case LAVA:
                    myMap.put(next, new LavaTrap());
                    break;
                case WATER:
                    myMap.put(next, new WaterTrap());
                    break;
                case HEALTH:
                    myMap.put(next, new HealthTrap());
                    break;
                case PARCEL:
                    myMap.put(next, new ParcelTrap());
                    break;
            }
        }
    }

    /**
     * this method will print out the explore map, with visited tiles printed
     * as 1, and unvisited tiles printed as blank (used for debugging)
     */
    public void printMap() {
        for(int i = exploreMap.length - 1; i >= 0; i--) {
            for(int j = 0; j < exploreMap[i].length; j++) {
                if(exploreMap[i][j] == 1) {
                    System.out.print(exploreMap[i][j] + ", ");
                }
                else if(exploreMap[i][j] == 0) {
                    System.out.print(" , ");
                }
            }
            System.out.println("");
        }
    }

    /**
     * return a map showing the status(being visited or not) of all tiles in the map
     * @return a map showing tile status
     */
    public int[][] getExploreMap() { return exploreMap; }

    /**
     * return a map representing the world the car has seen so far
     * @return the map representing the world the car has seen so far
     */
    public HashMap<Coordinate, MapTile> getMap() { return myMap; }

    /**
     * return coordinates of all destinations
     * @return coordinates of all destinations
     */
    public ArrayList<Coordinate> getDes() { return destinations; }

    /**
     * return coordinates of all parcels in the world the car has seen
     * @return coordinates of all parcels
     */
    public ArrayList<Coordinate> getParcel() {
        ArrayList<Coordinate> out = new ArrayList<>();
        AdapterFactory factory = AdapterFactory.getInstance();
        for(Coordinate next: myMap.keySet()) {
            MapTile tile = myMap.get(next);
            if(factory.getAdapter(tile).getType(tile) == TileType.PARCEL) {
                out.add(next);
            }
        }
        return out;
    }

    // check if the given coordinate is within the border
    private boolean onBoard(Coordinate coor) {
        return 0 <= coor.x && coor.x < World.MAP_WIDTH &&
                0 <= coor.y && coor.y < World.MAP_HEIGHT;
    }
}
