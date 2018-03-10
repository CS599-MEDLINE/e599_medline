package edu.uwm.pmcarticleparser.structuralelements;

/**
 * Represents a table in the article
 * @author agarwal
 */
public class PMCArticleTable {
    private String id;
    private String label;
    private String caption;

    /**
     * Creates an instances of PMCArticleTable
     * @param id id of the table
     * @param label label used in the article for this table, eg. "Table 1"
     * @param caption the caption or legend of the table
     */
    public PMCArticleTable(String id, String label, String caption) {
        this.id = id;
        this.label = label;
        this.caption = caption;
    }

    /**
     * Creates an empty instance of PMCArticleTable
     */
    public PMCArticleTable() {
        this.id = "";
        this.label = "";
        this.caption = "";
    }

    /**
     * Gets the table caption
     * @return the table caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the table
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Gets the id of the table
     * @return the id of the table
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the table
     * @param id the id of the table to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the label of the table
     * @return the label of the table
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the table's label
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
