package model;

/**
 * FeatureFilm class - Feature-length film
 * Typically movies longer than 40 minutes
 */
public class FeatureFilm extends Movie {

    private String director;
    private String[] mainCast;

    /**
     * Constructor
     * @param id the movie ID
     * @param title the movie title
     * @param genre the movie genre
     * @param year the release year
     * @param rating the rating
     */
    public FeatureFilm(String id, String title, String genre, int year, double rating) {
        super(id, title, genre, year, rating, Movie.TYPE_FEATURE, 120);
    }

    /**
     * Full constructor
     * @param id the movie ID
     * @param title the movie title
     * @param genre the movie genre
     * @param year the release year
     * @param rating the rating
     * @param duration the duration in minutes
     * @param director the director
     */
    public FeatureFilm(String id, String title, String genre, int year, double rating,
                       int duration, String director) {
        super(id, title, genre, year, rating, Movie.TYPE_FEATURE, duration);
        this.director = director;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String[] getMainCast() {
        return mainCast;
    }

    public void setMainCast(String[] mainCast) {
        this.mainCast = mainCast;
    }

    @Override
    public String toDetailedString() {
        String baseInfo = super.toDetailedString();
        if (director != null && !director.isEmpty()) {
            return baseInfo + " | Director: " + director;
        }
        return baseInfo;
    }
}
