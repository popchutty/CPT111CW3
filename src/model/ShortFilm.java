package model;

/**
 * ShortFilm class - Short film
 * Typically movies shorter than 40 minutes
 */
public class ShortFilm extends Movie {
    
    private boolean isAnimated;
    private String filmFestival;

    /**
     * Constructor
     * @param id the movie ID
     * @param title the movie title
     * @param genre the movie genre
     * @param year the release year
     * @param rating the rating
     */
    public ShortFilm(String id, String title, String genre, int year, double rating) {
        super(id, title, genre, year, rating, Movie.TYPE_SHORT, 20);
    }
    
    /**
     * Full constructor
     * @param id the movie ID
     * @param title the movie title
     * @param genre the movie genre
     * @param year the release year
     * @param rating the rating
     * @param duration the duration in minutes
     * @param isAnimated whether it is an animated short film
     */
    public ShortFilm(String id, String title, String genre, int year, double rating,
                     int duration, boolean isAnimated) {
        super(id, title, genre, year, rating, Movie.TYPE_SHORT, duration);
        this.isAnimated = isAnimated;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }

    public String getFilmFestival() {
        return filmFestival;
    }

    public void setFilmFestival(String filmFestival) {
        this.filmFestival = filmFestival;
    }

    @Override
    public String toDetailedString() {
        String baseInfo = super.toDetailedString();
        String extra = "";
        if (isAnimated) {
            extra = extra + " | Animated";
        }
        if (filmFestival != null && !filmFestival.isEmpty()) {
            extra = extra + " | Festival: " + filmFestival;
        }
        return baseInfo + extra;
    }
}
