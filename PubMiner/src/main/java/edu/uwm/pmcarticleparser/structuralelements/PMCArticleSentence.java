package edu.uwm.pmcarticleparser.structuralelements;

import java.util.*;

import edu.stanford.nlp.simple.*;

/**
 * Represents a sentence in PMC article. A sentence has several properties,
 * including its text, the figures, tables and citations it refers to,
 * the index of this sentence in the article, the section this sentence appears
 * in, the subsection (if any) the sentence appears in.
 *
 * @author agarwal
 * @author gaob@github, add demographic scores to determine whether a sentence will likely contain demographic information.
 */
public class PMCArticleSentence {
    private String text;
    private int inParagraphIndex;
    private int totalSentencesInContainingParagraph;
    private int indexInDocument;
    private String sectionName;
    private String subSectionName;

    private Sentence stanfordSentence;
    private List<Optional<String>> dependencyLabels;
    private List<Integer> nummodIndices;
    private static final String NUMMOD = "nummod";
    private List<String> lemmas;

    // "women" and "men" are included because Stanford lemmatization sometimes doesn't reduce these plural forms to their singular forms.
    private static final Set<String> ANCHOR_WORDS = new HashSet<>(Arrays.asList("patient", "age", "aged", "male",
            "female", "subject", "individual", "woman", "man", "women", "men", "people"));
    private static final Set<String> EXCLUSION_NUMMOD = new HashSet<>(Arrays.asList("±", "1", "®", "one", "0"));

    private static final Map<String, Integer> keywordBase = new HashMap<>();
    static {
        keywordBase.put("patient", 5);
        keywordBase.put("year", 5);
        keywordBase.put("male", 5);
        keywordBase.put("female", 5);
        keywordBase.put("subject", 5);
        keywordBase.put("individual", 5);
        keywordBase.put("woman", 5);
        keywordBase.put("man", 5);
        keywordBase.put("women", 5);
        keywordBase.put("men", 5);
        keywordBase.put("people", 5);
    }

    private static final Map<String, Integer> keywordMax = new HashMap<>();
    static {
        keywordMax.put("patient", 1);
        keywordMax.put("year", 2);
        keywordMax.put("male", 1);
        keywordMax.put("female", 1);
        keywordMax.put("subject", 1);
        keywordMax.put("individual", 1);
        keywordMax.put("woman", 1);
        keywordMax.put("man", 1);
        keywordMax.put("women", 1);
        keywordMax.put("men", 1);
        keywordMax.put("people", 1);
    }

    /**
     * Creates an instance of PMCArticleSentence with the given text
     * @param text the text of this sentence
     */
    public PMCArticleSentence(String text) {
        this.text = text;
        inParagraphIndex = -1;
        sectionName = "No Section";
        subSectionName = "No Sub-section";

        stanfordSentence = new Sentence(text);
        lemmas = null;
        dependencyLabels = null;
        nummodIndices = null;
    }

    /**
     * Creates an empty instance of PMCArticleSentence
     */
    public PMCArticleSentence() {
        this("");
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
     * Gets the index of the sentence in the paragraph. By default, the index
     * starts at 1.
     * @return the index of the sentence in the paragraph
     */
    public int getInParagraphIndex() {
        return inParagraphIndex;
    }

    /**
     * Sets the value for index of the sentence in its paragraph.
     * @param inParagraphIndex the index of the sentence in its paragraph
     */
    public void setInParagraphIndex(int inParagraphIndex) {
        this.inParagraphIndex = inParagraphIndex;
    }

    /**
     * Gets the index of the sentence in the article
     * @return the index of the sentence in the article
     */
    public int getIndexInDocument() {
        return indexInDocument;
    }

    /**
     * Sets the index of the sentence in the article
     * @param indexInDocument the index of the sentence in the article
     */
    public void setIndexInDocument(int indexInDocument) {
        this.indexInDocument = indexInDocument;
    }

    /**
     * Gets the name of the section this sentence belongs to in the article
     * @return the section this sentence belongs to
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * Sets the section that this sentence belongs to
     * @param sectionName the section this sentence belongs to
     */
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    /**
     * Gets the name of the subsection this sentence belongs to in the article
     * @return the subsection this sentence belongs to
     */
    public String getSubSectionName() {
        return subSectionName;
    }

    /**
     * Sets the subsection that this sentence belongs to
     * @param subSectionName the subsection this sentence belongs to
     */
    public void setSubSectionName(String subSectionName) {
        this.subSectionName = subSectionName;
    }

    /**
     * Gets the total number of sentences in the paragraph that this
     * sentence belongs to
     * @return the total number of sentences in the paragraph that this
     * sentence belongs to
     */
    public int getTotalSentencesInContainingParagraph() {
        return totalSentencesInContainingParagraph;
    }

    /**
     * Gets the total number of sentences in the paragraph that this
     * sentence belongs to
     * @param totalSentencesInContainingParagraph the total number of
     * sentences in the paragraph that this sentence belongs to
     */
    public void setTotalSentencesInContainingParagraph(int totalSentencesInContainingParagraph) {
        this.totalSentencesInContainingParagraph = totalSentencesInContainingParagraph;
    }

    public Sentence getStanfordSentence() {
        return stanfordSentence;
    }

    public List<String> getLemmas() {
        if (lemmas==null) {
            lemmas = stanfordSentence.lemmas();
        }

        return lemmas;
    }

    public List<Optional<String>> getDependencyLabels() {
        populateDependencyFields();

        return dependencyLabels;
    }

    public int getNummodCount() {
        populateDependencyFields();

        return nummodIndices.size();
    }

    @Deprecated
    public int getDemographicScore() {
        int score = 0;

        Map<String, Integer> keywordCurrentMax = new HashMap<>();

        for (int index : getNummodIndices()) {
            String currentLemma = getLemmas().get(index + 1);
            Integer multiplier = keywordBase.get(currentLemma);
            if (multiplier==null) {
                score++;
            } else {
                Integer currentMax = keywordCurrentMax.get(currentLemma);
                if (currentMax==null) {
                    keywordCurrentMax.put(currentLemma, 1);
                    currentMax = 0;
                } else {
                    keywordCurrentMax.put(currentLemma, currentMax+1);
                }

                if (currentMax < keywordMax.get(currentLemma)) {
                    score += multiplier;
                }
            }
        }

        return score;
    }

    public Double getDemographicScoreBasedOnNumCounts(Map<String, Integer> numCounts) {
        double score = 0;

        Map<String, Integer> keywordCurrentMax = new HashMap<>();

        for (int index : getNummodIndices()) {
            String numLemma = getLemmas().get(index);
            Integer count = numCounts.get(numLemma);
            double multiplier = 1 + count/10.0;

            String baseLemma = getLemmas().get(index + 1);
            Integer base = keywordBase.get(baseLemma);
            if (base==null) {
                score++;
            } else {
                Integer currentMax = keywordCurrentMax.get(baseLemma);
                if (currentMax==null) {
                    keywordCurrentMax.put(baseLemma, 1);
                    currentMax = 0;
                } else {
                    keywordCurrentMax.put(baseLemma, currentMax+1);
                }

                if (currentMax < keywordMax.get(baseLemma)) {
                    score += multiplier * base;
                }
            }
        }

        return score;
    }

    public List<Integer> getNummodIndices() {
        populateDependencyFields();

        return nummodIndices;
    }

    private void populateDependencyFields() {
        if (dependencyLabels == null || nummodIndices == null) {
            dependencyLabels = stanfordSentence.incomingDependencyLabels();
            nummodIndices = new ArrayList<>();
            for (int index = 0; index < dependencyLabels.size(); index++) {
                Optional<String> labelOptional = dependencyLabels.get(index);
                if (labelOptional.isPresent() && labelOptional.get().equals(NUMMOD) && !EXCLUSION_NUMMOD.contains
                        (getLemmas
                        ().get(index)) && index < (getLemmas().size() - 1)) {
                    nummodIndices.add(index);
                }
            }
        }
    }

    public boolean hasAnchorWords() {
        for (String lemma : getLemmas()) {
            if (ANCHOR_WORDS.contains(lemma)) {
                return true;
            }
        }

        return false;
    }
}
