package mycontroller.strategies;

import tiles.MapTile;
import utilities.Coordinate;
import world.World;

import java.util.HashMap;

/**
 * Team: W9-5
 * Description:
 */
public class ExploreStrategy implements iStrategy{

    private Integer[][] exploreMap;


    public ExploreStrategy() {
        exploreMap = new Integer[World.MAP_WIDTH][World.MAP_HEIGHT];
    }

    @Override
    public Coordinate getNextPosition(int fuel, Coordinate curr) {
        return null;
    }

    // 1 for explored and 0 for not
    public boolean isExplored(Coordinate coor) {
        return exploreMap[coor.x][coor.y] == 1;
    }

    // TODO decide detail implementation, input parameter
    public void updateMap() {
    }

    // TODO decide detail implementation
    public boolean canExit(int fuel, Coordinate coor) {
        return true;
    }

    // TODO decide detial implementation
    public boolean seeParcel(HashMap<Coordinate, MapTile> view) {
        return true;
    }

}
