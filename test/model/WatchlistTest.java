package test.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.Watchlist;
import java.util.ArrayList;

/**
 * Unit tests for Watchlist class
 */
public class WatchlistTest {
    
    private Watchlist watchlist;
    
    @Before
    public void setUp() {
        watchlist = new Watchlist();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(watchlist);
        assertTrue(watchlist.isEmpty());
        assertEquals(0, watchlist.size());
    }
    
    @Test
    public void testAddMovie() {
        assertTrue(watchlist.addMovie("M001"));
        assertEquals(1, watchlist.size());
        assertTrue(watchlist.contains("M001"));
    }
    
    @Test
    public void testAddDuplicateMovie() {
        assertTrue(watchlist.addMovie("M001"));
        assertFalse(watchlist.addMovie("M001"));
        assertEquals(1, watchlist.size());
    }
    
    @Test
    public void testAddMultipleMovies() {
        watchlist.addMovie("M001");
        watchlist.addMovie("M002");
        watchlist.addMovie("M003");
        
        assertEquals(3, watchlist.size());
        assertTrue(watchlist.contains("M001"));
        assertTrue(watchlist.contains("M002"));
        assertTrue(watchlist.contains("M003"));
    }
    
    @Test
    public void testRemoveMovie() {
        watchlist.addMovie("M001");
        assertTrue(watchlist.removeMovie("M001"));
        assertFalse(watchlist.contains("M001"));
        assertEquals(0, watchlist.size());
    }
    
    @Test
    public void testRemoveNonExistentMovie() {
        assertFalse(watchlist.removeMovie("M999"));
    }
    
    @Test
    public void testContains() {
        assertFalse(watchlist.contains("M001"));
        watchlist.addMovie("M001");
        assertTrue(watchlist.contains("M001"));
    }
    
    @Test
    public void testGetMovieIds() {
        watchlist.addMovie("M001");
        watchlist.addMovie("M002");
        
        ArrayList<String> ids = watchlist.getMovieIds();
        assertEquals(2, ids.size());
        assertTrue(ids.contains("M001"));
        assertTrue(ids.contains("M002"));
        
        // Test that returned list is a copy
        ids.add("M003");
        assertEquals(2, watchlist.size());
    }
    
    @Test
    public void testIsEmpty() {
        assertTrue(watchlist.isEmpty());
        watchlist.addMovie("M001");
        assertFalse(watchlist.isEmpty());
        watchlist.removeMovie("M001");
        assertTrue(watchlist.isEmpty());
    }
    
    @Test
    public void testClear() {
        watchlist.addMovie("M001");
        watchlist.addMovie("M002");
        watchlist.addMovie("M003");
        
        watchlist.clear();
        assertTrue(watchlist.isEmpty());
        assertEquals(0, watchlist.size());
    }
    
    @Test
    public void testToCSV() {
        watchlist.addMovie("M001");
        watchlist.addMovie("M002");
        watchlist.addMovie("M003");
        
        String csv = watchlist.toCSV();
        assertEquals("M001;M002;M003", csv);
    }
    
    @Test
    public void testToCSVEmpty() {
        String csv = watchlist.toCSV();
        assertEquals("", csv);
    }
    
    @Test
    public void testLoadFromCSV() {
        watchlist.loadFromCSV("M001;M002;M003");
        
        assertEquals(3, watchlist.size());
        assertTrue(watchlist.contains("M001"));
        assertTrue(watchlist.contains("M002"));
        assertTrue(watchlist.contains("M003"));
    }
    
    @Test
    public void testLoadFromCSVEmpty() {
        watchlist.addMovie("M001");
        watchlist.loadFromCSV("");
        
        assertTrue(watchlist.isEmpty());
    }
    
    @Test
    public void testLoadFromCSVNull() {
        watchlist.loadFromCSV(null);
        assertTrue(watchlist.isEmpty());
    }
    
    @Test
    public void testLoadFromCSVOverwrite() {
        watchlist.addMovie("M001");
        watchlist.loadFromCSV("M002;M003");
        
        assertEquals(2, watchlist.size());
        assertFalse(watchlist.contains("M001"));
        assertTrue(watchlist.contains("M002"));
        assertTrue(watchlist.contains("M003"));
    }
}
