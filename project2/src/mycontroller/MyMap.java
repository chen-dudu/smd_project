package mycontroller;

import mycontroller.adapters.AdapterFactory;
import mycontroller.adapters.TileType;
import tiles.*;
import utilities.Coordinate;
import world.World;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Team: W9-5
 * Description:
 */
public class MyMap {

    // 0 for not visited, 1 for yes
    private int[][] visited;
    // the world the car has seen so far
    private HashMap<Coordinate, MapTile> myMap;
    //
    private ArrayList<Coordinate> destinations;
    //
    private ArrayList<Coordinate> parcels;

    public MyMap(HashMap<Coordinate, MapTile> emptyMap) {
        visited = new int[World.MAP_HEIGHT][World.MAP_WIDTH];
        myMap = new HashMap<>();
        destinations = new ArrayList<>();
        parcels = new ArrayList<>();

        AdapterFactory factory = AdapterFactory.getInstance();
        for(Coordinate next: emptyMap.keySet()) {
            MapTile tile = emptyMap.get(next);
            if(factory.getAdapter(tile).getType(tile) == TileType.WALL) {
                myMap.put(next, new MapTile(MapTile.Type.WALL));
                visited[next.y][next.x] = 1;
            }
            else if(factory.getAdapter(tile).getType(tile) == TileType.ROAD) {
                myMap.put(next, new MapTile(MapTile.Type.ROAD));
            }
            else if(factory.getAdapter(tile).getType(tile) == TileType.FINISH) {
                myMap.put(next, new MapTile(MapTile.Type.FINISH));
                destinations.add(next);
                System.out.println("%%%%%%%%%%");
            }
            else if(factory.getAdapter(tile).getType(tile) == TileType.START) {
                myMap.put(next, new MapTile(MapTile.Type.START));
            }
            // starting point not included
//            System.out.println("%%% " + destinations.size());
        }
//        printMap();
//        for(Coordinate next: myMap.keySet()) {
//            MapTile tile = myMap.get(next);
//            if(AdapterFactory.getInstance().getAdapter(tile).getType(tile) == TileType.WALL && onBoard(next)) {
//                visited[next.y][next.x] = 1;
//            }
//        }
//        printMap();
    }

    // update both visited map and myMap
    public void updateMap(HashMap<Coordinate, MapTile> view) {
        AdapterFactory factory = AdapterFactory.getInstance();
        for(Coordinate next: view.keySet()) {
            if(onBoard(next)) {
                visited[next.y][next.x] = 1;
            }
            MapTile tile = view.get(next);
            // already in the map
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
                    System.out.println("####");
                    myMap.put(next, new ParcelTrap());
                    break;
            }
        }
    }

    public void printMap() {
        for(int i = visited.length - 1; i >= 0; i--) {
            for(int j = 0; j < visited[i].length; j++) {
                if(visited[i][j] == 1) {
                    System.out.print(visited[i][j] + ", ");
                }
                else if(visited[i][j] == 0) {
                    System.out.print(" , ");
                }
            }
            System.out.println("");
        }
    }

    public int[][] getVisited() { return visited; }

    public HashMap<Coordinate, MapTile> getMap() { return myMap; }

    public ArrayList<Coordinate> getDes() { return destinations; }

    public ArrayList<Coordinate> getParcel() {
        ArrayList<Coordinate> out = new ArrayList<>();
        AdapterFactory factory = AdapterFactory.getInstance();
        for(Coordinate next: myMap.keySet()) {
            MapTile tile = myMap.get(next);
            if(factory.getAdapter(tile).getType(tile) == TileType.PARCEL) {
                System.out.println("~~~~~");
                out.add(next);
            }
        }
        return out;
    }

    private boolean onBoard(Coordinate coor) {
        return 0 <= coor.x && coor.x < World.MAP_WIDTH &&
                0 <= coor.y && coor.y < World.MAP_HEIGHT;
    }
}
