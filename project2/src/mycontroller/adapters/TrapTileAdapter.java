package mycontroller.adapters;

import tiles.MapTile;
import tiles.TrapTile;

/**
 * Team: W9-5
 * Description: this is the adapter for trap tile
 */
public class TrapTileAdapter implements iTileAdapter {

    @Override
    public TileType getType(MapTile tile) {
        TrapTile trapTile = (TrapTile) tile;
        switch (trapTile.getTrap()) {
            case "lava":
                return TileType.LAVA;
            case "water":
                return TileType.WATER;
            case "health":
                return TileType.HEALTH;
            case "parcel":
                return TileType.PARCEL;
        }
        return null;
    }
}
