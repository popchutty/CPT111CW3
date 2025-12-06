package service;

import model.Movie;
import model.User;
import java.util.ArrayList;

/**
 * RecommendationStrategy class - Recommendation strategy base class
 * Implements the strategy pattern using class inheritance and method overriding
 */
public class RecommendationStrategy {
    
    /**
     * Gets the strategy name
     * @return the strategy name
     */
    public String getName() {
        return "Default";
    }
    
    /**
     * Gets the strategy description
     * @return the strategy description
     */
    public String getDescription() {
        return "Default recommendation strategy";
    }
    
    /**
     * Recommends movies based on strategy
     * @param user the user object
     * @param movieManager the movie manager
     * @param topN the number of recommendations
     * @return the list of recommended movies
     */
    public ArrayList<Movie> recommend(User user, MovieManager movieManager, int topN) {
        return new ArrayList<Movie>();
    }
    
    /**
     * Checks if this strategy requires premium permission
     * @return true if requires
     */
    public boolean requiresPremium() {
        return false;
    }
}

