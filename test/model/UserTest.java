package test.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.User;

/**
 * Unit tests for User class
 */
public class UserTest {
    
    private User user;
    
    @Before
    public void setUp() {
        user = new User("testuser", "password123");
    }
    
    @Test
    public void testConstructor() {
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertNotNull(user.getWatchlist());
        assertNotNull(user.getHistory());
        assertEquals(User.TYPE_BASIC, user.getUserType());
    }
    
    @Test
    public void testConstructorWithType() {
        User premiumUser = new User("premium", "pass", User.TYPE_PREMIUM);
        assertEquals(User.TYPE_PREMIUM, premiumUser.getUserType());
    }
    
    @Test
    public void testGettersAndSetters() {
        user.setUsername("newname");
        assertEquals("newname", user.getUsername());
        
        user.setPassword("newpass");
        assertEquals("newpass", user.getPassword());
        
        user.setUserType(User.TYPE_PREMIUM);
        assertEquals(User.TYPE_PREMIUM, user.getUserType());
    }
    
    @Test
    public void testAddToWatchlist() {
        assertTrue(user.addToWatchlist("movie1"));
        assertTrue(user.addToWatchlist("movie2"));
        assertEquals(2, user.getWatchlist().size());
    }
    
    @Test
    public void testAddToWatchlistDuplicate() {
        assertTrue(user.addToWatchlist("movie1"));
        assertFalse(user.addToWatchlist("movie1"));
        assertEquals(1, user.getWatchlist().size());
    }
    
    @Test
    public void testAddToWatchlistLimit() {
        // Basic user has limit of 10
        for (int i = 1; i <= 10; i++) {
            assertTrue(user.addToWatchlist("movie" + i));
        }
        assertFalse(user.addToWatchlist("movie11"));
        assertEquals(10, user.getWatchlist().size());
    }
    
    @Test
    public void testRemoveFromWatchlist() {
        user.addToWatchlist("movie1");
        assertTrue(user.removeFromWatchlist("movie1"));
        assertFalse(user.removeFromWatchlist("movie1"));
        assertEquals(0, user.getWatchlist().size());
    }
    
    @Test
    public void testMarkAsWatched() {
        user.addToWatchlist("movie1");
        user.markAsWatched("movie1", "2024-01-01");
        
        assertFalse(user.getWatchlist().contains("movie1"));
        assertTrue(user.getHistory().contains("movie1"));
        assertEquals("2024-01-01", user.getHistory().getWatchDate("movie1"));
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
    public void testToCSV() {
        user.addToWatchlist("movie1");
        user.markAsWatched("movie2", "2024-01-01");
        
        String csv = user.toCSV();
        assertTrue(csv.contains("testuser"));
        assertTrue(csv.contains("password123"));
        assertTrue(csv.contains(User.TYPE_BASIC));
    }
    
    @Test
    public void testGetUserTypeDisplayName() {
        assertEquals("Basic", user.getUserTypeDisplayName());
        
        User premiumUser = new User("premium", "pass", User.TYPE_PREMIUM);
        assertEquals("Premium", premiumUser.getUserTypeDisplayName());
    }
    
    @Test
    public void testToString() {
        String str = user.toString();
        assertTrue(str.contains("testuser"));
        assertTrue(str.contains("Basic"));
    }
}
