package util;

/**
 * PasswordUtils class - Password utility class
 * Provides simple password hashing and verification
 * Supports recognition and conversion of legacy plaintext passwords
 */
public class PasswordUtils {

    private static String HASH_PREFIX = "$HASH$";

    /**
     * Hashes password using simple hash algorithm
     * @param password the original password
     * @return the hashed password
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }

        return HASH_PREFIX + simpleHash(password);
    }

    /**
     * Simple hash algorithm
     * @param text the text to hash
     * @return the hash result
     */
    private static String simpleHash(String text) {
        String hashString = "";
        hashString = Integer.toHexString(text.hashCode());
        return hashString;
    }



    /**
     * Verifies if password matches
     * Supports verification of hashed passwords and plaintext passwords (backward compatibility)
     * @param inputPassword the password entered by user
     * @param storedPassword the stored password
     * @return true if matches, false otherwise
     */
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }

        if (isHashedPassword(storedPassword)) {
            String hashedInput = hashPassword(inputPassword);
            return hashedInput.equals(storedPassword);
        } else {
            return inputPassword.equals(storedPassword);
        }
    }

    /**
     * Checks if password is in hashed format
     * @param password the password string
     * @return true if in hashed format
     */
    public static boolean isHashedPassword(String password) {
        return password != null && password.startsWith(HASH_PREFIX);
    }

    /**
     * Checks if password needs upgrade (from plaintext to hash)
     * @param storedPassword the stored password
     * @return true if needs upgrade
     */
    public static boolean needsUpgrade(String storedPassword) {
        return storedPassword != null && !isHashedPassword(storedPassword);
    }

    /**
     * Validates password strength
     * @param password the password
     * @return true if meets requirements
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return false;
        }
        if (password.startsWith(HASH_PREFIX)) {
            System.out.println("Password cannot start with reserved prefix: " + HASH_PREFIX);
            return false;
        }
        return true;
    }

    /**
     * Gets password strength description
     * @param password the password
     * @return the strength description string
     */
    public static String getPasswordStrength(String password) {
        int score = 0;

        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        boolean hasDigit = false;
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else hasSpecial = true;
        }

        if (hasDigit) score++;
        if (hasLower) score++;
        if (hasUpper) score++;
        if (hasSpecial) score++;

        if (score <= 2) return "Weak";
        if (score <= 4) return "Medium";
        return "Strong";
    }
}
