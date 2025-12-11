package service;

import model.Movie;
import model.User;
import java.util.ArrayList;

/**
 * YearBasedStrategy - Year-based recommendation strategy
 * Recommends highly-rated movies from recent years
 */
public class YearBasedStrategy extends RecommendationStrategy {

    private RecommendationEngine engine;

    public YearBasedStrategy(RecommendationEngine engine) {
        this.engine = engine;
    }

    @Override
    public String getName() {
        return "Recent & Popular";
    }

    @Override
    public String getDescription() {
        return "Recommends highly rated movies from recent years (2015+)";
    }

    @Override
    public boolean requiresPremium() {
        return true;
    }

    @Override
    public ArrayList<Movie> recommend(User user, MovieManager movieManager, int topN) {
        ArrayList<Movie> allMovies = movieManager.getAllMovies();
        ArrayList<String> excludeIds = new ArrayList<String>();
        excludeIds.addAll(user.getHistory().getMovieIds());
        excludeIds.addAll(user.getWatchlist().getMovieIds());

        ArrayList<Movie> recentMovies = new ArrayList<Movie>();
        for (int i = 0; i < allMovies.size(); i++) {
            Movie movie = allMovies.get(i);
            if (!excludeIds.contains(movie.getId()) && movie.getYear() >= 2015) {
                recentMovies.add(movie);
            }
        }

        engine.sortMoviesByRating(recentMovies);

        ArrayList<Movie> result = new ArrayList<Movie>();
        int limit = Math.min(topN, recentMovies.size());
        for (int i = 0; i < limit; i++) {
            result.add(recentMovies.get(i));
        }

        return result;
    }
}
