package service;

import model.Movie;
import model.User;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * GenreBasedStrategy - Genre-based recommendation strategy
 * Recommends movies based on user's most-watched genres
 */
public class GenreBasedStrategy extends RecommendationStrategy {

    private RecommendationEngine engine;

    public GenreBasedStrategy(RecommendationEngine engine) {
        this.engine = engine;
    }

    public String getName() {
        return "Genre-Based";
    }

    public String getDescription() {
        return "Recommends movies based on your favorite genres from watch history";
    }

    public boolean requiresPremium() {
        return false;
    }

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

        if (genreCounts.isEmpty()) {
            ArrayList<String> watchlistIds = user.getWatchlist().getMovieIds();
            for (int i = 0; i < watchlistIds.size(); i++) {
                String movieId = watchlistIds.get(i);
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
        }

        if (genreCounts.isEmpty()) {
            return engine.getTopRatedMovies(topN, user);
        }

        String favoriteGenre = null;
        int maxCount = 0;
        for (String genre : genreCounts.keySet()) {
            Integer count = genreCounts.get(genre);
            if (count != null && count > maxCount) {
                maxCount = count;
                favoriteGenre = genre;
            }
        }

        if (favoriteGenre != null) {
            ArrayList<Movie> genreMovies = movieManager.getMoviesByGenre(favoriteGenre);
            ArrayList<String> excludeIds = new ArrayList<String>();
            excludeIds.addAll(watchedMovieIds);
            excludeIds.addAll(user.getWatchlist().getMovieIds());

            ArrayList<Movie> filteredMovies = new ArrayList<Movie>();
            for (int i = 0; i < genreMovies.size(); i++) {
                Movie movie = genreMovies.get(i);
                if (!excludeIds.contains(movie.getId())) {
                    filteredMovies.add(movie);
                }
            }

            engine.sortMoviesByRating(filteredMovies);

            int limit = Math.min(topN, filteredMovies.size());
            for (int i = 0; i < limit; i++) {
                recommendations.add(filteredMovies.get(i));
            }
        }

        if (recommendations.size() < topN) {
            ArrayList<Movie> additionalMovies = engine.getTopRatedMovies(
                topN - recommendations.size(), user
            );
            for (int i = 0; i < additionalMovies.size(); i++) {
                Movie movie = additionalMovies.get(i);
                if (!recommendations.contains(movie)) {
                    recommendations.add(movie);
                    if (recommendations.size() >= topN) {
                        break;
                    }
                }
            }
        }

        return recommendations;
    }
}
