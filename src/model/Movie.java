package model;

/**
 * Movie class - Movie base class
 * Contains all basic movie information
 * Supports subclassing (FeatureFilm, ShortFilm)
 */
public class Movie {
    public static String TYPE_FEATURE = "feature";
    public static String TYPE_SHORT = "short";

    protected String id;
    protected String title;
    protected String genre;
    protected int year;
    protected double rating;
    protected String movieType;
    protected int duration;

    /**
     * Constructor
     * @param id the movie ID
     * @param title the movie title
     * @param genre the movie genre
     * @param year the release year
     * @param rating the rating (0.0-10.0)
     */
    public Movie(String id, String title, String genre, int year, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
        this.movieType = TYPE_FEATURE;
        this.duration = 120;
    }

    /**
     * Full constructor
     * @param id the movie ID
     * @param title the movie title
     * @param genre the movie genre
     * @param year the release year
     * @param rating the rating
     * @param movieType the movie type (feature/short)
     * @param duration the duration in minutes
     */
    public Movie(String id, String title, String genre, int year, double rating, 
                 String movieType, int duration) {
        this(id, title, genre, year, rating);
        this.movieType = movieType;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public double getRating() {
        return rating;
    }

    public String getMovieType() {
        return movieType;
    }

    public int getDuration() {
        return duration;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Checks if it is a feature film
     * @return true if it is a feature film
     */
    public boolean isFeatureFilm() {
        return TYPE_FEATURE.equals(movieType) || duration >= 40;
    }

    /**
     * Checks if it is a short film
     * @return true if it is a short film
     */
    public boolean isShortFilm() {
        return TYPE_SHORT.equals(movieType) || duration < 40;
    }

    /**
     * Gets the movie type display name
     * @return the display name
     */
    public String getMovieTypeDisplayName() {
        if (isShortFilm()) {
            return "Short Film";
        }
        return "Feature Film";
    }

    /**
     * Gets the formatted duration string
     * @return the formatted duration
     */
    public String getFormattedDuration() {
        int hours = duration / 60;
        int minutes = duration % 60;
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        }
        return String.format("%dm", minutes);
    }

    /**
     * Overrides toString method for displaying movie information
     */
    @Override
    public String toString() {
        return String.format("[%s] %s (%d) - %s | Rating: %.1f/10.0",
                            id, title, year, genre, rating);
    }

    /**
     * Detailed toString
     * @return the detailed information string
     */
    public String toDetailedString() {
        return String.format("[%s] %s (%d) - %s | Rating: %.1f/10.0 | %s | %s",
                            id, title, year, genre, rating, getMovieTypeDisplayName(), getFormattedDuration());
    }

    /**
     * Overrides equals method for comparing two movie objects
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movie movie = (Movie) obj;
        return id.equals(movie.id);
    }

    /**
     * Overrides hashCode method
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

