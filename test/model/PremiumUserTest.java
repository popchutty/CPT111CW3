package test.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.PremiumUser;
import model.User;

/**
 * Unit tests for PremiumUser class
 */
public class PremiumUserTest {
    
    private PremiumUser user;
    
    @Before
    public void setUp() {
        user = new PremiumUser("premiumuser", "password");
    }
    
    @Test
    public void testConstructor() {
        assertEquals("premiumuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(User.TYPE_PREMIUM, user.getUserType());
    }
    
    @Test
    public void testGetMaxWatchlistSize() {
        assertEquals(100, user.getMaxWatchlistSize());
    }
    
    @Test
    public void testGetMaxRecommendations() {
        assertEquals(20, user.getMaxRecommendations());
    }
    
    @Test
    public void testCanUseAdvancedRecommendations() {
        assertTrue(user.canUseAdvancedRecommendations());
    }
    
    @Test
    public void testGetPremiumBenefits() {
        String benefits = user.getPremiumBenefits();
        assertNotNull(benefits);
        assertTrue(benefits.contains("Premium"));
        assertTrue(benefits.contains("100"));
        assertTrue(benefits.contains("20"));
    }
    
    @Test
    public void testLargeWatchlist() {
        for (int i = 1; i <= 50; i++) {
            assertTrue(user.addToWatchlist("movie" + i));
        }
        assertEquals(50, user.getWatchlist().size());
    }
}
