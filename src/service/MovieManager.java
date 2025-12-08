package service;

import model.Movie;
import util.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * MovieManager class - Movie manager
 * Manages loading, searching, and retrieval of all movie data
 */
public class MovieManager {
    private HashMap<String, Movie> movies;
    private ArrayList<Movie> movieList;
    private String movieFilePath;

    /**
     * Constructor
     * @param movieFilePath the movie CSV file path
     */
    public MovieManager(String movieFilePath) {
        this.movieFilePath = movieFilePath;
        this.movies = new HashMap<String, Movie>();
        this.movieList = new ArrayList<Movie>();
    }

    /**
     * Loads all movie data from CSV file
     * @return true if loaded successfully, false otherwise
     */
    public boolean loadMovies() {
        try {
            ArrayList<String> lines = FileHandler.readCSV(movieFilePath);

            if (lines.isEmpty()) {
                System.out.println("Movie file is empty.");
                return false;
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = FileHandler.parseCSVLine(line);
                if (fields.length >= 5) {
                    try {
                        String id = fields[0].trim();
                        String title = fields[1].trim();
                        String genre = fields[2].trim();
                        int year = Integer.parseInt(fields[3].trim());
                        double rating = Double.parseDouble(fields[4].trim());

                        Movie movie = new Movie(id, title, genre, year, rating);
                        movies.put(id, movie);
                        movieList.add(movie);
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing line: " + line);
                    }
                }
            }

            System.out.println("Loaded " + movies.size() + " movies.");
            return true;

        } catch (IOException e) {
            System.out.println("Error loading movies: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets a movie by ID
     * @param movieId the movie ID
     * @return the Movie object, or null if not exists
     */
    public Movie getMovieById(String movieId) {
        return movies.get(movieId);
    }

    /**
     * Gets all movies
     * @return the list of all movies
     */
    public ArrayList<Movie> getAllMovies() {
        return new ArrayList<Movie>(movieList);
    }

    /**
     * Searches movies by genre
     * @param genre the movie genre
     * @return the list of movies matching the genre
     */
    public ArrayList<Movie> getMoviesByGenre(String genre) {
        ArrayList<Movie> result = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (movie.getGenre().equalsIgnoreCase(genre)) {
                result.add(movie);
            }
        }
        return result;
    }

    /**
     * Searches movies by title (fuzzy match)
     * @param keyword the keyword
     * @return the list of matching movies
     */
    public ArrayList<Movie> searchMoviesByTitle(String keyword) {
        ArrayList<Movie> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Movie movie : movies.values()) {
            if (movie.getTitle().toLowerCase().contains(lowerKeyword)) {
                result.add(movie);
            }
        }
        return result;
    }

    /**
     * Searches movies by year range
     * @param startYear the start year
     * @param endYear the end year
     * @return the list of matching movies
     */
    public ArrayList<Movie> getMoviesByYearRange(int startYear, int endYear) {
        ArrayList<Movie> result = new ArrayList<>();
        for (Movie movie : movies.values()) {
            int year = movie.getYear();
            if (year >= startYear && year <= endYear) {
                result.add(movie);
            }
        }
        return result;
    }

    /**
     * Gets movies with rating above the specified value
     * @param minRating the minimum rating
     * @return the list of matching movies
     */
    public ArrayList<Movie> getMoviesByMinRating(double minRating) {
        ArrayList<Movie> result = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (movie.getRating() >= minRating) {
                result.add(movie);
            }
        }
        return result;
    }

    /**
     * Gets movies by ID list
     * @param movieIds the list of movie IDs
     * @return the list of movies
     */
    public ArrayList<Movie> getMoviesByIds(ArrayList<String> movieIds) {
        ArrayList<Movie> result = new ArrayList<>();
        for (String id : movieIds) {
            Movie movie = getMovieById(id);
            if (movie != null) {
                result.add(movie);
            }
        }
        return result;
    }

    /**
     * Gets all distinct movie genres
     * @return the list of genres
     */
    public ArrayList<String> getAllGenres() {
        ArrayList<String> genres = new ArrayList<>();
        for (Movie movie : movies.values()) {
            String genre = movie.getGenre();
            if (!genres.contains(genre)) {
                genres.add(genre);
            }
        }
        return genres;
    }

    /**
     * Checks if a movie exists
     * @param movieId the movie ID
     * @return true if exists, false otherwise
     */
    public boolean movieExists(String movieId) {
        return movies.containsKey(movieId);
    }

    /**
     * Gets the total number of movies
     * @return the movie count
     */
    public int getMovieCount() {
        return movies.size();
    }
}
