package mycontroller;

import mycontroller.adapters.AdapterFactory;
import mycontroller.adapters.TileType;
import tiles.*;
import utilities.Coordinate;
import world.World;

import java.util.HashMap;

/**
 * Team: W9-5
 * Description:
 */
public class MyMap {

    // 0 for not visited, 1 for yes
    private int[][] visited;
    // the world the car has seen so far
    private HashMap<Coordinate, MapTile> myMap;

    public MyMap(HashMap<Coordinate, MapTile> emptyMap) {
        visited = new int[World.MAP_HEIGHT][World.MAP_WIDTH];
        myMap = new HashMap<>();
        for(Coordinate next: emptyMap.keySet()) {
            MapTile tile = emptyMap.get(next);
            if(AdapterFactory.getInstance().getAdapter(tile).getType(tile) == TileType.WALL) {
                myMap.put(next, new MapTile(MapTile.Type.WALL));
                visited[next.y][next.x] = 1;
            }
            else if(AdapterFactory.getInstance().getAdapter(tile).getType(tile) == TileType.ROAD) {
                myMap.put(next, new MapTile(MapTile.Type.ROAD));
            }
        }
    }

    // update both visited map and myMap
    public void updateMap(HashMap<Coordinate, MapTile> view) {
        for(Coordinate next: view.keySet()) {
            visited[next.y][next.x] = 1;
            MapTile tile = view.get(next);
            // already in the map
            if(myMap.get(next) != null) {
                continue;
            }
            switch (AdapterFactory.getInstance().getAdapter(tile).getType(tile)) {
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

    public void printMap() {
//		for(int i = map.length - 1; i >= 0; i--) {
//			System.out.println(Arrays.toString(map[i]));
//		}
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
}
