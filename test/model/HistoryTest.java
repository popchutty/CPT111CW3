package test.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import model.History;
import java.util.ArrayList;

/**
 * Unit tests for History class
 */
public class HistoryTest {
    
    private History history;
    
    @Before
    public void setUp() {
        history = new History();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(history);
        assertTrue(history.isEmpty());
        assertEquals(0, history.size());
    }
    
    @Test
    public void testAddMovie() {
        history.addMovie("M001", "2024-01-01");
        assertEquals(1, history.size());
        assertTrue(history.contains("M001"));
        assertEquals("2024-01-01", history.getWatchDate("M001"));
    }
    
    @Test
    public void testAddMultipleMovies() {
        history.addMovie("M001", "2024-01-01");
        history.addMovie("M002", "2024-01-02");
        history.addMovie("M003", "2024-01-03");
        
        assertEquals(3, history.size());
        assertTrue(history.contains("M001"));
        assertTrue(history.contains("M002"));
        assertTrue(history.contains("M003"));
    }
    
    @Test
    public void testAddMovieUpdateDate() {
        history.addMovie("M001", "2024-01-01");
        history.addMovie("M001", "2024-01-15");
        
        // Size should remain 1 as same movie is updated
        assertEquals(1, history.size());
        assertEquals("2024-01-15", history.getWatchDate("M001"));
    }
    
    @Test
    public void testGetWatchDate() {
        history.addMovie("M001", "2024-01-01");
        assertEquals("2024-01-01", history.getWatchDate("M001"));
        
        assertNull(history.getWatchDate("M999"));
    }
    
    @Test
    public void testContains() {
        assertFalse(history.contains("M001"));
        history.addMovie("M001", "2024-01-01");
        assertTrue(history.contains("M001"));
    }
    
    @Test
    public void testGetMovieIds() {
        history.addMovie("M001", "2024-01-01");
        history.addMovie("M002", "2024-01-02");
        
        ArrayList<String> ids = history.getMovieIds();
        assertEquals(2, ids.size());
        assertTrue(ids.contains("M001"));
        assertTrue(ids.contains("M002"));
        
        // Test that returned list is a copy
        ids.add("M003");
        assertEquals(2, history.size());
    }
    
    @Test
    public void testGetMovieIdsOrder() {
        history.addMovie("M001", "2024-01-01");
        history.addMovie("M002", "2024-01-02");
        history.addMovie("M003", "2024-01-03");
        
        ArrayList<String> ids = history.getMovieIds();
        // Should maintain addition order
        assertEquals("M001", ids.get(0));
        assertEquals("M002", ids.get(1));
        assertEquals("M003", ids.get(2));
    }
    
    @Test
    public void testIsEmpty() {
        assertTrue(history.isEmpty());
        history.addMovie("M001", "2024-01-01");
        assertFalse(history.isEmpty());
    }
    
    @Test
    public void testClear() {
        history.addMovie("M001", "2024-01-01");
        history.addMovie("M002", "2024-01-02");
        
        history.clear();
        assertTrue(history.isEmpty());
        assertEquals(0, history.size());
    }
    
    @Test
    public void testToCSV() {
        history.addMovie("M001", "2024-01-01");
        history.addMovie("M002", "2024-01-02");
        history.addMovie("M003", "2024-01-03");
        
        String csv = history.toCSV();
        assertEquals("M001@2024-01-01;M002@2024-01-02;M003@2024-01-03", csv);
    }
    
    @Test
    public void testToCSVEmpty() {
        String csv = history.toCSV();
        assertEquals("", csv);
    }
    
    @Test
    public void testLoadFromCSV() {
        history.loadFromCSV("M001@2024-01-01;M002@2024-01-02;M003@2024-01-03");
        
        assertEquals(3, history.size());
        assertTrue(history.contains("M001"));
        assertTrue(history.contains("M002"));
        assertTrue(history.contains("M003"));
        assertEquals("2024-01-01", history.getWatchDate("M001"));
        assertEquals("2024-01-02", history.getWatchDate("M002"));
        assertEquals("2024-01-03", history.getWatchDate("M003"));
    }
    
    @Test
    public void testLoadFromCSVEmpty() {
        history.addMovie("M001", "2024-01-01");
        history.loadFromCSV("");
        
        assertTrue(history.isEmpty());
    }
    
    @Test
    public void testLoadFromCSVNull() {
        history.loadFromCSV(null);
        assertTrue(history.isEmpty());
    }
    
    @Test
    public void testLoadFromCSVOverwrite() {
        history.addMovie("M001", "2024-01-01");
        history.loadFromCSV("M002@2024-01-02;M003@2024-01-03");
        
        assertEquals(2, history.size());
        assertFalse(history.contains("M001"));
        assertTrue(history.contains("M002"));
        assertTrue(history.contains("M003"));
    }
}
