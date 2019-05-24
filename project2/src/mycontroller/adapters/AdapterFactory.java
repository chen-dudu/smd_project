package mycontroller.adapters;

import tiles.MapTile;

/**
 * Team: W9-5
 * Description: this is the singleton adapter factory class
 */
public class AdapterFactory {

    private static AdapterFactory factory = null;

    private AdapterFactory() {}

    /**
     * return an instance of the factory, create one if none exists
     * @return an instance of the factory
     */
    public static AdapterFactory getInstance() {
        if(factory == null) {
            factory = new AdapterFactory();
        }
        return factory;
    }

    /**
     * return an adapter according to the tile given
     * @param tile the tile for which the adapter is used
     * @return an adapter compatible with the given tile
     */
    public iTileAdapter getAdapter(MapTile tile) {
        if(tile.getType() == MapTile.Type.TRAP) {
            return new TrapTileAdapter();
        }
        return new MapTileAdapter();
    }
}
