package model;

/**
 * User class - User base class
 * Contains username, password, watchlist, and watch history
 * Supports subclassing (BasicUser, PremiumUser)
 */
public class User {
    public static String TYPE_BASIC = "basic";
    public static String TYPE_PREMIUM = "premium";

    protected String username;
    protected String password;
    protected Watchlist watchlist;
    protected History history;
    protected String userType;

    /**
     * Constructor
     * @param username the username
     * @param password the password (encrypted)
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.watchlist = new Watchlist();
        this.history = new History();
        this.userType = TYPE_BASIC;
    }

    /**
     * Constructor with user type
     * @param username the username
     * @param password the password
     * @param userType the user type
     */
    public User(String username, String password, String userType) {
        this(username, password);
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public History getHistory() {
        return history;
    }

    public String getUserType() {
        return userType;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * Gets the maximum watchlist size
     * @return the maximum size
     */
    public int getMaxWatchlistSize() {
        return 10;
    }

    /**
     * Gets the maximum number of recommendations
     * @return the maximum number
     */
    public int getMaxRecommendations() {
        return 5;
    }

    /**
     * Checks if advanced recommendation strategies can be used
     * @return whether available
     */
    public boolean canUseAdvancedRecommendations() {
        return false;
    }

    /**
     * Adds a movie to the watchlist
     * @param movieId the movie ID
     * @return true if added successfully
     */
    public boolean addToWatchlist(String movieId) {
        if (watchlist.size() >= getMaxWatchlistSize()) {
            return false;
        }
        return watchlist.addMovie(movieId);
    }

    /**
     * Removes a movie from the watchlist
     * @param movieId the movie ID
     * @return true if removed successfully
     */
    public boolean removeFromWatchlist(String movieId) {
        return watchlist.removeMovie(movieId);
    }

    /**
     * Marks a movie as watched
     * @param movieId the movie ID
     * @param watchDate the watch date
     */
    public void markAsWatched(String movieId, String watchDate) {
        watchlist.removeMovie(movieId);
        history.addMovie(movieId, watchDate);
    }

    /**
     * Converts to a CSV format string
     * @return the CSV format string
     */
    public String toCSV() {
        return username + ","
                + password + ","
                + userType + ","
                + watchlist.toCSV() + ","
                + history.toCSV();
    }

    /**
     * Gets the user type display name
     * @return the display name
     */
    public String getUserTypeDisplayName() {
        if (TYPE_PREMIUM.equals(userType)) {
            return "Premium";
        }
        return "Basic";
    }

    @Override
    public String toString() {
        return "User{" +
               "username='" + username + '\'' +
               ", type=" + getUserTypeDisplayName() +
               ", watchlist size=" + watchlist.size() +
               ", history size=" + history.size() +
               '}';
    }
}
