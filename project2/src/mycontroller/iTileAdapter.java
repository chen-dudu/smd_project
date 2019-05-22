package mycontroller;

import tiles.MapTile;

/**
 * Team: W9-5
 * Description: this is the common interface for all adapters
 */
public interface iTileAdapter {

    TileType getType(MapTile tile);
}
