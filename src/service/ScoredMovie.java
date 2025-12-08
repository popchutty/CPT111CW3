package service;

import model.Movie;

/**
 * ScoredMovie - Movie with score
 * Used for movie scoring calculations in hybrid recommendation strategy
 */
public class ScoredMovie {
    public Movie movie;
    public double score;

    public ScoredMovie(Movie movie, double score) {
        this.movie = movie;
        this.score = score;
    }
}
