package test.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.Movie;
import service.MovieManager;
import java.util.ArrayList;

/**
 * Unit tests for MovieManager class
 */
public class MovieManagerTest {
    
    private MovieManager movieManager;
    private static final String TEST_DATA_PATH = "data/movies.csv";
    
    @Before
    public void setUp() {
        movieManager = new MovieManager(TEST_DATA_PATH);
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(movieManager);
    }
    
    @Test
    public void testLoadMovies() {
        boolean loaded = movieManager.loadMovies();
        assertTrue(loaded);
    }
    
    @Test
    public void testGetAllMovies() {
        movieManager.loadMovies();
        ArrayList<Movie> movies = movieManager.getAllMovies();
        
        assertNotNull(movies);
        assertTrue(movies.size() > 0);
    }
    
    @Test
    public void testGetMovieById() {
        movieManager.loadMovies();
        
        // Get first movie
        ArrayList<Movie> movies = movieManager.getAllMovies();
        if (movies.size() > 0) {
            Movie first = movies.get(0);
            Movie retrieved = movieManager.getMovieById(first.getId());
            
            assertNotNull(retrieved);
            assertEquals(first.getId(), retrieved.getId());
            assertEquals(first.getTitle(), retrieved.getTitle());
        }
    }
    
    @Test
    public void testGetMovieByIdNotFound() {
        movieManager.loadMovies();
        Movie movie = movieManager.getMovieById("NONEXISTENT");
        
        assertNull(movie);
    }
    
    @Test
    public void testGetMoviesByGenre() {
        movieManager.loadMovies();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() > 0) {
            String genre = allMovies.get(0).getGenre();
            ArrayList<Movie> genreMovies = movieManager.getMoviesByGenre(genre);
            
            assertNotNull(genreMovies);
            assertTrue(genreMovies.size() > 0);
            
            // Verify all movies have the correct genre
            for (Movie movie : genreMovies) {
                assertEquals(genre, movie.getGenre());
            }
        }
    }
    
    @Test
    public void testGetMoviesByYearRange() {
        movieManager.loadMovies();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() > 0) {
            int year = allMovies.get(0).getYear();
            ArrayList<Movie> yearMovies = movieManager.getMoviesByYearRange(year, year);
            
            assertNotNull(yearMovies);
            
            // Verify all movies have the correct year
            for (Movie movie : yearMovies) {
                assertEquals(year, movie.getYear());
            }
        }
    }
    
    @Test
    public void testSearchMoviesByTitle() {
        movieManager.loadMovies();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() > 0) {
            String searchTerm = allMovies.get(0).getTitle().substring(0, 3);
            ArrayList<Movie> results = movieManager.searchMoviesByTitle(searchTerm);
            
            assertNotNull(results);
            // At least one movie should match
            assertTrue(results.size() > 0);
        }
    }
    
    @Test
    public void testSearchMoviesByTitleNoResults() {
        movieManager.loadMovies();
        ArrayList<Movie> results = movieManager.searchMoviesByTitle("XYZNONEXISTENT");
        
        assertNotNull(results);
        assertEquals(0, results.size());
    }
    
    @Test
    public void testGetMoviesByMinRating() {
        movieManager.loadMovies();
        ArrayList<Movie> highRated = movieManager.getMoviesByMinRating(7.0);
        
        assertNotNull(highRated);
        
        // Verify all movies have rating >= 7.0
        for (Movie movie : highRated) {
            assertTrue(movie.getRating() >= 7.0);
        }
    }
    
    @Test
    public void testGetAllGenres() {
        movieManager.loadMovies();
        ArrayList<String> genres = movieManager.getAllGenres();
        
        assertNotNull(genres);
        assertTrue(genres.size() > 0);
    }
    
    @Test
    public void testGetMovieCount() {
        movieManager.loadMovies();
        int count = movieManager.getMovieCount();
        
        assertTrue(count > 0);
        assertEquals(movieManager.getAllMovies().size(), count);
    }
    
    @Test
    public void testGetMoviesByIds() {
        movieManager.loadMovies();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            ArrayList<String> ids = new ArrayList<String>();
            ids.add(allMovies.get(0).getId());
            ids.add(allMovies.get(1).getId());
            ids.add(allMovies.get(2).getId());
            
            ArrayList<Movie> result = movieManager.getMoviesByIds(ids);
            
            assertNotNull(result);
            assertEquals(3, result.size());
            assertEquals(allMovies.get(0).getId(), result.get(0).getId());
            assertEquals(allMovies.get(1).getId(), result.get(1).getId());
            assertEquals(allMovies.get(2).getId(), result.get(2).getId());
        }
    }
    
    @Test
    public void testGetMoviesByIdsEmptyList() {
        movieManager.loadMovies();
        ArrayList<String> emptyIds = new ArrayList<String>();
        
        ArrayList<Movie> result = movieManager.getMoviesByIds(emptyIds);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    @Test
    public void testGetMoviesByIdsNonExistent() {
        movieManager.loadMovies();
        ArrayList<String> ids = new ArrayList<String>();
        ids.add("NONEXISTENT1");
        ids.add("NONEXISTENT2");
        
        ArrayList<Movie> result = movieManager.getMoviesByIds(ids);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }
    
    @Test
    public void testGetMoviesByIdsMixed() {
        movieManager.loadMovies();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 2) {
            ArrayList<String> ids = new ArrayList<String>();
            ids.add(allMovies.get(0).getId());
            ids.add("NONEXISTENT");
            ids.add(allMovies.get(1).getId());
            
            ArrayList<Movie> result = movieManager.getMoviesByIds(ids);
            
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(allMovies.get(0).getId(), result.get(0).getId());
            assertEquals(allMovies.get(1).getId(), result.get(1).getId());
        }
    }
    
    @Test
    public void testGetMoviesByIdsSingleId() {
        movieManager.loadMovies();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 1) {
            ArrayList<String> ids = new ArrayList<String>();
            ids.add(allMovies.get(0).getId());
            
            ArrayList<Movie> result = movieManager.getMoviesByIds(ids);
            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(allMovies.get(0).getId(), result.get(0).getId());
        }
    }
    
    @Test
    public void testGetMoviesByIdsDuplicates() {
        movieManager.loadMovies();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 1) {
            ArrayList<String> ids = new ArrayList<String>();
            String id = allMovies.get(0).getId();
            ids.add(id);
            ids.add(id);
            ids.add(id);
            
            ArrayList<Movie> result = movieManager.getMoviesByIds(ids);
            
            assertNotNull(result);
            assertEquals(3, result.size());
            for (Movie movie : result) {
                assertEquals(id, movie.getId());
            }
        }
    }
    
    @Test
    public void testMovieExistsTrue() {
        movieManager.loadMovies();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 1) {
            String id = allMovies.get(0).getId();
            boolean exists = movieManager.movieExists(id);
            
            assertTrue(exists);
        }
    }
    
    @Test
    public void testMovieExistsFalse() {
        movieManager.loadMovies();
        boolean exists = movieManager.movieExists("NONEXISTENT");
        
        assertFalse(exists);
    }
    
    @Test
    public void testMovieExistsMultiple() {
        movieManager.loadMovies();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        
        if (allMovies.size() >= 3) {
            assertTrue(movieManager.movieExists(allMovies.get(0).getId()));
            assertTrue(movieManager.movieExists(allMovies.get(1).getId()));
            assertTrue(movieManager.movieExists(allMovies.get(2).getId()));
        }
    }
    
    @Test
    public void testMovieExistsEmptyString() {
        movieManager.loadMovies();
        boolean exists = movieManager.movieExists("");
        
        assertFalse(exists);
    }
    
    @Test
    public void testMovieExistsNull() {
        movieManager.loadMovies();
        boolean exists = movieManager.movieExists(null);
        
        assertFalse(exists);
    }
    
    @Test
    public void testMovieExistsBeforeLoad() {
        boolean exists = movieManager.movieExists("ANYID");
        
        assertFalse(exists);
    }
    
    @Test
    public void testGetMoviesByIdsBeforeLoad() {
        ArrayList<String> ids = new ArrayList<String>();
        ids.add("ID1");
        ids.add("ID2");
        
        ArrayList<Movie> result = movieManager.getMoviesByIds(ids);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
