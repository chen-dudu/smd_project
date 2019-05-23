package mycontroller.adapters;

import tiles.MapTile;

/**
 * Team: W9-5
 * Description: this is the adapter for map tile
 */
public class MapTileAdapter implements iTileAdapter{

    @Override
    public TileType getType(MapTile tile) {
        switch (tile.getType()) {
            case ROAD:
                return TileType.ROAD;
            case WALL:
                return TileType.WALL;
            case EMPTY:
                return TileType.EMPTY;
            case START:
                return TileType.START;
            case FINISH:
                return TileType.FINISH;
        }
        return null;
    }
}
