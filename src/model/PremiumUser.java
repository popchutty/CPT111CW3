package model;

/**
 * PremiumUser class - Premium user type
 * Has full feature permissions
 */
public class PremiumUser extends User {

    private static final int MAX_WATCHLIST_SIZE = 100;
    private static final int MAX_RECOMMENDATIONS = 20;

    /**
     * Constructor
     * @param username the username
     * @param password the password
     */
    public PremiumUser(String username, String password) {
        super(username, password, User.TYPE_PREMIUM);
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
        return true;
    }

    /**
     * Gets the premium benefits description
     * @return the benefits description
     */
    public String getPremiumBenefits() {
        return "Premium benefits: Unlimited watchlist (" + MAX_WATCHLIST_SIZE + 
               "), up to " + MAX_RECOMMENDATIONS + " recommendations, all recommendation strategies available!";
    }
}
