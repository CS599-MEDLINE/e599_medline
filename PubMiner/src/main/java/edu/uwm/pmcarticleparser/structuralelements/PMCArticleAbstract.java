package edu.uwm.pmcarticleparser.structuralelements;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract
 * @author agarwal
 * @author gaob@github
 */
public class PMCArticleAbstract {
    /**
     * List of sentences in the abstract
     */
    private List<PMCArticleSentence> abstractSentences;

    /**
     * Default constructor of the class
     */
    public PMCArticleAbstract() {
        abstractSentences = new ArrayList<>();
    }

    /**
     * Adds a sentence to the abstract sentences.
     * @param sentence the sentence to add
     */
    public void addSentence(PMCArticleSentence sentence) {
        abstractSentences.add(sentence);
    }

    /**
     * Gets the list of abstract sentences.
     * @return a list of sentences
     */
    public List<PMCArticleSentence> getAbstractSentences() {
        return abstractSentences;
    }
}
