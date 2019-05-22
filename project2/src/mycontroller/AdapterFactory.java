package mycontroller;

import tiles.MapTile;

/**
 * Team: W9-5
 * Description: this is the singleton adapter factory class
 */
public class AdapterFactory {

    private static AdapterFactory factory = null;

    private AdapterFactory() {}

    public static AdapterFactory getInstance() {
        if(factory == null) {
            factory = new AdapterFactory();
        }
        return factory;
    }

    public iTileAdapter getAdapter(MapTile tile) {
        if(tile.getType() == MapTile.Type.TRAP) {
            return new TrapTileAdapter();
        }
        return new MapTileAdapter();
    }
}
