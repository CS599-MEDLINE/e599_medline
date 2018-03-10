package edu.uwm.pmcarticleparser.structuralelements;

/**
 * Represents a reference
 * @author agarwal
 */
public class PMCArticleReference {
    private String id;
    private String text;

    /**
     * Creates an instance of PMCArticleReference
     * @param id the id of the reference in the article
     * @param text the text associated with this reference
     */
    public PMCArticleReference(String id, String text) {
        this.id = id;
        this.text = text;
    }

    /**
     * Creates an empty instance of PMCArticleReference
     */
    public PMCArticleReference() {
        id = "";
        text = "";
    }


    /**
     * Get the value of text
     *
     * @return the value of text
     */
    public String getText() {
        return text;
    }

    /**
     * Set the value of text
     *
     * @param text new value of text
     */
    public void setText(String text) {
        this.text = text;
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
}
