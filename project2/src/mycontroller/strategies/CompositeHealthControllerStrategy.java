package mycontroller.strategies;

import mycontroller.adapters.TileType;

/**
 * Team: W9-5
 * Description: this is the class representing the health strategy used by
 *              our autoController
 */
public class CompositeHealthControllerStrategy extends CompositeControllerStrategy {

    /**
     * create a new health strategy object
     */
    public CompositeHealthControllerStrategy() {
        super();
        costTable.put(TileType.LAVA, 99);
        costTable.put(TileType.ROAD, 1);
        // take health only when it is necessary, so high cost
        costTable.put(TileType.WATER, 999);
        costTable.put(TileType.HEALTH, 999);
    }
}
