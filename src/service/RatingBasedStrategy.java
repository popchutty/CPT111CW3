package service;

import model.Movie;
import model.User;
import java.util.ArrayList;

/**
 * RatingBasedStrategy - Rating-based recommendation strategy
 * Recommends highest-rated movies
 */
public class RatingBasedStrategy extends RecommendationStrategy {

    private RecommendationEngine engine;

    public RatingBasedStrategy(RecommendationEngine engine) {
        this.engine = engine;
    }

    @Override
    public String getName() {
        return "Top Rated";
    }

    @Override
    public String getDescription() {
        return "Recommends highest rated movies you haven't seen";
    }

    @Override
    public boolean requiresPremium() {
        return false;
    }

    @Override
    public ArrayList<Movie> recommend(User user, MovieManager movieManager, int topN) {
        return engine.getTopRatedMovies(topN, user);
    }
}
