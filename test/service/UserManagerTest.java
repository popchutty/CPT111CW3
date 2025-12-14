package test.service;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import model.User;
import model.BasicUser;
import model.PremiumUser;
import service.UserManager;
import java.io.File;

/**
 * Unit tests for UserManager class
 */
public class UserManagerTest {
    
    private UserManager userManager;
    private static String TEST_USER_FILE = "data/test_users.csv";
    
    @Before
    public void setUp() {
        userManager = new UserManager(TEST_USER_FILE);
    }
    
    @After
    public void tearDown() {
        File file = new File(TEST_USER_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(userManager);
    }
    
    @Test
    public void testRegisterUser() {
        User registered = userManager.register("testuser", "password123", User.TYPE_BASIC);
        assertNotNull(registered);
        
        User user = userManager.getUser("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }
    
    @Test
    public void testRegisterDuplicateUser() {
        userManager.register("testuser", "password123", User.TYPE_BASIC);
        User duplicate = userManager.register("testuser", "password456", User.TYPE_BASIC);
        
        assertNull(duplicate);
    }
    
    @Test
    public void testRegisterBasicUser() {
        userManager.register("basic", "password", User.TYPE_BASIC);
        User user = userManager.getUser("basic");
        
        assertNotNull(user);
        assertTrue(user instanceof BasicUser);
        assertEquals(User.TYPE_BASIC, user.getUserType());
    }
    
    @Test
    public void testRegisterPremiumUser() {
        userManager.register("premium", "password", User.TYPE_PREMIUM);
        User user = userManager.getUser("premium");
        
        assertNotNull(user);
        assertTrue(user instanceof PremiumUser);
        assertEquals(User.TYPE_PREMIUM, user.getUserType());
    }
    
    @Test
    public void testLoginSuccess() {
        userManager.register("testuser", "password123", User.TYPE_BASIC);
        User user = userManager.login("testuser", "password123");
        
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }
    
    @Test
    public void testLoginFailWrongPassword() {
        userManager.register("testuser", "password123", User.TYPE_BASIC);
        User user = userManager.login("testuser", "wrongpassword");
        
        assertNull(user);
    }
    
    @Test
    public void testLoginFailUserNotFound() {
        User user = userManager.login("nonexistent", "password");
        assertNull(user);
    }
    
    @Test
    public void testGetUser() {
        userManager.register("testuser", "password123", User.TYPE_BASIC);
        User user = userManager.getUser("testuser");
        
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }
    
    @Test
    public void testGetUserNotFound() {
        User user = userManager.getUser("nonexistent");
        assertNull(user);
    }
    
    @Test
    public void testUserExists() {
        userManager.register("testuser", "password123", User.TYPE_BASIC);
        
        assertTrue(userManager.userExists("testuser"));
        assertFalse(userManager.userExists("nonexistent"));
    }
    
    @Test
    public void testSaveUsers() {
        userManager.register("user1", "password1", User.TYPE_BASIC);
        userManager.register("user2", "password2", User.TYPE_PREMIUM);
        
        boolean saved = userManager.saveUsers();
        assertTrue(saved);
        
        File file = new File(TEST_USER_FILE);
        assertTrue(file.exists());
    }
    
    @Test
    public void testLoadAndSaveUsers() {
        userManager.register("user1", "password1", User.TYPE_BASIC);
        userManager.register("user2", "password2", User.TYPE_PREMIUM);
        userManager.saveUsers();
        
        UserManager newManager = new UserManager(TEST_USER_FILE);
        boolean loaded = newManager.loadUsers();
        
        assertTrue(loaded);
        assertNotNull(newManager.getUser("user1"));
        assertNotNull(newManager.getUser("user2"));
    }
    
    @Test
    public void testGetUserCount() {
        assertEquals(0, userManager.getUserCount());
        
        userManager.register("user1", "password1", User.TYPE_BASIC);
        assertEquals(1, userManager.getUserCount());
        
        userManager.register("user2", "password2", User.TYPE_BASIC);
        assertEquals(2, userManager.getUserCount());
    }
    
    @Test
    public void testUpdateUser() {
        userManager.register("testuser", "oldpass123", User.TYPE_BASIC);
        User user = userManager.getUser("testuser");
        
        user.setPassword("newpass123");
        boolean updated = userManager.updateUser(user);
        
        assertTrue(updated);
    }
    
    @Test
    public void testUpgradeUserToPremium() {
        userManager.register("testuser", "password123", User.TYPE_BASIC);
        User user = userManager.getUser("testuser");
        User upgraded = userManager.upgradeToPremiuim(user);
        
        assertNotNull(upgraded);
        
        User retrievedUser = userManager.getUser("testuser");
        assertTrue(retrievedUser instanceof PremiumUser);
        assertEquals(User.TYPE_PREMIUM, retrievedUser.getUserType());
    }
    
    @Test
    public void testUpgradeNonExistentUser() {
        User user = userManager.getUser("nonexistent");
        assertNull(user);
    }
    
    @Test
    public void testLoadUsersFileNotExist() {
        UserManager manager = new UserManager("nonexistent/path.csv");
        boolean loaded = manager.loadUsers();
        assertFalse(loaded);
    }
    
    @Test
    public void testLoadUsersEmptyFile() throws Exception {
        java.io.PrintWriter writer = new java.io.PrintWriter(TEST_USER_FILE);
        writer.close();
        
        boolean loaded = userManager.loadUsers();
        assertFalse(loaded);
    }
    
    @Test
    public void testLoadUsersOldFormat() throws Exception {
        java.io.PrintWriter writer = new java.io.PrintWriter(TEST_USER_FILE);
        writer.println("username,password,watchlist,history");
        writer.println("olduser,plainpass,M001;M002,M003@2024-01-01");
        writer.close();
        
        boolean loaded = userManager.loadUsers();
        assertTrue(loaded);
        
        User user = userManager.getUser("olduser");
        assertNotNull(user);
        assertTrue(user instanceof BasicUser);
        assertEquals(2, user.getWatchlist().size());
        assertEquals(1, user.getHistory().size());
    }
    
    @Test
    public void testLoadUsersNewFormat() throws Exception {
        java.io.PrintWriter writer = new java.io.PrintWriter(TEST_USER_FILE);
        writer.println("username,password,usertype,watchlist,history");
        writer.println("basicuser,pass123,basic,M001,M002@2024-01-01");
        writer.println("premiumuser,pass456,premium,M003;M004,");
        writer.close();
        
        boolean loaded = userManager.loadUsers();
        assertTrue(loaded);
        assertEquals(2, userManager.getUserCount());
        
        User basic = userManager.getUser("basicuser");
        assertTrue(basic instanceof BasicUser);
        
        User premium = userManager.getUser("premiumuser");
        assertTrue(premium instanceof PremiumUser);
    }
    
    @Test
    public void testLoadUsersSkipEmptyLines() throws Exception {
        java.io.PrintWriter writer = new java.io.PrintWriter(TEST_USER_FILE);
        writer.println("username,password,usertype,watchlist,history");
        writer.println("user1,pass123,basic,,");
        writer.println("");
        writer.println("   ");
        writer.println("user2,pass456,premium,,");
        writer.close();
        
        boolean loaded = userManager.loadUsers();
        assertTrue(loaded);
        assertEquals(2, userManager.getUserCount());
    }
    
    @Test
    public void testLoadUsersInsufficientFields() throws Exception {
        java.io.PrintWriter writer = new java.io.PrintWriter(TEST_USER_FILE);
        writer.println("username,password,usertype,watchlist,history");
        writer.println("user1,pass123");
        writer.println("user2,pass456,basic,,");
        writer.close();
        
        boolean loaded = userManager.loadUsers();
        assertTrue(loaded);
        assertEquals(1, userManager.getUserCount());
    }
    
    @Test
    public void testRegisterWithShortUsername() {
        User user = userManager.register("ab", "password123", User.TYPE_BASIC);
        assertNull(user);
        assertEquals(0, userManager.getUserCount());
    }
    
    @Test
    public void testRegisterWithNullUsername() {
        User user = userManager.register(null, "password123", User.TYPE_BASIC);
        assertNull(user);
    }
    
    @Test
    public void testRegisterWithEmptyUsername() {
        User user = userManager.register("", "password123", User.TYPE_BASIC);
        assertNull(user);
        
        User user2 = userManager.register("   ", "password123", User.TYPE_BASIC);
        assertNull(user2);
    }
    
    @Test
    public void testRegisterWithShortPassword() {
        User user = userManager.register("testuser", "short", User.TYPE_BASIC);
        assertNull(user);
    }
    
    @Test
    public void testRegisterWithInvalidPassword() {
        User user = userManager.register("testuser", "$HASH$invalid", User.TYPE_BASIC);
        assertNull(user);
    }
    
    @Test
    public void testRegisterDefaultType() {
        User user = userManager.register("testuser", "password123");
        assertNotNull(user);
        assertTrue(user instanceof BasicUser);
        assertEquals(User.TYPE_BASIC, user.getUserType());
    }
    
    @Test
    public void testLoginWithPlaintextPassword() throws Exception {
        java.io.PrintWriter writer = new java.io.PrintWriter(TEST_USER_FILE);
        writer.println("username,password,usertype,watchlist,history");
        writer.println("olduser,plainpass,basic,,");
        writer.close();
        
        userManager.loadUsers();
        User user = userManager.login("olduser", "plainpass");
        
        assertNotNull(user);
        assertTrue(util.PasswordUtils.isHashedPassword(user.getPassword()));
    }
    
    @Test
    public void testChangePasswordSuccess() {
        userManager.register("testuser", "oldpass123", User.TYPE_BASIC);
        User user = userManager.getUser("testuser");
        
        boolean changed = userManager.changePassword(user, "oldpass123", "newpass456");
        assertTrue(changed);
    }
    
    @Test
    public void testChangePasswordWrongOldPassword() {
        userManager.register("testuser", "oldpass123", User.TYPE_BASIC);
        User user = userManager.getUser("testuser");
        
        boolean changed = userManager.changePassword(user, "wrongpass", "newpass456");
        assertFalse(changed);
    }
    
    @Test
    public void testChangePasswordInvalidNewPassword() {
        userManager.register("testuser", "oldpass123", User.TYPE_BASIC);
        User user = userManager.getUser("testuser");
        
        boolean changed = userManager.changePassword(user, "oldpass123", "short");
        assertFalse(changed);
    }
    
    @Test
    public void testChangePasswordSamePassword() {
        userManager.register("testuser", "password123", User.TYPE_BASIC);
        User user = userManager.getUser("testuser");
        
        boolean changed = userManager.changePassword(user, "password123", "password123");
        assertFalse(changed);
    }
    
    @Test
    public void testUpgradeAlreadyPremiumUser() {
        userManager.register("premium", "password123", User.TYPE_PREMIUM);
        User user = userManager.getUser("premium");
        
        User upgraded = userManager.upgradeToPremiuim(user);
        assertSame(user, upgraded);
        assertEquals(User.TYPE_PREMIUM, upgraded.getUserType());
    }
    
    @Test
    public void testUpgradePreservesData() {
        userManager.register("testuser", "password123", User.TYPE_BASIC);
        User user = userManager.getUser("testuser");
        
        user.addToWatchlist("M001");
        user.addToWatchlist("M002");
        user.markAsWatched("M003", "2024-01-01");
        
        User upgraded = userManager.upgradeToPremiuim(user);
        
        assertEquals(2, upgraded.getWatchlist().size());
        assertEquals(1, upgraded.getHistory().size());
        assertTrue(upgraded.getWatchlist().contains("M001"));
        assertTrue(upgraded.getHistory().contains("M003"));
    }
    
    @Test
    public void testUpdateUserNotExists() {
        User fakeUser = new BasicUser("nonexistent", "password123");
        boolean updated = userManager.updateUser(fakeUser);
        assertFalse(updated);
    }
    
    @Test
    public void testDeleteUserSuccess() {
        userManager.register("testuser", "password123", User.TYPE_BASIC);
        assertTrue(userManager.userExists("testuser"));
        
        boolean deleted = userManager.deleteUser("testuser");
        assertTrue(deleted);
        assertFalse(userManager.userExists("testuser"));
        assertEquals(0, userManager.getUserCount());
    }
    
    @Test
    public void testDeleteUserNotExists() {
        boolean deleted = userManager.deleteUser("nonexistent");
        assertFalse(deleted);
    }
    
    @Test
    public void testGetAllUsers() {
        userManager.register("user1", "password123", User.TYPE_BASIC);
        userManager.register("user2", "password456", User.TYPE_PREMIUM);
        userManager.register("user3", "password789", User.TYPE_BASIC);
        
        java.util.ArrayList<User> allUsers = userManager.getAllUsers();
        assertEquals(3, allUsers.size());
    }
    
    @Test
    public void testGetAllUsersEmpty() {
        java.util.ArrayList<User> allUsers = userManager.getAllUsers();
        assertEquals(0, allUsers.size());
    }
    
    @Test
    public void testGetPasswordStrength() {
        String weak = userManager.getPasswordStrength("abc");
        assertEquals("Weak", weak);
        
        String medium = userManager.getPasswordStrength("abcdefgh");
        assertEquals("Weak", medium);
        
        String strong = userManager.getPasswordStrength("Abcd1234!@#");
        assertEquals("Strong", strong);
    }
    
    @Test
    public void testSaveUsersMultiple() {
        userManager.register("user1", "password123", User.TYPE_BASIC);
        userManager.register("user2", "password456", User.TYPE_PREMIUM);
        
        User user1 = userManager.getUser("user1");
        user1.addToWatchlist("M001");
        user1.markAsWatched("M002", "2024-01-01");
        
        boolean saved = userManager.saveUsers();
        assertTrue(saved);
        
        UserManager newManager = new UserManager(TEST_USER_FILE);
        newManager.loadUsers();
        
        User loadedUser1 = newManager.getUser("user1");
        assertEquals(1, loadedUser1.getWatchlist().size());
        assertEquals(1, loadedUser1.getHistory().size());
    }
}
