package test.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.Movie;

/**
 * Unit tests for Movie class
 */
public class MovieTest {
    
    private Movie movie;
    
    @Before
    public void setUp() {
        movie = new Movie("M001", "Test Movie", "Action", 2024, 8.5);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("M001", movie.getId());
        assertEquals("Test Movie", movie.getTitle());
        assertEquals("Action", movie.getGenre());
        assertEquals(2024, movie.getYear());
        assertEquals(8.5, movie.getRating(), 0.01);
        assertEquals(Movie.TYPE_FEATURE, movie.getMovieType());
        assertEquals(120, movie.getDuration());
    }
    
    @Test
    public void testFullConstructor() {
        Movie fullMovie = new Movie("M002", "Full Movie", "Drama", 2023, 9.0, 
                                    Movie.TYPE_SHORT, 30);
        assertEquals("M002", fullMovie.getId());
        assertEquals("Full Movie", fullMovie.getTitle());
        assertEquals("Drama", fullMovie.getGenre());
        assertEquals(2023, fullMovie.getYear());
        assertEquals(9.0, fullMovie.getRating(), 0.01);
        assertEquals(Movie.TYPE_SHORT, fullMovie.getMovieType());
        assertEquals(30, fullMovie.getDuration());
    }
    
    @Test
    public void testGettersAndSetters() {
        movie.setTitle("New Title");
        assertEquals("New Title", movie.getTitle());
        
        movie.setGenre("Comedy");
        assertEquals("Comedy", movie.getGenre());
        
        movie.setYear(2025);
        assertEquals(2025, movie.getYear());
        
        movie.setRating(7.5);
        assertEquals(7.5, movie.getRating(), 0.01);
        
        movie.setDuration(150);
        assertEquals(150, movie.getDuration());
    }

    @Test
    public void testMovieType() {
        movie.setMovieType("feature");
        assertTrue(movie.isFeatureFilm());

        movie.setMovieType("short");
        assertTrue(movie.isShortFilm());

        movie.setMovieType(null);
        movie.setDuration(10);
        assertTrue(movie.isShortFilm());

        movie.setDuration(50);
        assertTrue(movie.isFeatureFilm());
    }
    
    @Test
    public void testGetDuration() {
        movie.setDuration(125);
        assertEquals(125, movie.getDuration());
    }
    
    @Test
    public void testToString() {
        String str = movie.toString();
        assertTrue(str.contains("M001"));
        assertTrue(str.contains("Test Movie"));
        assertTrue(str.contains("Action"));
    }
    
    @Test
    public void testRatingBoundaries() {
        movie.setRating(0.0);
        assertEquals(0.0, movie.getRating(), 0.01);
        
        movie.setRating(10.0);
        assertEquals(10.0, movie.getRating(), 0.01);
    }

    @Test
    public void testEquals() {
        assertTrue(movie.equals(movie));
        assertFalse(movie.equals(null));
        assertFalse(movie.equals("String"));

        Movie otherMovie = new Movie("M001", "Test Movie", "Action", 2024, 8.5);
        assertTrue(movie.equals(otherMovie));
    }

    @Test
    public void testHashCode() {
        movie.setId("M001");
        assertEquals("M001".hashCode(), movie.hashCode());
    }
}
