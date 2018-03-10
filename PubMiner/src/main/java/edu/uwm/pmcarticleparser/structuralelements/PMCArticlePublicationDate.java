package edu.uwm.pmcarticleparser.structuralelements;

/**
 * Date of article publication
 * @author agarwal
 */
public class PMCArticlePublicationDate {
    private String day;
    private String month;
    private String year;

    /**
     * Constructs a PMCArticlePublicationDate object with the given date
     * information
     * @param day the day of the month on which article was published
     * @param month the month in which the article was published
     * @param year the year in which the article was published
     */
    public PMCArticlePublicationDate(String day, String month, String year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    /**
     * Get the value of day
     *
     * @return the value of day
     */
    public String getDay() {
        return day;
    }

    /**
     * Set the value of day
     *
     * @param day new value of day
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     * Get the value of month
     *
     * @return the value of month
     */
    public String getMonth() {
        return month;
    }

    /**
     * Set the value of month
     *
     * @param month new value of month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Get the value of year
     *
     * @return the value of year
     */
    public String getYear() {
        return year;
    }

    /**
     * Set the value of year
     *
     * @param year new value of year
     */
    public void setYear(String year) {
        this.year = year;
    }
}
