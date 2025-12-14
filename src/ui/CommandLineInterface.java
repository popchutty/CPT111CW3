package ui;

import model.Movie;
import model.User;
import service.MovieManager;
import service.UserManager;
import service.RecommendationEngine;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * CommandLineInterface class - Command-line user interface
 * Handles all user interactions including main menu and user menu
 */
public class CommandLineInterface {
    private Scanner scanner;
    private MovieManager movieManager;
    private UserManager userManager;
    private RecommendationEngine recommendationEngine;
    private User currentUser;

    /**
     * Constructor
     * @param movieManager the movie manager
     * @param userManager the user manager
     * @param recommendationEngine the recommendation engine
     */
    public CommandLineInterface(MovieManager movieManager, UserManager userManager, 
                               RecommendationEngine recommendationEngine) {
        this.scanner = new Scanner(System.in);
        this.movieManager = movieManager;
        this.userManager = userManager;
        this.recommendationEngine = recommendationEngine;
        this.currentUser = null;
    }

    /**
     * Starts the user interface
     */
    public void start() {
        System.out.println("------------------------------------------");
        System.out.println("  Welcome to Movie Recommendation System  ");
        System.out.println("------------------------------------------");
        
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showMainMenu();
            } else {
                running = showUserMenu();
            }
        }
        
        System.out.println("\nThank you for using Movie Recommendation System!");
        scanner.close();
    }

    /**
     * Shows the main menu (not logged in)
     * @return true to continue running, false to exit
     */
    private boolean showMainMenu() {
        System.out.println("\n---------- Main Menu ----------");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Please select an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        return true;
    }

    /**
     * Shows the user menu (logged in)
     * @return true to continue running, false to exit
     */
    private boolean showUserMenu() {
        System.out.println("\n---------- User Menu (" + currentUser.getUserTypeDisplayName() + 
                         " - " + currentUser.getUsername() + ") ----------");
        System.out.println("1. Browse movies");
        System.out.println("2. Add movie to watchlist");
        System.out.println("3. Remove movie from watchlist");
        System.out.println("4. View watchlist");
        System.out.println("5. Mark movie as watched");
        System.out.println("6. View history");
        System.out.println("7. Get recommendations");
        System.out.println("8. Change recommendation strategy");
        System.out.println("9. Change password");
        System.out.println("10. View account info");
        System.out.println("11. Logout");
        System.out.print("Please select an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    browseMovies();
                    break;
                case 2:
                    addToWatchlist();
                    break;
                case 3:
                    removeFromWatchlist();
                    break;
                case 4:
                    viewWatchlist();
                    break;
                case 5:
                    markAsWatched();
                    break;
                case 6:
                    viewHistory();
                    break;
                case 7:
                    getRecommendations();
                    break;
                case 8:
                    changeRecommendationStrategy();
                    break;
                case 9:
                    changePassword();
                    break;
                case 10:
                    viewAccountInfo();
                    break;
                case 11:
                    handleLogout();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }

        return true;
    }

    /**
     * Handles user login
     */
    private void handleLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = userManager.login(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful! Welcome, " + username + "!");
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }
    }

    /**
     * Handles user logout
     */
    private void handleLogout() {
        userManager.updateUser(currentUser);
        System.out.println("Logged out successfully. Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }
    
    /**
     * Handles user registration
     */
    private void handleRegister() {
        System.out.println("\n--- Register New Account ---");
        System.out.print("Username (min 3 characters): ");
        String username = scanner.nextLine().trim();
        
        if (userManager.userExists(username)) {
            System.out.println("Username already exists. Please try another one.");
            return;
        }
        
        System.out.print("Password (min 6 characters): ");
        String password = scanner.nextLine().trim();
        
        String strength = userManager.getPasswordStrength(password);
        System.out.println("Password strength: " + strength);
        
        if (!util.PasswordUtils.isValidPassword(password)) {
            return;
        }
        
        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine().trim();
        
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }
        
        System.out.println("\nSelect account type:");
        System.out.println("1. Basic (Free - Watchlist limit: 10, Recommendations: 5)");
        System.out.println("2. Premium (Watchlist limit: 100, Recommendations: 20, Advanced features)");
        System.out.print("Choice (default: 1): ");
        
        String userType = model.User.TYPE_BASIC;
        try {
            String choice = scanner.nextLine().trim();
            if (!choice.isEmpty() && Integer.parseInt(choice) == 2) {
                userType = model.User.TYPE_PREMIUM;
            }
        } catch (NumberFormatException e) {
        }
        
        model.User newUser = userManager.register(username, password, userType);
        if (newUser != null) {
            System.out.println("\n Registration successful!");
            System.out.println("Account type: " + newUser.getUserTypeDisplayName());
            System.out.println("You can now login with your credentials.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    /**
     * Browse all movies
     */
    private void browseMovies() {
        System.out.println("\n--- Browse Movies ---");
        ArrayList<Movie> movies = movieManager.getAllMovies();
        
        if (movies.isEmpty()) {
            System.out.println("No movies available.");
            return;
        }

        System.out.println("Total movies: " + movies.size());
        System.out.println();
        
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }

    /**
     * Add movie to watchlist
     */
    private void addToWatchlist() {
        System.out.println("\n--- Add Movie to Watchlist ---");
        
        if (currentUser.getWatchlist().size() >= currentUser.getMaxWatchlistSize()) {
            System.out.println("Your watchlist is full! (" + currentUser.getMaxWatchlistSize() + " movies max)");
            if (currentUser.getUserType().equals(model.User.TYPE_BASIC)) {
                System.out.println("Tip: Upgrade to Premium for larger watchlist (100 movies)!");
            }
            return;
        }
        
        System.out.print("Enter movie ID: ");
        String movieId = scanner.nextLine().trim().toUpperCase();

        if (!movieManager.movieExists(movieId)) {
            System.out.println("Movie not found with ID: " + movieId);
            return;
        }

        if (currentUser.getHistory().contains(movieId)) {
            System.out.println("You have already watched this movie.");
            return;
        }

        if (currentUser.addToWatchlist(movieId)) {
            Movie movie = movieManager.getMovieById(movieId);
            System.out.println("Added to watchlist: " + movie.getTitle());
            System.out.println("Watchlist: " + currentUser.getWatchlist().size() + 
                             "/" + currentUser.getMaxWatchlistSize());
            userManager.updateUser(currentUser);
        } else {
            System.out.println("Movie is already in your watchlist or watchlist is full.");
        }
    }

    /**
     * Removes movie from watchlist
     */
    private void removeFromWatchlist() {
        System.out.println("\n--- Remove Movie from Watchlist ---");
        
        if (currentUser.getWatchlist().isEmpty()) {
            System.out.println("Your watchlist is empty.");
            return;
        }

        System.out.print("Enter movie ID: ");
        String movieId = scanner.nextLine().trim().toUpperCase();

        if (currentUser.removeFromWatchlist(movieId)) {
            Movie movie = movieManager.getMovieById(movieId);
            if (movie != null) {
                System.out.println("Removed from watchlist: " + movie.getTitle());
            } else {
                System.out.println("Movie removed from watchlist.");
            }
            userManager.updateUser(currentUser);
        } else {
            System.out.println("Movie not found in your watchlist.");
        }
    }

    /**
     * Views watchlist
     */
    private void viewWatchlist() {
        System.out.println("\n--- Your Watchlist ---");
        
        ArrayList<String> movieIds = currentUser.getWatchlist().getMovieIds();
        
        if (movieIds.isEmpty()) {
            System.out.println("Your watchlist is empty.");
            return;
        }

        System.out.println("Total movies in watchlist: " + movieIds.size());
        System.out.println();
        
        for (String movieId : movieIds) {
            Movie movie = movieManager.getMovieById(movieId);
            if (movie != null) {
                System.out.println(movie);
            }
        }
    }

    /**
     * Marks movie as watched
     */
    private void markAsWatched() {
        System.out.println("\n--- Mark Movie as Watched ---");
        System.out.print("Enter movie ID: ");
        String movieId = scanner.nextLine().trim().toUpperCase();

        if (!movieManager.movieExists(movieId)) {
            System.out.println("Movie not found with ID: " + movieId);
            return;
        }

        if (currentUser.getHistory().contains(movieId)) {
            System.out.println("You have already marked this movie as watched.");
            return;
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = today.format(formatter);

        currentUser.markAsWatched(movieId, dateStr);
        
        Movie movie = movieManager.getMovieById(movieId);
        System.out.println("Marked as watched: " + movie.getTitle());
        System.out.println("Watch date: " + dateStr);
        
        userManager.updateUser(currentUser);
    }

    /**
     * Views watch history
     */
    private void viewHistory() {
        System.out.println("\n--- Your Watch History ---");
        
        ArrayList<String> movieIds = currentUser.getHistory().getMovieIds();
        
        if (movieIds.isEmpty()) {
            System.out.println("Your watch history is empty.");
            return;
        }

        System.out.println("Total movies watched: " + movieIds.size());
        System.out.println();
        
        for (String movieId : movieIds) {
            Movie movie = movieManager.getMovieById(movieId);
            String watchDate = currentUser.getHistory().getWatchDate(movieId);
            if (movie != null) {
                System.out.println(movie + " | Watched on: " + watchDate);
            }
        }
    }

    /**
     * Gets recommended movies
     */
    private void getRecommendations() {
        System.out.println("\n--- Movie Recommendations ---");
        System.out.println("Current strategy: " + recommendationEngine.getCurrentStrategy().getName());
        System.out.println("(" + recommendationEngine.getCurrentStrategy().getDescription() + ")");
        
        System.out.print("\nHow many recommendations do you want? (default: 5, max: " 
                       + currentUser.getMaxRecommendations() + "): ");
        
        int topN = 5;
        try {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                topN = Integer.parseInt(input);
                if (topN <= 0) {
                    System.out.println("Invalid number. Using default: 5");
                    topN = 5;
                }
                if (topN > currentUser.getMaxRecommendations()) {
                    System.out.println("Exceeded your limit. Using max: " + currentUser.getMaxRecommendations());
                    topN = currentUser.getMaxRecommendations();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default: 5");
        }

        ArrayList<Movie> recommendations = recommendationEngine.getRecommendations(currentUser, topN);
        
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available at the moment.");
            System.out.println("Tip: Watch some movies first to get personalized recommendations!");
            return;
        }

        System.out.println("\nTop " + recommendations.size() + " recommended movies for you:");
        System.out.println();
        
        for (int i = 0; i < recommendations.size(); i++) {
            System.out.println((i + 1) + ". " + recommendations.get(i));
        }
    }
    
    /**
     * Changes recommendation strategy
     */
    private void changeRecommendationStrategy() {
        System.out.println("\n--- Change Recommendation Strategy ---");
        
        ArrayList<service.RecommendationStrategy> availableStrategies = 
            recommendationEngine.getAvailableStrategiesForUser(currentUser);
        
        if (availableStrategies.isEmpty()) {
            System.out.println("No strategies available.");
            return;
        }
        
        System.out.println("Available strategies:");
        for (int i = 0; i < availableStrategies.size(); i++) {
            service.RecommendationStrategy strategy = availableStrategies.get(i);
            String current = strategy.equals(recommendationEngine.getCurrentStrategy()) ? " [CURRENT]" : "";
            String premium = strategy.requiresPremium() ? " [Premium]" : "";
            System.out.println((i + 1) + ". " + strategy.getName() + premium + current);
            System.out.println("   " + strategy.getDescription());
        }
        
        System.out.print("\nSelect strategy (1-" + availableStrategies.size() + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= availableStrategies.size()) {
                service.RecommendationStrategy selected = availableStrategies.get(choice - 1);
                recommendationEngine.setStrategy(selected);
                System.out.println("Strategy changed to: " + selected.getName());
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    /**
     * Changes password
     */
    private void changePassword() {
        System.out.println("\n--- Change Password ---");
        
        System.out.print("Current password: ");
        String oldPassword = scanner.nextLine().trim();
        
        if (!util.PasswordUtils.verifyPassword(oldPassword, currentUser.getPassword())) {
            System.out.println("Current password is incorrect.");
            return;
        }
        
        System.out.print("New password (min 6 characters): ");
        String newPassword = scanner.nextLine().trim();
        
        if (newPassword.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return;
        }
        
        String strength = userManager.getPasswordStrength(newPassword);
        System.out.println("Password strength: " + strength);
        
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match.");
            return;
        }
        
        if (userManager.changePassword(currentUser, oldPassword, newPassword)) {
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password. Please check:");
            System.out.println("- Current password is correct");
            System.out.println("- New password is at least 6 characters");
            System.out.println("- New password is different from current password");
        }
    }
    
    /**
     * Views account information
     */
    private void viewAccountInfo() {
        System.out.println("\n========== Account Information ==========");
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Account Type: " + currentUser.getUserTypeDisplayName());
        System.out.println("Watchlist: " + currentUser.getWatchlist().size() + 
                         "/" + currentUser.getMaxWatchlistSize() + " movies");
        System.out.println("Watch History: " + currentUser.getHistory().size() + " movies");
        System.out.println("Max Recommendations: " + currentUser.getMaxRecommendations());
        System.out.println("Advanced Features: " + 
                         (currentUser.canUseAdvancedRecommendations() ? "Yes" : "No"));
        
        if (currentUser.getUserType().equals(model.User.TYPE_BASIC)) {
            System.out.println("\n" + ((model.BasicUser)currentUser).getUpgradeHint());
        } else if (currentUser.getUserType().equals(model.User.TYPE_PREMIUM)) {
            System.out.println("\n" + ((model.PremiumUser)currentUser).getPremiumBenefits());
        }
    }
}
