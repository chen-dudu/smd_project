package mycontroller.strategies;

/**
 * Team: W9-5
 * Description:
 */
public class SearchStrategyFactory {

    private static SearchStrategyFactory factory = null;

    private SearchStrategyFactory() {}

    public static SearchStrategyFactory getInstance() {
        if(factory == null) {
            factory = new SearchStrategyFactory();
        }
        return factory;
    }

    // TODO decide detail implementation, and input parameter
    public iSearchStrategy getStrategy(SearchAlgorithmType type) {
        switch (type) {
            case Dijkstra:
                return new Dijkstra();
        }
    }
}
