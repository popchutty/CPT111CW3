package service;

import model.Movie;
import model.User;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * HybridStrategy - Hybrid recommendation strategy
 * Combines genre preference, rating, and recency for comprehensive recommendations
 */
public class HybridStrategy extends RecommendationStrategy {

    private RecommendationEngine engine;

    public HybridStrategy(RecommendationEngine engine) {
        this.engine = engine;
    }

    @Override
    public String getName() {
        return "Smart Hybrid";
    }

    @Override
    public String getDescription() {
        return "Advanced recommendation combining genre preference, rating, and recency";
    }

    @Override
    public boolean requiresPremium() {
        return true;
    }

    @Override
    public ArrayList<Movie> recommend(User user, MovieManager movieManager, int topN) {
        ArrayList<Movie> recommendations = new ArrayList<Movie>();
        HashMap<String, Integer> genreCounts = new HashMap<String, Integer>();
        ArrayList<String> watchedMovieIds = user.getHistory().getMovieIds();

        for (int i = 0; i < watchedMovieIds.size(); i++) {
            String movieId = watchedMovieIds.get(i);
            Movie movie = movieManager.getMovieById(movieId);
            if (movie != null) {
                String genre = movie.getGenre();
                Integer count = genreCounts.get(genre);
                if (count == null) {
                    count = 0;
                }
                genreCounts.put(genre, count + 1);
            }
        }

        ArrayList<String> excludeIds = new ArrayList<String>();
        excludeIds.addAll(watchedMovieIds);
        excludeIds.addAll(user.getWatchlist().getMovieIds());

        ArrayList<ScoredMovie> scoredMovies = new ArrayList<ScoredMovie>();
        ArrayList<Movie> allMovies = movieManager.getAllMovies();

        for (int i = 0; i < allMovies.size(); i++) {
            Movie movie = allMovies.get(i);
            if (excludeIds.contains(movie.getId())) {
                continue;
            }

            double score = 0;
            score += movie.getRating() * 4;

            Integer genreCount = genreCounts.get(movie.getGenre());
            if (genreCount == null) {
                genreCount = 0;
            }
            score += genreCount * 4;

            int yearsOld = 2025 - movie.getYear();
            double recencyScore = Math.max(0, 10 - yearsOld * 0.3);
            score += recencyScore * 2;

            scoredMovies.add(new ScoredMovie(movie, score));
        }

        engine.sortScoredMovies(scoredMovies);

        int limit = Math.min(topN, scoredMovies.size());
        for (int i = 0; i < limit; i++) {
            recommendations.add(scoredMovies.get(i).movie);
        }

        return recommendations;
    }
}
