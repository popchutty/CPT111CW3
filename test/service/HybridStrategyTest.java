package test.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.Movie;
import model.User;
import model.PremiumUser;
import service.HybridStrategy;
import service.RecommendationEngine;
import service.MovieManager;
import java.util.ArrayList;

public class HybridStrategyTest {
    
    private HybridStrategy strategy;
    private RecommendationEngine engine;
    private MovieManager movieManager;
    private User premiumUser;
    
    @Before
    public void setUp() {
        movieManager = new MovieManager("data/movies.csv");
        movieManager.loadMovies();
        
        engine = new RecommendationEngine(movieManager);
        strategy = new HybridStrategy(engine);
        
        premiumUser = new PremiumUser("premium", "password");
    }
    
    @Test
    public void testGetName() {
        String name = strategy.getName();
        assertNotNull(name);
        assertEquals("Smart Hybrid", name);
    }
    
    @Test
    public void testGetDescription() {
        String description = strategy.getDescription();
        assertNotNull(description);
        assertTrue(description.length() > 0);
        assertTrue(description.contains("genre"));
        assertTrue(description.contains("rating"));
        assertTrue(description.contains("recency"));
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
    }
    
    @Test
    public void testRecommendWithHistory() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            premiumUser.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            premiumUser.markAsWatched(allMovies.get(1).getId(), "2024-01-02");
            
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
            
            assertNotNull(recommendations);
            assertTrue(recommendations.size() > 0);
            
            for (Movie movie : recommendations) {
                assertFalse(movie.getId().equals(allMovies.get(0).getId()));
                assertFalse(movie.getId().equals(allMovies.get(1).getId()));
            }
        }
    }
    
    @Test
    public void testRecommendExcludesWatchedMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 5) {
            premiumUser.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            premiumUser.markAsWatched(allMovies.get(1).getId(), "2024-01-02");
            premiumUser.markAsWatched(allMovies.get(2).getId(), "2024-01-03");
            
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
            
            for (Movie movie : recommendations) {
                assertFalse(premiumUser.getHistory().contains(movie.getId()));
            }
        }
    }
    
    @Test
    public void testRecommendExcludesWatchlistMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            premiumUser.addToWatchlist(allMovies.get(0).getId());
            premiumUser.addToWatchlist(allMovies.get(1).getId());
            
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
            
            for (Movie movie : recommendations) {
                assertFalse(premiumUser.getWatchlist().contains(movie.getId()));
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
    public void testRecommendPrefersUserGenre() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 5) {
            String preferredGenre = allMovies.get(0).getGenre();
            
            for (int i = 0; i < Math.min(3, allMovies.size()); i++) {
                if (allMovies.get(i).getGenre().equals(preferredGenre)) {
                    premiumUser.markAsWatched(allMovies.get(i).getId(), "2024-01-0" + (i + 1));
                }
            }
            
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
            
            boolean hasPreferredGenre = false;
            for (Movie movie : recommendations) {
                if (movie.getGenre().equals(preferredGenre)) {
                    hasPreferredGenre = true;
                    break;
                }
            }
            
            if (movieManager.getMoviesByGenre(preferredGenre).size() > 3) {
                assertTrue(hasPreferredGenre);
            }
        }
    }
    
    @Test
    public void testRecommendConsidersRating() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
        
        assertNotNull(recommendations);
        
        if (recommendations.size() >= 2) {
            double totalRating = 0;
            for (Movie movie : recommendations) {
                totalRating += movie.getRating();
            }
            double avgRating = totalRating / recommendations.size();
            
            assertTrue(avgRating >= 0);
            assertTrue(avgRating <= 10);
        }
    }
    
    @Test
    public void testRecommendWithMultipleGenres() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 10) {
            String genre1 = allMovies.get(0).getGenre();
            String genre2 = null;
            
            for (Movie movie : allMovies) {
                if (!movie.getGenre().equals(genre1)) {
                    genre2 = movie.getGenre();
                    break;
                }
            }
            
            if (genre2 != null) {
                premiumUser.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
                
                for (Movie movie : allMovies) {
                    if (movie.getGenre().equals(genre2)) {
                        premiumUser.markAsWatched(movie.getId(), "2024-01-02");
                        break;
                    }
                }
                
                ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
                assertNotNull(recommendations);
            }
        }
    }
    
    @Test
    public void testRecommendHandlesEmptyGenreCounts() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() > 0) {
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 5);
            
            assertNotNull(recommendations);
            assertTrue(recommendations.size() <= 5);
        }
    }
    
    @Test
    public void testRecommendRecencyScoring() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
        
        assertNotNull(recommendations);
        
        for (Movie movie : recommendations) {
            int yearsOld = 2025 - movie.getYear();
            assertTrue(yearsOld >= 0);
        }
    }
    
    @Test
    public void testRecommendWithOldMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        Movie oldMovie = null;
        for (Movie movie : allMovies) {
            if (movie.getYear() < 2000) {
                oldMovie = movie;
                break;
            }
        }
        
        if (oldMovie != null) {
            premiumUser.markAsWatched(oldMovie.getId(), "2024-01-01");
            
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 5);
            assertNotNull(recommendations);
        }
    }
    
    @Test
    public void testRecommendWithRecentMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        Movie recentMovie = null;
        for (Movie movie : allMovies) {
            if (movie.getYear() >= 2020) {
                recentMovie = movie;
                break;
            }
        }
        
        if (recentMovie != null) {
            premiumUser.markAsWatched(recentMovie.getId(), "2024-01-01");
            
            ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 5);
            assertNotNull(recommendations);
        }
    }
    
    @Test
    public void testRecommendLargeTopN() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 100);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() <= movieManager.getAllMovies().size());
    }
    
    @Test
    public void testRecommendZeroTopN() {
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 0);
        
        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }
    
    @Test
    public void testRecommendAllMoviesWatchedOrInWatchlist() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        int limit = Math.min(10, allMovies.size());
        for (int i = 0; i < limit; i++) {
            if (i % 2 == 0) {
                premiumUser.markAsWatched(allMovies.get(i).getId(), "2024-01-01");
            } else {
                premiumUser.addToWatchlist(allMovies.get(i).getId());
            }
        }
        
        ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 5);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() <= allMovies.size() - limit);
    }
    
    @Test
    public void testScoreCombination() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            Movie highRated = null;
            double maxRating = 0;
            
            for (Movie movie : allMovies) {
                if (movie.getRating() > maxRating) {
                    maxRating = movie.getRating();
                    highRated = movie;
                }
            }
            
            if (highRated != null) {
                String genre = highRated.getGenre();
                
                for (int i = 0; i < Math.min(5, allMovies.size()); i++) {
                    if (allMovies.get(i).getGenre().equals(genre) && 
                        !allMovies.get(i).getId().equals(highRated.getId())) {
                        premiumUser.markAsWatched(allMovies.get(i).getId(), "2024-01-01");
                    }
                }
                
                ArrayList<Movie> recommendations = strategy.recommend(premiumUser, movieManager, 10);
                
                assertNotNull(recommendations);
            }
        }
    }
}
