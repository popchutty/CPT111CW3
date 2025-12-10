package test.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.BasicUser;
import model.User;

/**
 * Unit tests for BasicUser class
 */
public class BasicUserTest {
    
    private BasicUser user;
    
    @Before
    public void setUp() {
        user = new BasicUser("basicuser", "password");
    }
    
    @Test
    public void testConstructor() {
        assertEquals("basicuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(User.TYPE_BASIC, user.getUserType());
    }
    
    @Test
    public void testGetMaxWatchlistSize() {
        assertEquals(10, user.getMaxWatchlistSize());
    }
    
    @Test
    public void testGetMaxRecommendations() {
        assertEquals(5, user.getMaxRecommendations());
    }
    
    @Test
    public void testCanUseAdvancedRecommendations() {
        assertFalse(user.canUseAdvancedRecommendations());
    }
    
    @Test
    public void testGetUpgradeHint() {
        String hint = user.getUpgradeHint();
        assertNotNull(hint);
        assertTrue(hint.contains("Premium"));
    }
    
    @Test
    public void testWatchlistLimit() {
        // Add 10 movies (max for basic user)
        for (int i = 1; i <= 10; i++) {
            assertTrue(user.addToWatchlist("movie" + i));
        }
        
        // Try to add 11th movie, should fail
        assertFalse(user.addToWatchlist("movie11"));
        assertEquals(10, user.getWatchlist().size());
    }
}
