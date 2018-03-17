package edu.uwm.pmcarticleparser.structuralelements;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author agarwal
 */
public class PMCArticleFullText {
    private List<PMCArticleSentence> fullTextSentences;

    /**
     * Creates an instance of PMCArticleFullText
     */
    public PMCArticleFullText() {
        fullTextSentences = new ArrayList<>();
    }

    /**
     * Adds a sentence to the list of full text sentences
     * @param sentence sentence to add
     */
    public void addSentence(PMCArticleSentence sentence) {
        fullTextSentences.add(sentence);
    }

    /**
     * Getter for the list of full text sentences
     * @return the list of sentences
     */
    public List<PMCArticleSentence> getFullTextSentences() {
        return fullTextSentences;
    }
}
