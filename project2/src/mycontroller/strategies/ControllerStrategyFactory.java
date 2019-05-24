package mycontroller.strategies;

/**
 * Team: W9-5
 * Description: this is the singleton strategy factory class
 */
public class ControllerStrategyFactory {

    private static ControllerStrategyFactory factory = null;

    private ControllerStrategyFactory() {}

    /**
     * return an instance of the factory, create one if none exists
     * @return an instance of the factory
     */
    public static ControllerStrategyFactory getInstance() {
        if(factory == null) {
            factory = new ControllerStrategyFactory();
        }
        return factory;
    }

    public iControllerStrategy getStrategy() {
        // TODO decide what the input is and the detail implementation
        return null;
    }

}
