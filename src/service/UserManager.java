package service;

import model.User;
import model.BasicUser;
import model.PremiumUser;
import util.FileHandler;
import util.PasswordUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * UserManager class - User manager
 * Handles user login, registration, password verification, and data persistence
 * Supports user subclassing and password upgrades
 */
public class UserManager {
    private HashMap<String, User> users;
    private String userFilePath;

    /**
     * Constructor
     * @param userFilePath the user CSV file path
     */
    public UserManager(String userFilePath) {
        this.userFilePath = userFilePath;
        this.users = new HashMap<>();
    }

    /**
     * Loads all user data from CSV file
     * Compatible with old format (4 fields) and new format (5 fields)
     * @return true if loaded successfully, false otherwise
     */
    public boolean loadUsers() {
        try {
            if (!FileHandler.fileExists(userFilePath)) {
                System.out.println("User file does not exist: " + userFilePath);
                return false;
            }

            ArrayList<String> lines = FileHandler.readCSV(userFilePath);
            
            if (lines.isEmpty()) {
                System.out.println("User file is empty.");
                return false;
            }

            String header = lines.get(0);
            boolean isNewFormat = header.contains("usertype");

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = FileHandler.parseCSVLine(line);
                User user = null;
                
                if (isNewFormat && fields.length >= 5) {
                    String username = fields[0].trim();
                    String password = fields[1].trim();
                    String userType = fields[2].trim();
                    String watchlistData = fields[3].trim();
                    String historyData = fields[4].trim();
                    
                    user = createUserByType(username, password, userType);
                    
                    if (!watchlistData.isEmpty()) {
                        user.getWatchlist().loadFromCSV(watchlistData);
                    }
                    if (!historyData.isEmpty()) {
                        user.getHistory().loadFromCSV(historyData);
                    }
                    
                } else if (fields.length >= 4) {
                    String username = fields[0].trim();
                    String password = fields[1].trim();
                    String watchlistData = fields[2].trim();
                    String historyData = fields[3].trim();
                    
                    user = new BasicUser(username, password);
                    
                    if (!watchlistData.isEmpty()) {
                        user.getWatchlist().loadFromCSV(watchlistData);
                    }
                    if (!historyData.isEmpty()) {
                        user.getHistory().loadFromCSV(historyData);
                    }
                }
                
                if (user != null) {
                    users.put(user.getUsername(), user);
                }
            }

            System.out.println("Loaded " + users.size() + " users.");
            return true;

        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Creates user object by type
     */
    private User createUserByType(String username, String password, String userType) {
        if (User.TYPE_PREMIUM.equals(userType)) {
            return new PremiumUser(username, password);
        }
        return new BasicUser(username, password);
    }

    /**
     * Saves all user data to CSV file (using new format)
     * @return true if saved successfully, false otherwise
     */
    public boolean saveUsers() {
        try {
            ArrayList<String> lines = new ArrayList<String>();
            
            lines.add("username,password,usertype,watchlist,history");
            
            for (User user : users.values()) {
                lines.add(user.toCSV());
            }

            FileHandler.writeCSV(userFilePath, lines);
            return true;

        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
            return false;
        }
    }

    /**
     * User login
     * Supports automatic password upgrade (plain text to hash)
     * @param username the username
     * @param password the password
     * @return the User object if login successful, null otherwise
     */
    public User login(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            return null;
        }

        String storedPassword = user.getPassword();
        
        if (PasswordUtils.verifyPassword(password, storedPassword)) {
            if (PasswordUtils.needsUpgrade(storedPassword)) {
                String hashedPassword = PasswordUtils.hashPassword(password);
                user.setPassword(hashedPassword);
                saveUsers();
                System.out.println("Password has been upgraded to secure hash format.");
            }
            return user;
        }

        return null;
    }

    /**
     * User registration
     * @param username the username
     * @param password the password
     * @param userType the user type (basic or premium)
     * @return the new user if registration successful, null otherwise
     */
    public User register(String username, String password, String userType) {
        if (users.containsKey(username)) {
            return null;
        }
        
        if (username == null || username.trim().isEmpty() || username.length() < 3) {
            return null;
        }

        if (!PasswordUtils.isValidPassword(password)) {
            return null;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        
        User newUser = createUserByType(username, hashedPassword, userType);
        users.put(username, newUser);

        if (saveUsers()) {
            return newUser;
        }
        
        users.remove(username);
        return null;
    }
    
    /**
     * User registration (defaults to basic user)
     */
    public User register(String username, String password) {
        return register(username, password, User.TYPE_BASIC);
    }
    
    /**
     * Changes password
     * @param user the user object
     * @param oldPassword the old password
     * @param newPassword the new password
     * @return true if changed successfully, false otherwise
     */
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!PasswordUtils.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }
        
        if (!PasswordUtils.isValidPassword(newPassword)) {
            return false;
        }
        
        if (oldPassword.equals(newPassword)) {
            return false;
        }
        
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        
        return saveUsers();
    }
    
    /**
     * Upgrades user to premium user
     * @param user the user object
     * @return the upgraded user object
     */
    public User upgradeToPremiuim(User user) {
        if (User.TYPE_PREMIUM.equals(user.getUserType())) {
            return user;
        }
        
        PremiumUser premiumUser = new PremiumUser(user.getUsername(), user.getPassword());
        premiumUser.setWatchlist(user.getWatchlist());
        premiumUser.setHistory(user.getHistory());
        
        users.put(user.getUsername(), premiumUser);
        saveUsers();
        
        return premiumUser;
    }

    /**
     * Checks if user exists
     * @param username the username
     * @return true if exists, false otherwise
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    /**
     * Gets user
     * @param username the username
     * @return the User object, or null if not exists
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Updates user data (saves to file)
     * @param user the user to update
     * @return true if updated successfully, false otherwise
     */
    public boolean updateUser(User user) {
        if (!users.containsKey(user.getUsername())) {
            return false;
        }
        
        users.put(user.getUsername(), user);
        return saveUsers();
    }

    /**
     * Gets all users
     * @return the list of users
     */
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Deletes user
     * @param username the username
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteUser(String username) {
        if (!users.containsKey(username)) {
            return false;
        }
        
        users.remove(username);
        return saveUsers();
    }

    /**
     * Gets the total number of users
     * @return the user count
     */
    public int getUserCount() {
        return users.size();
    }
    
    /**
     * Gets password strength description
     * @param password the password
     * @return the strength description
     */
    public String getPasswordStrength(String password) {
        return PasswordUtils.getPasswordStrength(password);
    }
}
