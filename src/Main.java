import service.MovieManager;
import service.UserManager;
import service.RecommendationEngine;
import ui.CommandLineInterface;

/**
 * Main class - Program entry point
 * Responsible for initializing all services and starting the user interface
 */
public class Main {
    private static String MOVIE_FILE = "data/movies.csv";
    private static String USER_FILE = "data/users.csv";

    public static void main(String[] args) {
        System.out.println("Initializing Movie Recommendation System...\n");

        try {
            MovieManager movieManager = new MovieManager(MOVIE_FILE);
            if (!movieManager.loadMovies()) {
                System.out.println("Failed to load movies. Please check the movie file.");
                return;
            }

            UserManager userManager = new UserManager(USER_FILE);
            if (!userManager.loadUsers()) {
                System.out.println("Failed to load users. Please check the user file.");
                return;
            }

            RecommendationEngine recommendationEngine = new RecommendationEngine(movieManager);

            CommandLineInterface cli = new CommandLineInterface(
                movieManager,
                userManager,
                recommendationEngine
            );

            cli.start();

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
