package test.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.Movie;
import model.User;
import model.BasicUser;
import model.PremiumUser;
import service.RecommendationEngine;
import service.MovieManager;
import service.RecommendationStrategy;
import service.GenreBasedStrategy;
import java.util.ArrayList;

/**
 * Unit tests for RecommendationEngine class
 */
public class RecommendationEngineTest {
    
    private RecommendationEngine engine;
    private MovieManager movieManager;
    private User basicUser;
    private User premiumUser;
    
    @Before
    public void setUp() {
        movieManager = new MovieManager("data/movies.csv");
        movieManager.loadMovies();
        
        engine = new RecommendationEngine(movieManager);
        
        basicUser = new BasicUser("basic", "pass");
        premiumUser = new PremiumUser("premium", "pass");
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(engine);
        assertNotNull(engine.getCurrentStrategy());
    }
    
    @Test
    public void testGetAvailableStrategies() {
        ArrayList<RecommendationStrategy> strategies = engine.getAvailableStrategies();
        
        assertNotNull(strategies);
        assertTrue(strategies.size() > 0);
    }
    
    @Test
    public void testGetAvailableStrategiesForBasicUser() {
        ArrayList<RecommendationStrategy> strategies = engine.getAvailableStrategiesForUser(basicUser);
        
        assertNotNull(strategies);
        assertTrue(strategies.size() > 0);
        
        // Basic user should not have access to premium strategies
        for (RecommendationStrategy strategy : strategies) {
            assertFalse(strategy.requiresPremium());
        }
    }
    
    @Test
    public void testGetAvailableStrategiesForPremiumUser() {
        ArrayList<RecommendationStrategy> strategies = engine.getAvailableStrategiesForUser(premiumUser);
        
        assertNotNull(strategies);
        assertTrue(strategies.size() > 0);
        
        // Premium user should have access to all strategies
        int allStrategiesCount = engine.getAvailableStrategies().size();
        assertEquals(allStrategiesCount, strategies.size());
    }
    
    @Test
    public void testSetStrategyByIndex() {
        assertTrue(engine.setStrategy(0));
        assertNotNull(engine.getCurrentStrategy());
    }
    
    @Test
    public void testSetStrategyInvalidIndex() {
        assertFalse(engine.setStrategy(-1));
        assertFalse(engine.setStrategy(999));
    }
    
    @Test
    public void testSetStrategyByObject() {
        RecommendationStrategy strategy = new GenreBasedStrategy(engine);
        engine.setStrategy(strategy);
        
        assertEquals(strategy, engine.getCurrentStrategy());
    }
    
    @Test
    public void testGetCurrentStrategy() {
        RecommendationStrategy current = engine.getCurrentStrategy();
        assertNotNull(current);
    }
    
    @Test
    public void testRecommendMovies() {
        // Add some watch history
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        if (allMovies.size() > 0) {
            basicUser.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
        }
        
        ArrayList<Movie> recommendations = engine.getRecommendations(basicUser, 5);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() <= 5);
    }
    
    @Test
    public void testRecommendMoviesRespectLimit() {
        ArrayList<Movie> recommendations = engine.getRecommendations(basicUser, 3);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() <= 3);
    }
    
    @Test
    public void testRecommendMoviesExcludeWatchedAndWatchlist() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            // Add movies to history and watchlist
            basicUser.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            basicUser.addToWatchlist(allMovies.get(1).getId());
            
            ArrayList<Movie> recommendations = engine.getRecommendations(basicUser, 10);
            
            // Verify recommended movies don't include watched or watchlist movies
            for (Movie movie : recommendations) {
                assertFalse(basicUser.getHistory().contains(movie.getId()));
                assertFalse(basicUser.getWatchlist().contains(movie.getId()));
            }
        }
    }
    
    @Test
    public void testGetTopRatedMovies() {
        ArrayList<Movie> topRated = engine.getTopRatedMovies(5, basicUser);
        
        assertNotNull(topRated);
        assertTrue(topRated.size() <= 5);
        
        // Verify sorted by rating
        for (int i = 0; i < topRated.size() - 1; i++) {
            assertTrue(topRated.get(i).getRating() >= topRated.get(i + 1).getRating());
        }
    }
}
