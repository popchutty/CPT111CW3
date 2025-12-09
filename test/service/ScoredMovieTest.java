package test.service;

import org.junit.Test;
import static org.junit.Assert.*;

import model.Movie;
import service.ScoredMovie;

public class ScoredMovieTest {
    
    @Test
    public void testScoredMovie() {
        Movie movie = new Movie("M001", "The Shawshank Redemption", "Drama", 1994, 9.3);
        double score = 8.5;

        ScoredMovie scoredMovie = new ScoredMovie(movie, score);
        
        assertEquals(movie, scoredMovie.movie);
        assertEquals(score, scoredMovie.score, 0.001);
    }
}
