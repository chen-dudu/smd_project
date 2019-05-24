package mycontroller.strategies;

/**
 * Team: W9-5
 * Description: this is the singleton strategy factory class
 */
public class ControllerStrategyFactory {

    private static ControllerStrategyFactory factory = null;

    private ControllerStrategyFactory() {}

    public static ControllerStrategyFactory getInstance() {
        if(factory == null) {
            factory = new ControllerStrategyFactory();
        }
        return factory;
    }

    // TODO decide what the input is and the detail implementation
    public iControllerStrategy getStrategy() {
        return null;
    }

}
