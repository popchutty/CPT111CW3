package model;

import java.util.ArrayList;

/**
 * Watchlist class
 * Provides methods to add, remove, and view movies
 */
public class Watchlist {
    private ArrayList<String> movieIds;

    /**
     * Constructor
     */
    public Watchlist() {
        this.movieIds = new ArrayList<>();
    }

    /**
     * Adds a movie to the watchlist
     * @param movieId the movie ID
     * @return true if added successfully, false if already exists
     */
    public boolean addMovie(String movieId) {
        if (movieIds.contains(movieId)) {
            return false;
        }
        movieIds.add(movieId);
        return true;
    }

    /**
     * Removes a movie from the watchlist
     * @param movieId the movie ID
     * @return true if removed successfully, false if not exists
     */
    public boolean removeMovie(String movieId) {
        return movieIds.remove(movieId);
    }

    /**
     * Checks if a movie is in the watchlist
     * @param movieId the movie ID
     * @return true if exists, false otherwise
     */
    public boolean contains(String movieId) {
        return movieIds.contains(movieId);
    }

    /**
     * Gets all movie IDs in the watchlist
     * @return the list of movie IDs
     */
    public ArrayList<String> getMovieIds() {
        return new ArrayList<>(movieIds);
    }

    /**
     * Gets the size of the watchlist
     * @return the size
     */
    public int size() {
        return movieIds.size();
    }

    /**
     * Checks if the watchlist is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return movieIds.isEmpty();
    }

    /**
     * Clears the watchlist
     */
    public void clear() {
        movieIds.clear();
    }

    /**
     * Converts the watchlist to a CSV format string (semicolon separated)
     * @return the CSV format string
     */
    public String toCSV() {
        if (movieIds.isEmpty()) {
            return "";
        }
        return String.join(";", movieIds);
    }

    /**
     * Loads the watchlist from a CSV format string
     * @param csvData the CSV format string
     */
    public void loadFromCSV(String csvData) {
        movieIds.clear();
        if (csvData != null && !csvData.trim().isEmpty()) {
            String[] ids = csvData.split(";");
            for (String id : ids) {
                if (!id.trim().isEmpty()) {
                    movieIds.add(id.trim());
                }
            }
        }
    }
}
