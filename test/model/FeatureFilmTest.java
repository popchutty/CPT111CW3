package test.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.FeatureFilm;
import model.Movie;

/**
 * Unit tests for FeatureFilm class
 */
public class FeatureFilmTest {
    
    private FeatureFilm film;
    
    @Before
    public void setUp() {
        film = new FeatureFilm("M001", "Feature Film", "Drama", 2024, 8.0);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("M001", film.getId());
        assertEquals("Feature Film", film.getTitle());
        assertEquals("Drama", film.getGenre());
        assertEquals(2024, film.getYear());
        assertEquals(8.0, film.getRating(), 0.01);
        assertEquals(Movie.TYPE_FEATURE, film.getMovieType());
        assertEquals(120, film.getDuration());
    }
    
    @Test
    public void testFullConstructor() {
        FeatureFilm fullFilm = new FeatureFilm("F002", "Full Feature", "Action", 2023, 9.0, 150, "John Doe");
        assertEquals("F002", fullFilm.getId());
        assertEquals(150, fullFilm.getDuration());
        assertEquals("John Doe", fullFilm.getDirector());
        assertEquals(Movie.TYPE_FEATURE, fullFilm.getMovieType());
    }
    
    @Test
    public void testDirector() {
        film.setDirector("Christopher Nolan");
        assertEquals("Christopher Nolan", film.getDirector());
    }
    
    @Test
    public void testMainCast() {
        String[] cast = {"Actor 1", "Actor 2", "Actor 3"};
        film.setMainCast(cast);
        assertArrayEquals(cast, film.getMainCast());
    }
    
    @Test
    public void testDirectorNotNull() {
        film.setDirector("Director Name");
        assertNotNull(film.getDirector());
        assertEquals("Director Name", film.getDirector());
    }

    @Test
    public void testToDetailedString() {
        film.setDirector("Director Name");
        assertEquals("[M001] Feature Film (2024) - Drama | Rating: 8.0/10.0 | Feature Film | 2h 0m | Director: Director Name", film.toDetailedString());

        film.setDirector("");
        assertEquals("[M001] Feature Film (2024) - Drama | Rating: 8.0/10.0 | Feature Film | 2h 0m", film.toDetailedString());

        film.setDirector(null);
        assertEquals("[M001] Feature Film (2024) - Drama | Rating: 8.0/10.0 | Feature Film | 2h 0m", film.toDetailedString());
    }
}
