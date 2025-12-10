package test.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.Movie;
import model.User;
import model.BasicUser;
import service.GenreBasedStrategy;
import service.RecommendationEngine;
import service.MovieManager;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Unit tests for GenreBasedStrategy class
 */
public class GenreBasedStrategyTest {
    
    private GenreBasedStrategy strategy;
    private RecommendationEngine engine;
    private MovieManager movieManager;
    private User user;
    
    @Before
    public void setUp() {
        movieManager = new MovieManager("data/movies.csv");
        movieManager.loadMovies();
        
        engine = new RecommendationEngine(movieManager);
        strategy = new GenreBasedStrategy(engine);
        
        user = new BasicUser("testuser", "password");
    }
    
    @Test
    public void testGetName() {
        String name = strategy.getName();
        assertNotNull(name);
        assertEquals("Genre-Based", name);
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
    public void testRecommendWithHistory() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            // Add movies from same genre to history
            String genre = allMovies.get(0).getGenre();
            user.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 5);
            
            assertNotNull(recommendations);
            assertTrue(recommendations.size() > 0);
            
            // Recommendations should prefer the watched genre
            boolean hasPreferredGenre = false;
            for (Movie movie : recommendations) {
                if (movie.getGenre().equals(genre)) {
                    hasPreferredGenre = true;
                    break;
                }
            }
            
            if (movieManager.getMoviesByGenre(genre).size() > 1) {
                assertTrue(hasPreferredGenre);
            }
        }
    }
    
    @Test
    public void testRecommendWithWatchlist() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 2) {
            // If no history, should use watchlist
            user.addToWatchlist(allMovies.get(0).getId());
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 5);
            
            assertNotNull(recommendations);
            assertTrue(recommendations.size() > 0);
        }
    }
    
    @Test
    public void testRecommendWithoutHistoryOrWatchlist() {
        // No history or watchlist - should return top rated movies
        ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 5);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() > 0);
    }
    
    @Test
    public void testRecommendExcludesWatchedMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            user.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 10);
            
            for (Movie movie : recommendations) {
                assertFalse(movie.getId().equals(allMovies.get(0).getId()));
            }
        }
    }
    
    @Test
    public void testRecommendExcludesWatchlistMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 5) {
            String genre = allMovies.get(0).getGenre();
            user.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            
            for (Movie movie : allMovies) {
                if (movie.getGenre().equals(genre) && !movie.getId().equals(allMovies.get(0).getId())) {
                    user.addToWatchlist(movie.getId());
                    break;
                }
            }
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 10);
            
            for (Movie movie : recommendations) {
                assertFalse(user.getWatchlist().contains(movie.getId()));
            }
        }
    }
    
    @Test
    public void testRecommendFillsWithAdditionalMovies() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 5) {
            String genre = allMovies.get(0).getGenre();
            
            ArrayList<String> sameGenreIds = new ArrayList<String>();
            for (Movie movie : allMovies) {
                if (movie.getGenre().equals(genre)) {
                    sameGenreIds.add(movie.getId());
                }
            }
            
            if (sameGenreIds.size() >= 2) {
                user.markAsWatched(sameGenreIds.get(0), "2024-01-01");
                
                for (int i = 1; i < sameGenreIds.size(); i++) {
                    user.addToWatchlist(sameGenreIds.get(i));
                }
                
                int requestCount = 10;
                ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, requestCount);
                
                assertNotNull(recommendations);
                assertTrue(recommendations.size() > 0);
            }
        }
    }
    
    @Test
    public void testRecommendAdditionalMoviesNoDuplicates() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            user.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 15);
            
            assertNotNull(recommendations);
            
            for (int i = 0; i < recommendations.size(); i++) {
                for (int j = i + 1; j < recommendations.size(); j++) {
                    assertFalse(recommendations.get(i).getId().equals(recommendations.get(j).getId()));
                }
            }
        }
    }
    
    @Test
    public void testRecommendStopsAtTopN() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            user.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            
            int topN = 5;
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, topN);
            
            assertNotNull(recommendations);
            assertTrue(recommendations.size() <= topN);
        }
    }
    
    @Test
    public void testRecommendMultipleGenres() {
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
                user.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
                
                for (Movie movie : allMovies) {
                    if (movie.getGenre().equals(genre1) && !movie.getId().equals(allMovies.get(0).getId())) {
                        user.markAsWatched(movie.getId(), "2024-01-02");
                        break;
                    }
                }
                
                for (Movie movie : allMovies) {
                    if (movie.getGenre().equals(genre2)) {
                        user.markAsWatched(movie.getId(), "2024-01-03");
                        break;
                    }
                }
                
                ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 10);
                
                assertNotNull(recommendations);
                assertTrue(recommendations.size() > 0);
            }
        }
    }
    
    @Test
    public void testRecommendSortedByRating() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            String genre = allMovies.get(0).getGenre();
            user.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 100);
            assertNotNull(recommendations);
            
            if (recommendations.size() >= 2) {
                boolean foundSameGenre = false;
                int startIdx = -1;
                
                for (int i = 0; i < recommendations.size(); i++) {
                    if (recommendations.get(i).getGenre().equals(genre)) {
                        if (!foundSameGenre) {
                            foundSameGenre = true;
                            startIdx = i;
                        }
                    } else if (foundSameGenre) {
                        break;
                    }
                }
                
                if (foundSameGenre && startIdx >= 0) {
                    for (int i = startIdx; i < recommendations.size() - 1; i++) {
                        if (recommendations.get(i).getGenre().equals(genre) && 
                            recommendations.get(i + 1).getGenre().equals(genre)) {
                            assertTrue(recommendations.get(i).getRating() >= 
                                      recommendations.get(i + 1).getRating());
                        }
                    }
                }
            }
        }
    }
    
    @Test
    public void testRecommendZeroTopN() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 1) {
            user.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 0);
            
            assertNotNull(recommendations);
            assertEquals(0, recommendations.size());
        }
    }
    
    @Test
    public void testRecommendLargeTopN() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 1) {
            user.markAsWatched(allMovies.get(0).getId(), "2024-01-01");
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 100);
            
            assertNotNull(recommendations);
            assertTrue(recommendations.size() <= allMovies.size() - 1);
        }
    }
    
    @Test
    public void testRecommendAllGenreMoviesExcluded() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 5) {
            String genre = allMovies.get(0).getGenre();
            
            for (Movie movie : allMovies) {
                if (movie.getGenre().equals(genre)) {
                    user.markAsWatched(movie.getId(), "2024-01-01");
                }
            }
            
            ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 10);
            
            assertNotNull(recommendations);
        }
    }
    
    @Test
    public void testRecommendFavoriteGenreSelection() {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 10) {
            HashMap<String, ArrayList<String>> genreMap = new HashMap<String, ArrayList<String>>();
            
            for (Movie movie : allMovies) {
                String genre = movie.getGenre();
                if (!genreMap.containsKey(genre)) {
                    genreMap.put(genre, new ArrayList<String>());
                }
                genreMap.get(genre).add(movie.getId());
            }
            
            String maxGenre = null;
            int maxSize = 0;
            
            for (String genre : genreMap.keySet()) {
                int size = genreMap.get(genre).size();
                if (size > maxSize) {
                    maxSize = size;
                    maxGenre = genre;
                }
            }
            
            if (maxGenre != null && maxSize >= 3) {
                ArrayList<String> ids = genreMap.get(maxGenre);
                user.markAsWatched(ids.get(0), "2024-01-01");
                user.markAsWatched(ids.get(1), "2024-01-02");
                
                ArrayList<Movie> recommendations = strategy.recommend(user, movieManager, 10);
                
                assertNotNull(recommendations);
            }
        }
    }
}
