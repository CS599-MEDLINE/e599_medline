package edu.uwm.pubmedparser;

/**
 * Represents a pubmed article
 * @author shashank
 */
public class PubmedArticle {
    private String title;
    private String abstractText;
    private String pmid;
    private String year;
    private String month;
    private String day;

    /**
     *
     * @param pmid
     * @param title
     * @param abstractText
     */
    public PubmedArticle(String pmid, String title, String abstractText) {
        this.pmid = pmid;
        this.title = title;
        this.abstractText = abstractText;
        year = "0000";
        month = "00";
        day = "00";
    }

    /**
     * Default constructor for PubmedArticle
     */
    public PubmedArticle() {
        this("", "", "");
    }

    /**
     * Gets the day of the month on which this article was published
     * @return
     */
    public String getDay() {
        return day;
    }

    /**
     * Set the day of the month on which this article was published
     * @param day
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     * Gets the month of the year in which this article was published
     * @return
     */
    public String getMonth() {
        return month;
    }

    /**
     * Sets the month of the year in which this article was published
     * @param month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Gets the year in which this article was published
     * @return
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the year in which this article was published
     * @param year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Gets the PMID of this article
     * @return
     */
    public String getPmid() {
        return pmid;
    }

    /**
     * Sets the PMID of this article
     * @param pmid
     */
    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    /**
     * Gets the text of this article's abstract
     * @return
     */
    public String getAbstractText() {
        return abstractText;
    }

    /**
     * Sets the text of this article's abstract
     * @param abstractText
     */
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    /**
     * Gets the title of this article
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this article
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(pmid);
        ret.append(": ").append(title).append("\n");
        ret.append("Published on: ").append(year).append(" ").append(month);
        return ret.toString();
    }
}
