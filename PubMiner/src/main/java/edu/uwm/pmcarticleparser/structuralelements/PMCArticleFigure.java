package edu.uwm.pmcarticleparser.structuralelements;

/**
 * Represents a figure
 * @author agarwal
 */
public class PMCArticleFigure implements java.io.Serializable {
    private String caption;
    private String graphicLocation;
    private String id;
    private String label;

    /**
     * Creates a PMCArticleFigure
     * @param id the id of the figure in the article
     * @param label the lable of the figure, eg "Figure 1"
     * @param caption the caption of the figure
     * @param graphicLocation the graphicLocation, helps locate the file on
     * PMC
     */
    public PMCArticleFigure(String id, String label, String caption, String graphicLocation) {
        this.id = id;
        this.label = label;
        this.caption = caption;
        this.graphicLocation = graphicLocation;
    }

    /**
     * Creates an empty PMCArticleFigure object
     */
    public PMCArticleFigure() {
        this.id = "";
        this.label = "";
        this.caption = "";
        this.graphicLocation = "";
    }

    /**
     * Get the value of label
     *
     * @return the value of label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the value of label
     *
     * @param label new value of label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Get the value of graphicLocation
     *
     * @return the value of graphicLocation
     */
    public String getGraphicLocation() {
        return graphicLocation;
    }

    /**
     * Set the value of graphicLocation
     *
     * @param graphicLocation new value of graphicLocation
     */
    public void setGraphicLocation(String graphicLocation) {
        this.graphicLocation = graphicLocation;
    }

    /**
     * Returns the caption of this figure
     * @return caption of the figure as string
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption text of this article
     * @param caption the caption of this article
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
}
