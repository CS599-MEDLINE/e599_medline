package edu.uwm.pmcarticleparser.structuralelements;

/**
 * Author of an article
 * @author agarwal
 */
public class PMCArticleAuthor {
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Constructs an instance of PMCArticleAuthor with the given firstName,
     * middleName and lastName.
     * @param firstName the first name of the author
     * @param lastName the last name of the author
     */
    public PMCArticleAuthor(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        email = "";
    }

    /**
     * Default constructor for PMCArticleAuthor.
     */
    public PMCArticleAuthor() {
        this.firstName = "";
        this.lastName = "";
    }

    /**
     * Gets the first name of the author
     * @return first name of the author
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the author
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the author
     * @return last name of the author
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the author
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email of the author.
     * @return the email of the author
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the author
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
