package test.util;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import util.FileHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Unit tests for FileHandler class
 */
public class FileHandlerTest {
    
    private static String TEST_FILE = "data/test_file_handler.csv";
    private static String TEST_DIR = "data/test_subdir";
    private static String TEST_FILE_IN_DIR = TEST_DIR + "/test.csv";
    
    @Before
    public void setUp() {
        // Clean up any existing test files
        cleanUpTestFiles();
    }
    
    @After
    public void tearDown() {
        // Clean up test files after each test
        cleanUpTestFiles();
    }
    
    private void cleanUpTestFiles() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
        
        File fileInDir = new File(TEST_FILE_IN_DIR);
        if (fileInDir.exists()) {
            fileInDir.delete();
        }
        
        File dir = new File(TEST_DIR);
        if (dir.exists()) {
            dir.delete();
        }
    }

    @Test
    public void testConstructor() {
        FileHandler handler = new FileHandler();
        assertNotNull(handler);
    }
    
    @Test
    public void testWriteAndReadCSV() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("header1,header2,header3");
        lines.add("value1,value2,value3");
        lines.add("data1,data2,data3");
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        ArrayList<String> readLines = FileHandler.readCSV(TEST_FILE);
        
        assertEquals(3, readLines.size());
        assertEquals("header1,header2,header3", readLines.get(0));
        assertEquals("value1,value2,value3", readLines.get(1));
        assertEquals("data1,data2,data3", readLines.get(2));
    }
    
    @Test
    public void testWriteCSVEmptyList() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        ArrayList<String> readLines = FileHandler.readCSV(TEST_FILE);
        assertEquals(0, readLines.size());
    }
    
    @Test(expected = IOException.class)
    public void testReadCSVFileNotFound() throws IOException {
        FileHandler.readCSV("nonexistent/file.csv");
    }
    
    @Test(expected = IOException.class)
    public void testWriteCSVInvalidPath() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("test");
        
        // Try to write to an invalid path (assuming /invalid/path doesn't exist and can't be created)
        FileHandler.writeCSV("/invalid/path/that/does/not/exist/file.csv", lines);
    }
    
    @Test
    public void testParseCSVLine() {
        String line = "field1,field2,field3";
        String[] fields = FileHandler.parseCSVLine(line);
        
        assertEquals(3, fields.length);
        assertEquals("field1", fields[0]);
        assertEquals("field2", fields[1]);
        assertEquals("field3", fields[2]);
    }
    
    @Test
    public void testParseCSVLineWithEmptyFields() {
        String line = "field1,,field3,";
        String[] fields = FileHandler.parseCSVLine(line);
        
        assertEquals(4, fields.length);
        assertEquals("field1", fields[0]);
        assertEquals("", fields[1]);
        assertEquals("field3", fields[2]);
        assertEquals("", fields[3]);
    }
    
    @Test
    public void testParseCSVLineEmpty() {
        String[] fields = FileHandler.parseCSVLine("");
        assertEquals(0, fields.length);
    }
    
    @Test
    public void testParseCSVLineNull() {
        String[] fields = FileHandler.parseCSVLine(null);
        assertEquals(0, fields.length);
    }
    
    @Test
    public void testParseCSVLineWhitespace() {
        String[] fields = FileHandler.parseCSVLine("   ");
        assertEquals(0, fields.length);
    }
    
    @Test
    public void testParseCSVLineSingleField() {
        String line = "singlefield";
        String[] fields = FileHandler.parseCSVLine(line);
        
        assertEquals(1, fields.length);
        assertEquals("singlefield", fields[0]);
    }
    
    @Test
    public void testFileExists() throws IOException {
        assertFalse(FileHandler.fileExists(TEST_FILE));
        
        ArrayList<String> lines = new ArrayList<>();
        lines.add("test");
        FileHandler.writeCSV(TEST_FILE, lines);
        
        assertTrue(FileHandler.fileExists(TEST_FILE));
    }
    
    @Test
    public void testFileExistsDirectory() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdir();
        }
        
        // Directory should return false (not a file)
        assertFalse(FileHandler.fileExists("data"));
        
        dir.delete();
    }
    
    @Test
    public void testReadLine() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("line0");
        lines.add("line1");
        lines.add("line2");
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        assertEquals("line0", FileHandler.readLine(TEST_FILE, 0));
        assertEquals("line1", FileHandler.readLine(TEST_FILE, 1));
        assertEquals("line2", FileHandler.readLine(TEST_FILE, 2));
    }
    
    @Test
    public void testReadLineOutOfBounds() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("line0");
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        assertNull(FileHandler.readLine(TEST_FILE, 5));
        assertNull(FileHandler.readLine(TEST_FILE, -1));
    }
    
    @Test
    public void testReadLineFileNotFound() {
        String result = FileHandler.readLine("nonexistent.csv", 0);
        assertNull(result);
    }
    
    @Test
    public void testUpdateLine() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("line0");
        lines.add("line1");
        lines.add("line2");
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        FileHandler.updateLine(TEST_FILE, 1, "updated_line1");
        
        ArrayList<String> updatedLines = FileHandler.readCSV(TEST_FILE);
        assertEquals(3, updatedLines.size());
        assertEquals("line0", updatedLines.get(0));
        assertEquals("updated_line1", updatedLines.get(1));
        assertEquals("line2", updatedLines.get(2));
    }
    
    @Test
    public void testUpdateLineOutOfBounds() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("line0");
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        // Should not throw exception, just do nothing
        FileHandler.updateLine(TEST_FILE, 5, "new_line");
        
        ArrayList<String> readLines = FileHandler.readCSV(TEST_FILE);
        assertEquals(1, readLines.size());
        assertEquals("line0", readLines.get(0));
    }
    
    @Test
    public void testUpdateLineNegativeIndex() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("line0");
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        // Should not throw exception, just do nothing
        FileHandler.updateLine(TEST_FILE, -1, "new_line");
        
        ArrayList<String> readLines = FileHandler.readCSV(TEST_FILE);
        assertEquals(1, readLines.size());
        assertEquals("line0", readLines.get(0));
    }
    
    @Test
    public void testAppendLine() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("line0");
        lines.add("line1");
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        FileHandler.appendLine(TEST_FILE, "line2");
        
        ArrayList<String> readLines = FileHandler.readCSV(TEST_FILE);
        assertEquals(3, readLines.size());
        assertEquals("line0", readLines.get(0));
        assertEquals("line1", readLines.get(1));
        assertEquals("line2", readLines.get(2));
    }
    
    @Test
    public void testAppendLineToEmptyFile() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        FileHandler.writeCSV(TEST_FILE, lines);
        
        FileHandler.appendLine(TEST_FILE, "first_line");
        
        ArrayList<String> readLines = FileHandler.readCSV(TEST_FILE);
        assertEquals(1, readLines.size());
        assertEquals("first_line", readLines.get(0));
    }
    
    @Test
    public void testAppendMultipleLines() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("line0");
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        FileHandler.appendLine(TEST_FILE, "line1");
        FileHandler.appendLine(TEST_FILE, "line2");
        FileHandler.appendLine(TEST_FILE, "line3");
        
        ArrayList<String> readLines = FileHandler.readCSV(TEST_FILE);
        assertEquals(4, readLines.size());
        assertEquals("line3", readLines.get(3));
    }
    
    @Test
    public void testReadCSVWithSpecialCharacters() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("name,age,email");
        lines.add("John Doe,30,john@example.com");
        lines.add("Jane Smith,25,jane@test.org");
        
        FileHandler.writeCSV(TEST_FILE, lines);
        
        ArrayList<String> readLines = FileHandler.readCSV(TEST_FILE);
        assertEquals(3, readLines.size());
        assertTrue(readLines.get(1).contains("@"));
    }
    
    @Test
    public void testParseCSVLineWithManyFields() {
        String line = "a,b,c,d,e,f,g,h,i,j";
        String[] fields = FileHandler.parseCSVLine(line);
        
        assertEquals(10, fields.length);
        assertEquals("a", fields[0]);
        assertEquals("j", fields[9]);
    }
}
