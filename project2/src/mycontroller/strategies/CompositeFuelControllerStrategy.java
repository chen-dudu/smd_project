package mycontroller.strategies;

import mycontroller.adapters.TileType;

/**
 * Team: W9-5
 * Description: this is the class representing the fuel strategy used by
 *              our autoController
 */
public class CompositeFuelControllerStrategy extends CompositeControllerStrategy {

    /**
     * create a new composite fuel controller strategy object
     */
    public CompositeFuelControllerStrategy() {
        super();
        // everything has the same, always choose shortest path
        costTable.put(TileType.LAVA, 1);
        costTable.put(TileType.ROAD, 1);
        costTable.put(TileType.WATER, 1);
        costTable.put(TileType.HEALTH, 1);
    }
}
