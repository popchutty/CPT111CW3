package model;

/**
 * BasicUser class - Basic user type
 * Has limited feature permissions
 */
public class BasicUser extends User {

    private static int MAX_WATCHLIST_SIZE = 10;
    private static int MAX_RECOMMENDATIONS = 5;

    /**
     * Constructor
     * @param username the username
     * @param password the password
     */
    public BasicUser(String username, String password) {
        super(username, password, User.TYPE_BASIC);
    }

    @Override
    public int getMaxWatchlistSize() {
        return MAX_WATCHLIST_SIZE;
    }

    @Override
    public int getMaxRecommendations() {
        return MAX_RECOMMENDATIONS;
    }

    @Override
    public boolean canUseAdvancedRecommendations() {
        return false;
    }

    /**
     * Gets the upgrade hint for premium user
     * @return the hint message
     */
    public String getUpgradeHint() {
        return "Upgrade to Premium for unlimited watchlist, more recommendations, and advanced features!";
    }
}
