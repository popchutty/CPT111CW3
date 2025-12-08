package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * History class
 * Records movie IDs and watch dates
 */
public class History {
    private HashMap<String, String> movieHistory;
    private ArrayList<String> movieIds;

    /**
     * Constructor
     */
    public History() {
        this.movieHistory = new HashMap<>();
        this.movieIds = new ArrayList<>();
    }

    /**
     * Adds a movie to the watch history
     * @param movieId the movie ID
     * @param watchDate the watch date (format: YYYY-MM-DD)
     */
    public void addMovie(String movieId, String watchDate) {
        if (!movieHistory.containsKey(movieId)) {
            movieIds.add(movieId);
        }
        movieHistory.put(movieId, watchDate);
    }

    /**
     * Gets the watch date of a movie
     * @param movieId the movie ID
     * @return the watch date, or null if not exists
     */
    public String getWatchDate(String movieId) {
        return movieHistory.get(movieId);
    }

    /**
     * Checks if a movie is in the watch history
     * @param movieId the movie ID
     * @return true if exists, false otherwise
     */
    public boolean contains(String movieId) {
        return movieHistory.containsKey(movieId);
    }

    /**
     * Gets all movie IDs in the watch history (in addition order)
     * @return the list of movie IDs
     */
    public ArrayList<String> getMovieIds() {
        return new ArrayList<>(movieIds);
    }

    /**
     * Gets the size of the watch history
     * @return the number of history records
     */
    public int size() {
        return movieHistory.size();
    }

    /**
     * Checks if the watch history is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return movieHistory.isEmpty();
    }

    /**
     * Clears the watch history
     */
    public void clear() {
        movieHistory.clear();
        movieIds.clear();
    }

    /**
     * Converts the watch history to a CSV format string
     * @return the CSV format string
     */
    public String toCSV() {
        if (movieIds.isEmpty()) {
            return "";
        }
        ArrayList<String> entries = new ArrayList<>();
        for (String movieId : movieIds) {
            String date = movieHistory.get(movieId);
            entries.add(movieId + "@" + date);
        }
        return String.join(";", entries);
    }

    /**
     * Loads the watch history from a CSV format string
     * @param csvData the CSV format string
     */
    public void loadFromCSV(String csvData) {
        movieHistory.clear();
        movieIds.clear();
        if (csvData != null && !csvData.trim().isEmpty()) {
            String[] entries = csvData.split(";");
            for (String entry : entries) {
                if (!entry.trim().isEmpty() && entry.contains("@")) {
                    String[] parts = entry.split("@");
                    if (parts.length == 2) {
                        String movieId = parts[0].trim();
                        String date = parts[1].trim();
                        addMovie(movieId, date);
                    }
                }
            }
        }
    }

    /**
     * Gets the complete watch history map
     * @return the map of movie ID to watch date
     */
    public HashMap<String, String> getMovieHistory() {
        return new HashMap<>(movieHistory);
    }
}
