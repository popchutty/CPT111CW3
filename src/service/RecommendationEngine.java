package service;

import model.Movie;
import model.User;
import java.util.ArrayList;

/**
 * RecommendationEngine class - Recommendation engine
 * Supports multiple recommendation strategies with runtime switching
 */
public class RecommendationEngine {
    private MovieManager movieManager;
    private ArrayList<RecommendationStrategy> strategies;
    private RecommendationStrategy currentStrategy;

    /**
     * Constructor
     * @param movieManager the movie manager
     */
    public RecommendationEngine(MovieManager movieManager) {
        this.movieManager = movieManager;
        this.strategies = new ArrayList<RecommendationStrategy>();
        initializeStrategies();
    }
    
    /**
     * Initializes all recommendation strategies
     */
    private void initializeStrategies() {
        strategies.add(new GenreBasedStrategy(this));
        strategies.add(new RatingBasedStrategy(this));
        strategies.add(new YearBasedStrategy(this));
        strategies.add(new HybridStrategy(this));
        
        currentStrategy = strategies.get(0);
    }
    
    /**
     * Gets all available strategies
     * @return the list of strategies
     */
    public ArrayList<RecommendationStrategy> getAvailableStrategies() {
        ArrayList<RecommendationStrategy> copy = new ArrayList<RecommendationStrategy>();
        for (int i = 0; i < strategies.size(); i++) {
            copy.add(strategies.get(i));
        }
        return copy;
    }
    
    /**
     * Gets available strategies for user (considering permissions)
     * @param user the user
     * @return the list of available strategies
     */
    public ArrayList<RecommendationStrategy> getAvailableStrategiesForUser(User user) {
        ArrayList<RecommendationStrategy> available = new ArrayList<RecommendationStrategy>();
        for (int i = 0; i < strategies.size(); i++) {
            RecommendationStrategy strategy = strategies.get(i);
            if (!strategy.requiresPremium() || user.canUseAdvancedRecommendations()) {
                available.add(strategy);
            }
        }
        return available;
    }
    
    /**
     * Sets the current strategy
     * @param strategyIndex the strategy index
     * @return true if set successfully
     */
    public boolean setStrategy(int strategyIndex) {
        if (strategyIndex >= 0 && strategyIndex < strategies.size()) {
            currentStrategy = strategies.get(strategyIndex);
            return true;
        }
        return false;
    }
    
    /**
     * Sets the current strategy
     * @param strategy the strategy object
     */
    public void setStrategy(RecommendationStrategy strategy) {
        this.currentStrategy = strategy;
    }
    
    /**
     * Gets the current strategy
     * @return the current strategy
     */
    public RecommendationStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    /**
     * Recommends movies using current strategy
     * @param user the user object
     * @param topN the number of recommended movies
     * @return the list of recommended movies
     */
    public ArrayList<Movie> getRecommendations(User user, int topN) {
        int maxN = Math.min(topN, user.getMaxRecommendations());
        return currentStrategy.recommend(user, movieManager, maxN);
    }
    
    /**
     * Recommends movies using specified strategy
     * @param user the user object
     * @param topN the number of recommendations
     * @param strategyIndex the strategy index
     * @return the list of recommended movies
     */
    public ArrayList<Movie> getRecommendations(User user, int topN, int strategyIndex) {
        if (strategyIndex >= 0 && strategyIndex < strategies.size()) {
            RecommendationStrategy strategy = strategies.get(strategyIndex);
            int maxN = Math.min(topN, user.getMaxRecommendations());
            return strategy.recommend(user, movieManager, maxN);
        }
        return getRecommendations(user, topN);
    }
    
    /**
     * Gets top rated movies
     */
    public ArrayList<Movie> getTopRatedMovies(int count, User user) {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        ArrayList<String> excludeIds = new ArrayList<String>();
        excludeIds.addAll(user.getHistory().getMovieIds());
        excludeIds.addAll(user.getWatchlist().getMovieIds());
        
        ArrayList<Movie> filteredMovies = new ArrayList<Movie>();
        for (int i = 0; i < allMovies.size(); i++) {
            Movie movie = allMovies.get(i);
            if (!excludeIds.contains(movie.getId())) {
                filteredMovies.add(movie);
            }
        }
        
        sortMoviesByRating(filteredMovies);
        
        ArrayList<Movie> result = new ArrayList<Movie>();
        int limit = Math.min(count, filteredMovies.size());
        for (int i = 0; i < limit; i++) {
            result.add(filteredMovies.get(i));
        }
        
        return result;
    }

    /**
     * Sorts movies by rating in descending order
     */
    public void sortMoviesByRating(ArrayList<Movie> movies) {
        int n = movies.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (movies.get(j).getRating() < movies.get(j + 1).getRating()) {
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }
    
    /**
     * Sorts scored movies by score in descending order
     */
    public void sortScoredMovies(ArrayList<ScoredMovie> movies) {
        int n = movies.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (movies.get(j).score < movies.get(j + 1).score) {
                    ScoredMovie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }
}
