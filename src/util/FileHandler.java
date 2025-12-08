package util;

import java.io.*;
import java.util.ArrayList;

/**
 * FileHandler class - File handling utility class
 * Handles CSV file reading and writing with exception handling
 */
public class FileHandler {

    /**
     * Reads all lines from CSV file
     * @param filePath the file path
     * @return the list containing all lines, each as a string
     * @throws IOException if file reading fails
     */
    public static ArrayList<String> readCSV(String filePath) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader reader = null;
        IOException readException = null;
        
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            readException = e;
        }
        
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error closing file: " + e.getMessage());
            }
        }
        
        if (readException != null) {
            throw readException;
        }
        
        return lines;
    }

    /**
     * Writes to CSV file
     * @param filePath the file path
     * @param lines the lines to write
     * @throws IOException if file writing fails
     */
    public static void writeCSV(String filePath, ArrayList<String> lines) throws IOException {
        BufferedWriter writer = null;
        IOException writeException = null;
        
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                writer.newLine();
            }
        } catch (IOException e) {
            writeException = e;
        }
        
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.out.println("Error closing file: " + e.getMessage());
            }
        }
        
        if (writeException != null) {
            throw writeException;
        }
    }

    /**
     * Parses CSV line into field array
     * @param line the CSV line
     * @return the field array
     */
    public static String[] parseCSVLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return new String[0];
        }
        // return line.split(",");
        return line.split(",", -1);
    }

    /**
     * Checks if file exists
     * @param filePath the file path
     * @return true if exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * Creates file if it does not exist
     * @param filePath the file path
     * @throws IOException if file creation fails
     */
    public static void createFileIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            file.createNewFile();
        }
    }

    /**
     * Reads a specific line from file
     * @param filePath the file path
     * @param lineNumber the line number (0-based)
     * @return the line content, or null if not exists
     */
    public static String readLine(String filePath, int lineNumber) {
        try {
            ArrayList<String> lines = readCSV(filePath);
            if (lineNumber >= 0 && lineNumber < lines.size()) {
                return lines.get(lineNumber);
            }
        } catch (IOException e) {
            System.out.println("Error reading line: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates a specific line in file
     * @param filePath the file path
     * @param lineNumber the line number (0-based)
     * @param newLine the new line content
     * @throws IOException file operation exception
     */
    public static void updateLine(String filePath, int lineNumber, String newLine) throws IOException {
        ArrayList<String> lines = readCSV(filePath);
        if (lineNumber >= 0 && lineNumber < lines.size()) {
            lines.set(lineNumber, newLine);
            writeCSV(filePath, lines);
        }
    }

    /**
     * Appends a line to file
     * @param filePath the file path
     * @param line the line to append
     * @throws IOException file operation exception
     */
    public static void appendLine(String filePath, String line) throws IOException {
        ArrayList<String> lines = readCSV(filePath);
        lines.add(line);
        writeCSV(filePath, lines);
    }
}
