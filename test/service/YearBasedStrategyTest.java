package test.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.Movie;
import model.User;
import model.PremiumUser;
import service.YearBasedStrategy;
import service.RecommendationEngine;
import service.MovieManager;
import java.util.ArrayList;

public class YearBasedStrategyTest {
    
    private YearBasedStrategy strategy;
    private RecommendationEngine engine;
    private MovieManager movieManager;
    private User premiumUser;
    
    @Before
    public void setUp() {
        movieManager = new MovieManager("data/movies.csv");
        movieManager.loadMovies();
        
        engine = new RecommendationEngine(movieManager);
        strategy = new YearBasedStrategy(engine);
        
        premiumUser = new PremiumUser("premium", "password");
    }
    
    @Test
    public void testGetName() {
        String name = strategy.getName();
        assertNotNull(name);
        assertEquals("Recent & Popular", name);
    }
    
    @Test
    public void testGetDescription() {
        String description = strategy.getDescription();
        assertNotNull(description);
        assertTrue(description.contains("2015"));
        assertTrue(description.contains("recent"));
    }
    
    @Test
    public void testRequiresPremium() {
        assertTrue(strategy.requiresPremium());
    }
    
    @Test
    public void testRecommendWithNoHistory() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 5);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() <= 5);
        
        for (Movie movie : recommendations) {
            assertTrue(movie.getYear() >= 2015);
        }
    }
    
    @Test
    public void testRecommendOnlyRecentMovies() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
        
        assertNotNull(recommendations);
        
        for (Movie movie : recommendations) {
            assertTrue(movie.getYear() >= 2015);
        }
    }
    
    @Test
    public void testRecommendExcludesWatchedMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        ArrayList<String> recentMovieIds = new ArrayList<String>();
        for (Movie movie : allMovies) {
            if (movie.getYear() >= 2015) {
                recentMovieIds.add(movie.getId());
                if (recentMovieIds.size() >= 3) {
                    break;
                }
            }
        }
        
        if (recentMovieIds.size() >= 3) {
            premiumUser.markAsWatched(recentMovieIds.get(0), "2024-01-01");
            premiumUser.markAsWatched(recentMovieIds.get(1), "2024-01-02");
            
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
            
            for (Movie movie : recommendations) {
                assertFalse(movie.getId().equals(recentMovieIds.get(0)));
                assertFalse(movie.getId().equals(recentMovieIds.get(1)));
            }
        }
    }
    
    @Test
    public void testRecommendExcludesWatchlistMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        ArrayList<String> recentMovieIds = new ArrayList<String>();
        for (Movie movie : allMovies) {
            if (movie.getYear() >= 2015) {
                recentMovieIds.add(movie.getId());
                if (recentMovieIds.size() >= 2) {
                    break;
                }
            }
        }
        
        if (recentMovieIds.size() >= 2) {
            premiumUser.addToWatchlist(recentMovieIds.get(0));
            premiumUser.addToWatchlist(recentMovieIds.get(1));
            
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
            
            for (Movie movie : recommendations) {
                assertFalse(movie.getId().equals(recentMovieIds.get(0)));
                assertFalse(movie.getId().equals(recentMovieIds.get(1)));
            }
        }
    }
    
    @Test
    public void testRecommendRespectLimit() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 3);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() <= 3);
    }
    
    @Test
    public void testRecommendSortedByRating() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
        
        assertNotNull(recommendations);
        
        for (int i = 0; i < recommendations.size() - 1; i++) {
            assertTrue(recommendations.get(i).getRating() >= recommendations.get(i + 1).getRating());
        }
    }
    
    @Test
    public void testRecommendZeroTopN() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 0);
        
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
    
    @Test
    public void testRecommendLargeTopN() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 100);
        
        assertNotNull(recommendations);
        
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        int recentCount = 0;
        for (Movie movie : allMovies) {
            if (movie.getYear() >= 2015) {
                recentCount++;
            }
        }
        
        assertTrue(recommendations.size() <= recentCount);
    }
    
    @Test
    public void testRecommendWithBothHistoryAndWatchlist() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        ArrayList<String> recentMovieIds = new ArrayList<String>();
        for (Movie movie : allMovies) {
            if (movie.getYear() >= 2015) {
                recentMovieIds.add(movie.getId());
                if (recentMovieIds.size() >= 4) {
                    break;
                }
            }
        }
        
        if (recentMovieIds.size() >= 4) {
            premiumUser.markAsWatched(recentMovieIds.get(0), "2024-01-01");
            premiumUser.markAsWatched(recentMovieIds.get(1), "2024-01-02");
            premiumUser.addToWatchlist(recentMovieIds.get(2));
            premiumUser.addToWatchlist(recentMovieIds.get(3));
            
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
            
            for (Movie movie : recommendations) {
                assertFalse(premiumUser.getHistory().contains(movie.getId()));
                assertFalse(premiumUser.getWatchlist().contains(movie.getId()));
            }
        }
    }
    
    @Test
    public void testRecommendAllRecentMoviesExcluded() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        for (Movie movie : allMovies) {
            if (movie.getYear() >= 2015) {
                premiumUser.markAsWatched(movie.getId(), "2024-01-01");
            }
        }
        
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
        
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
    
    @Test
    public void testRecommendYearBoundary() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        boolean has2015Movie = false;
        boolean has2014Movie = false;
        
        for (Movie movie : allMovies) {
            if (movie.getYear() == 2015) {
                has2015Movie = true;
            }
            if (movie.getYear() == 2014) {
                has2014Movie = true;
            }
        }
        
        if (has2015Movie || has2014Movie) {
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 50);
            
            for (Movie movie : recommendations) {
                assertTrue(movie.getYear() >= 2015);
            }
        }
    }
    
    @Test
    public void testRecommendHighRatingFirst() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 5);
        
        assertNotNull(recommendations);
        
        if (recommendations.size() >= 2) {
            double firstRating = recommendations.get(0).getRating();
            double lastRating = recommendations.get(recommendations.size() - 1).getRating();
            assertTrue(firstRating >= lastRating);
        }
    }
    
    @Test
    public void testRecommendEmptyAfterExclusion() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        int recentCount = 0;
        for (Movie movie : allMovies) {
            if (movie.getYear() >= 2015) {
                recentCount++;
                premiumUser.addToWatchlist(movie.getId());
            }
        }
        
        if (recentCount > 0) {
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
            
            assertNotNull(recommendations);
            assertEquals(0, recommendations.size());
        }
    }
    
    @Test
    public void testRecommendConsistency() {
        ArrayList<Movie> recommendations1 = strategy.recommend(premiumUser, movieManager, 5);
        ArrayList<Movie> recommendations2 = strategy.recommend(premiumUser, movieManager, 5);
        
        assertEquals(recommendations1.size(), recommendations2.size());
        
        for (int i = 0; i < recommendations1.size(); i++) {
            assertEquals(recommendations1.get(i).getId(), recommendations2.get(i).getId());
        }
    }
}
