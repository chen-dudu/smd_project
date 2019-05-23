package mycontroller.strategies;

/**
 * Team: W9-5
 * Description: this is the singleton strategy factory class
 */
public class StrategyFactory {

    private static StrategyFactory factory = null;

    private StrategyFactory() {}

    public static StrategyFactory getInstance() {
        if(factory == null) {
            factory = new StrategyFactory();
        }
        return factory;
    }

    // TODO decide what the input is and the detail implementation
    public iStrategy getStrategy() {
        return null;
    }

}
