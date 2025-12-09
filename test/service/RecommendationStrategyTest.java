package test.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.Movie;
import model.User;
import model.BasicUser;
import service.RecommendationStrategy;
import service.MovieManager;
import java.util.ArrayList;

public class RecommendationStrategyTest {
    
    private RecommendationStrategy strategy;
    private MovieManager movieManager;
    private User user;
    
    @Before
    public void setUp() {
        strategy = new RecommendationStrategy();
        movieManager = new MovieManager("data/movies.csv");
        movieManager.loadMovies();
        user = new BasicUser("testuser", "password");
    }
    
    @Test
    public void testGetName() {
        String name = strategy.getName();
        assertNotNull(name);
        assertEquals("Default", name);
    }
    
    @Test
    public void testGetDescription() {
        String description = strategy.getDescription();
        assertNotNull(description);
        assertEquals("Default recommendation strategy", description);
    }
    
    @Test
    public void testRequiresPremium() {
        boolean requiresPremium = strategy.requiresPremium();
        assertFalse(requiresPremium);
    }
    
    @Test
    public void testRecommendReturnsEmptyList() {
        ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 5);
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
    
    @Test
    public void testRecommendWithZeroTopN() {
        ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 0);
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
    
    @Test
    public void testRecommendWithNegativeTopN() {
        ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, -1);
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
    
    @Test
    public void testRecommendWithLargeTopN() {
        ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 1000);
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
    
    @Test
    public void testRecommendWithNullUser() {
        ArrayList<Movie> recommendations = strategy.recommend(null, movieManager, 5);
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
    
    @Test
    public void testRecommendWithNullMovieManager() {
        ArrayList<Movie> recommendations = strategy.recommend(user, null, 5);
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
    
    @Test
    public void testMultipleRecommendCalls() {
        ArrayList<Movie> recommendations1 = strategy.recommend(user, movieManager, 5);
        ArrayList<Movie> recommendations2 = strategy.recommend(user, movieManager, 10);
        ArrayList<Movie> recommendations3 = strategy.recommend(user, movieManager, 3);
        
        assertNotNull(recommendations1);
        assertNotNull(recommendations2);
        assertNotNull(recommendations3);
        assertEquals(0, recommendations1.size());
        assertEquals(0, recommendations2.size());
        assertEquals(0, recommendations3.size());
    }
}
