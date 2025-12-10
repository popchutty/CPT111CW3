package test.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.ShortFilm;
import model.Movie;

/**
 * Unit tests for ShortFilm class
 */
public class ShortFilmTest {
    
    private ShortFilm film;
    
    @Before
    public void setUp() {
        film = new ShortFilm("M001", "Short Film", "Animation", 2024, 7.5);
    }
    
    @Test
    public void testConstructor() {
        assertEquals("M001", film.getId());
        assertEquals("Short Film", film.getTitle());
        assertEquals("Animation", film.getGenre());
        assertEquals(2024, film.getYear());
        assertEquals(7.5, film.getRating(), 0.01);
        assertEquals(Movie.TYPE_SHORT, film.getMovieType());
        assertEquals(20, film.getDuration());
    }
    
    @Test
    public void testFullConstructor() {
        ShortFilm fullFilm = new ShortFilm("M002", "Animated Short", "Comedy", 
                                            2023, 8.0, 15, true);
        assertEquals("M002", fullFilm.getId());
        assertEquals(15, fullFilm.getDuration());
        assertTrue(fullFilm.isAnimated());
        assertEquals(Movie.TYPE_SHORT, fullFilm.getMovieType());
    }
    
    @Test
    public void testIsAnimated() {
        assertFalse(film.isAnimated());
        
        film.setAnimated(true);
        assertTrue(film.isAnimated());
    }
    
    @Test
    public void testFilmFestival() {
        assertNull(film.getFilmFestival());
        
        film.setFilmFestival("Cannes Film Festival");
        assertEquals("Cannes Film Festival", film.getFilmFestival());
    }
    
    @Test
    public void testFilmProperties() {
        film.setAnimated(true);
        film.setFilmFestival("Sundance");
        assertTrue(film.isAnimated());
        assertEquals("Sundance", film.getFilmFestival());
    }

    @Test
    public void testToDetailedString() {
        film.setAnimated(false);
        film.setFilmFestival(null);
        String baseInfo = film.toDetailedString();

        film.setAnimated(true);
        assertEquals(baseInfo + " | Animated", film.toDetailedString());

        film.setFilmFestival("");
        assertEquals(baseInfo + " | Animated", film.toDetailedString());

        film.setFilmFestival("Sundance");
        assertEquals(baseInfo + " | Animated | FilmFestival: Sundance", film.toDetailedString());
    }
}
