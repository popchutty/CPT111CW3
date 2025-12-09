package test.util;

import org.junit.Test;
import static org.junit.Assert.*;

import util.PasswordUtils;

/**
 * Unit tests for PasswordUtils class
 */
public class PasswordUtilsTest {

    @Test
    public void testConstructor() {
        PasswordUtils utils = new PasswordUtils();
        assertNotNull(utils);
    }
    
    @Test
    public void testHashPassword() {
        String password = "mypassword123";
        String hashed = PasswordUtils.hashPassword(password);
        
        assertNotNull(hashed);
        assertNotEquals(password, hashed);
        assertTrue(hashed.startsWith("$HASH$"));
    }
    
    @Test
    public void testHashPasswordConsistency() {
        String password = "testpass";
        String hash1 = PasswordUtils.hashPassword(password);
        String hash2 = PasswordUtils.hashPassword(password);
        
        // Same password should produce same hash
        assertEquals(hash1, hash2);
    }
    
    @Test
    public void testHashPasswordDifferent() {
        String pass1 = "password1";
        String pass2 = "password2";
        
        String hash1 = PasswordUtils.hashPassword(pass1);
        String hash2 = PasswordUtils.hashPassword(pass2);
        
        // Different passwords should produce different hashes
        assertNotEquals(hash1, hash2);
    }
    
    @Test
    public void testHashPasswordEmpty() {
        String hashed = PasswordUtils.hashPassword("");
        assertEquals("", hashed);
    }
    
    @Test
    public void testHashPasswordNull() {
        String hashed = PasswordUtils.hashPassword(null);
        assertEquals("", hashed);
    }
    
    @Test
    public void testVerifyPasswordHashed() {
        String password = "mypassword";
        String hashed = PasswordUtils.hashPassword(password);
        
        assertTrue(PasswordUtils.verifyPassword(password, hashed));
        assertFalse(PasswordUtils.verifyPassword("wrongpassword", hashed));
    }
    
    @Test
    public void testVerifyPasswordPlaintext() {
        // Test backward compatibility with plaintext passwords
        String password = "plainpass";
        
        assertTrue(PasswordUtils.verifyPassword(password, password));
        assertFalse(PasswordUtils.verifyPassword("wrong", password));
    }
    
    @Test
    public void testVerifyPasswordNull() {
        assertFalse(PasswordUtils.verifyPassword(null, "something"));
        assertFalse(PasswordUtils.verifyPassword("something", null));
        assertFalse(PasswordUtils.verifyPassword(null, null));
    }
    
    @Test
    public void testIsHashedPassword() {
        String hashed = PasswordUtils.hashPassword("password");
        assertTrue(PasswordUtils.isHashedPassword(hashed));
        
        assertFalse(PasswordUtils.isHashedPassword("plaintext"));
        assertFalse(PasswordUtils.isHashedPassword(""));
        assertFalse(PasswordUtils.isHashedPassword(null));
    }
    
    @Test
    public void testNeedsUpgrade() {
        String plaintext = "oldpassword";
        assertTrue(PasswordUtils.needsUpgrade(plaintext));
        
        String hashed = PasswordUtils.hashPassword("newpassword");
        assertFalse(PasswordUtils.needsUpgrade(hashed));
        
        assertFalse(PasswordUtils.needsUpgrade(null));
    }
    
    @Test
    public void testPasswordLength() {
        // Test that passwords of different lengths can be hashed
        String shortPass = PasswordUtils.hashPassword("abc");
        String longPass = PasswordUtils.hashPassword("verylongpassword123");
        
        assertNotNull(shortPass);
        assertNotNull(longPass);
        assertNotEquals(shortPass, longPass);
    }
    
    @Test
    public void testHashDifferentLengthPasswords() {
        String short1 = PasswordUtils.hashPassword("ab");
        String long1 = PasswordUtils.hashPassword("a".repeat(100));
        
        assertNotEquals(short1, long1);
        assertTrue(short1.startsWith("$HASH$"));
        assertTrue(long1.startsWith("$HASH$"));
    }
    
    @Test
    public void testHashSpecialCharacters() {
        String special = "P@ssw0rd!#$%";
        String hashed = PasswordUtils.hashPassword(special);
        
        assertNotNull(hashed);
        assertTrue(PasswordUtils.verifyPassword(special, hashed));
    }
    
    @Test
    public void testCaseSensitivity() {
        String lower = "password";
        String upper = "PASSWORD";
        
        String hashLower = PasswordUtils.hashPassword(lower);
        String hashUpper = PasswordUtils.hashPassword(upper);
        
        assertNotEquals(hashLower, hashUpper);
        assertFalse(PasswordUtils.verifyPassword(lower, hashUpper));
        assertFalse(PasswordUtils.verifyPassword(upper, hashLower));
    }

    @Test
    public void testIsValidPassword() {
        assertTrue(PasswordUtils.isValidPassword("validPass123"));
        assertFalse(PasswordUtils.isValidPassword("short"));
        assertFalse(PasswordUtils.isValidPassword(null));
        assertFalse(PasswordUtils.isValidPassword("$HASH$invalid"));
    }

    @Test
    public void testGetPasswordStrengthWeak() {
        // Weak passwords (score <= 2)
        assertEquals("Weak", PasswordUtils.getPasswordStrength("abc"));
        assertEquals("Weak", PasswordUtils.getPasswordStrength("123"));
        assertEquals("Weak", PasswordUtils.getPasswordStrength("abcdef"));
        assertEquals("Weak", PasswordUtils.getPasswordStrength("123456"));
    }

    @Test
    public void testGetPasswordStrengthMedium() {
        assertEquals("Medium", PasswordUtils.getPasswordStrength("abc12345"));
        assertEquals("Medium", PasswordUtils.getPasswordStrength("Abcdefgh"));
        assertEquals("Medium", PasswordUtils.getPasswordStrength("abcdefghijklm"));
    }

    @Test
    public void testGetPasswordStrengthStrong() {
        assertEquals("Strong", PasswordUtils.getPasswordStrength("Abcd1234!@#"));
        assertEquals("Strong", PasswordUtils.getPasswordStrength("Password123!"));
        assertEquals("Strong", PasswordUtils.getPasswordStrength("MyP@ssw0rd2024"));
        assertEquals("Strong", PasswordUtils.getPasswordStrength("Secure#Pass123"));
    }

    @Test
    public void testGetPasswordStrengthLengthBoundaries() {
        String len7 = "Abc123!";
        String len8 = "Abc1234!";
        String len12 = "Abc1234!@#$%";
        
        assertNotNull(PasswordUtils.getPasswordStrength(len7));
        assertNotNull(PasswordUtils.getPasswordStrength(len8));
        assertNotNull(PasswordUtils.getPasswordStrength(len12));
    }

    @Test
    public void testGetPasswordStrengthCharacterTypes() {
        assertEquals("Weak", PasswordUtils.getPasswordStrength("lowercase"));
        assertEquals("Weak", PasswordUtils.getPasswordStrength("UPPERCASE"));
        assertEquals("Weak", PasswordUtils.getPasswordStrength("12345678"));
        assertEquals("Weak", PasswordUtils.getPasswordStrength("!@#$%^&*"));
        
        String lowerUpper = "aBcDeFgH";
        String lowerDigit = "abcd1234";
        String upperDigit = "ABCD1234";
        
        assertEquals("Medium", PasswordUtils.getPasswordStrength(lowerUpper));
        assertEquals("Medium", PasswordUtils.getPasswordStrength(lowerDigit));
        assertEquals("Medium", PasswordUtils.getPasswordStrength(upperDigit));
    }

    @Test
    public void testGetPasswordStrengthAllCharacterTypes() {
        String allTypes8 = "Aa1!";
        String allTypes12 = "Aa1!Aa1!Aa1!";
        
        assertNotNull(PasswordUtils.getPasswordStrength(allTypes8));
        assertEquals("Strong", PasswordUtils.getPasswordStrength(allTypes12));
    }

    @Test
    public void testGetPasswordStrengthSpecialCharacters() {
        assertEquals("Strong", PasswordUtils.getPasswordStrength("Pass123!@#$%"));
        assertEquals("Strong", PasswordUtils.getPasswordStrength("Pass123^&*()"));
        assertEquals("Strong", PasswordUtils.getPasswordStrength("Pass123-_=+[]"));
        assertEquals("Strong", PasswordUtils.getPasswordStrength("Pass123{}|;:"));
    }

    @Test
    public void testGetPasswordStrengthEdgeCases() {
        assertEquals("Weak", PasswordUtils.getPasswordStrength("a"));
        assertEquals("Weak", PasswordUtils.getPasswordStrength("A1"));
        
        assertEquals("Medium", PasswordUtils.getPasswordStrength("a".repeat(20)));
        
        assertEquals("Strong", PasswordUtils.getPasswordStrength("Abcd1234!@#$%^&*()"));
    }

    @Test
    public void testGetPasswordStrengthScoring() {
        String score2 = "abcdefgh";
        assertEquals("Weak", PasswordUtils.getPasswordStrength(score2));
        
        String score3 = "abcdefg1";
        assertEquals("Medium", PasswordUtils.getPasswordStrength(score3));
        
        String score4 = "Abcdefg1";
        assertEquals("Medium", PasswordUtils.getPasswordStrength(score4));
        
        String score5 = "Abcdefg1!";
        assertEquals("Strong", PasswordUtils.getPasswordStrength(score5));
        
        String score7 = "Abcdefghijkl1!";
        assertEquals("Strong", PasswordUtils.getPasswordStrength(score7));
    }
}
