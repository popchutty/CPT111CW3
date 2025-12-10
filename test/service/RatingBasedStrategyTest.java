package test.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.Movie;
import model.User;
import model.BasicUser;
import service.RatingBasedStrategy;
import service.RecommendationEngine;
import service.MovieManager;
import java.util.ArrayList;

/**
 * Unit tests for RatingBasedStrategy class
 */
public class RatingBasedStrategyTest {
    
    private RatingBasedStrategy strategy;
    private RecommendationEngine engine;
    private MovieManager movieManager;
    private User user;
    
    @Before
    public void setUp() {
        movieManager = new MovieManager("data/movies.csv");
        movieManager.loadMovies();
        
        engine = new RecommendationEngine(movieManager);
        strategy = new RatingBasedStrategy(engine);
        
        user = new BasicUser("testuser", "password");
    }
    
    @Test
    public void testGetName() {
        String name = strategy.getName();
        assertNotNull(name);
        assertEquals("Top Rated", name);
    }
    
    @Test
    public void testGetDescription() {
        String description = strategy.getDescription();
        assertNotNull(description);
        assertTrue(description.length() > 0);
    }
    
    @Test
    public void testRequiresPremium() {
        assertFalse(strategy.requiresPremium());
    }
    
    @Test
    public void testRecommend() {
        ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 5);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() <= 5);
        
        // Verify movies are sorted by rating (descending)
        for (int i = 0; i < recommendations.size() - 1; i++) {
            assertTrue(recommendations.get(i).getRating() >= recommendations.get(i + 1).getRating());
        }
    }
    
    @Test
    public void testRecommendExcludesWatchedMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() > 0) {
            // Mark highest rated movie as watched
            Movie highestRated = null;
            for (Movie movie : allMovies) {
                if (highestRated == null || movie.getRating() > highestRated.getRating()) {
                    highestRated = movie;
                }
            }
            
            if (highestRated != null) {
                user.markAsWatched(highestRated.getId(), "2024-01-01");
                
                ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 10);
                
                // Verify watched movie is not in recommendations
                for (Movie movie : recommendations) {
                    assertFalse(movie.getId().equals(highestRated.getId()));
                }
            }
        }
    }
    
    @Test
    public void testRecommendExcludesWatchlistMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() > 0) {
            user.addToWatchlist(allMovies.get(0).getId());
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 10);
            
            // Verify watchlist movie is not in recommendations
            for (Movie movie : recommendations) {
                assertFalse(movie.getId().equals(allMovies.get(0).getId()));
            }
        }
    }
    
    @Test
    public void testRecommendRespectLimit() {
        ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 3);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() <= 3);
    }
}
