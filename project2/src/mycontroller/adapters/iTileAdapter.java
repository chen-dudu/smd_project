package mycontroller.adapters;

import tiles.MapTile;

/**
 * Team: W9-5
 * Description: this is the common interface for all adapters
 */
public interface iTileAdapter {

    /**
     * return the type of the given tile
     * @param tile the tile whose type is to be returned
     * @return the type of the given type
     */
    TileType getType(MapTile tile);
}
