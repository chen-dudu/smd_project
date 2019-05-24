package mycontroller.strategies;

/**
 * Team: W9-5
 * Description:
 */
public class SearchStrategyFactory {

    private static SearchStrategyFactory factory = null;

    private SearchStrategyFactory() {}

    /**
     * return an instance of the factory, create one if none exists
     * @return an instance of the factory
     */
    public static SearchStrategyFactory getInstance() {
        if(factory == null) {
            factory = new SearchStrategyFactory();
        }
        return factory;
    }

    /**
     * return a search algorithm with specified type
     * @param type the type of the search algorithm to be returned
     * @return a search algorithm with specified type
     */
    public iSearchStrategy getStrategy(SearchAlgorithmType type) {
        // TODO decide detail implementation, and input parameter, need more later on
        switch (type) {
            case Dijkstra:
                return new Dijkstra();
        }
        return null;
    }
}
