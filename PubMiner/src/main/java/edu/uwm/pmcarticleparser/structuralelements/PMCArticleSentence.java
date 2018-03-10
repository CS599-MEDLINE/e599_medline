package edu.uwm.pmcarticleparser.structuralelements;

import generalutils.TermTokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents a sentence in PMC article. A sentence has several properties,
 * including its text, the figures, tables and citations it refers to,
 * the index of this sentence in the article, the section this sentence appears
 * in, the subsection (if any) the sentence appears in.
 * @author agarwal
 */
public class PMCArticleSentence {
    private String text;
    private String citationReplacedText;
    private boolean refersFigure;
    private boolean refersTable;
    private boolean refersCitation;
    protected List<String> referedFigureId;
    protected List<String> referedTableId;
    protected List<String> referedCitationId;
    private int inParagraphIndex;
    private int totalSentencesInContainingParagraph;
    private int indexInDocument;
    private String sectionName;
    private String subSectionName;

    private TermTokenizer termTokenizer;
    Set<String> wordSet;

    /**
     * Creates an instance of PMCArticleSentence with the given text
     * @param text the text of this sentence
     */
    public PMCArticleSentence(String text) {
        this.text = text;
        refersCitation = false;
        refersFigure = false;
        referedCitationId = new ArrayList<String>();
        referedFigureId = new ArrayList<String>();
        referedTableId = new ArrayList<String>();
        inParagraphIndex = -1;
        sectionName = "No Section";
        subSectionName = "No Sub-section";

        termTokenizer = new TermTokenizer();
        wordSet = termTokenizer.getTextWordSet(text);
    }

    /**
     * Creates an empty instance of PMCArticleSentence
     */
    public PMCArticleSentence() {
        this("");
    }

    /**
     * Get the value of refersCitation
     *
     * @return the value of refersCitation
     */
    public boolean isRefersCitation() {
        return refersCitation;
    }

    /**
     * Set the value of refersCitation
     *
     * @param refersCitation new value of refersCitation
     */
    public void setRefersCitation(boolean refersCitation) {
        this.refersCitation = refersCitation;
    }

    /**
     * Get the value of refersTable
     *
     * @return the value of refersTable
     */
    public boolean isRefersTable() {
        return refersTable;
    }

    /**
     * Set the value of refersTable
     *
     * @param refersTable new value of refersTable
     */
    public void setRefersTable(boolean refersTable) {
        this.refersTable = refersTable;
    }

    /**
     * Get the value of refersFigure
     *
     * @return the value of refersFigure
     */
    public boolean isRefersFigure() {
        return refersFigure;
    }

    /**
     * Set the value of refersFigure
     *
     * @param refersFigure new value of refersFigure
     */
    public void setRefersFigure(boolean refersFigure) {
        this.refersFigure = refersFigure;
    }

    /**
     * Get the value of citationReplacedText
     *
     * @return the value of citationReplacedText
     */
    public String getCitationReplacedText() {
        return citationReplacedText;
    }

    /**
     * Set the value of citationReplacedText
     *
     * @param citationReplacedText new value of citationReplacedText
     */
    public void setCitationReplacedText(String citationReplacedText) {
        this.citationReplacedText = citationReplacedText;
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
     * Gets the list of CitationIds referred by this sentence. If no citations 
     * are referred to by this sentence, then an empty list is returned.
     * @return the list of citations referred by this sentence
     */
    public List<String> getReferedCitationId() {
        return referedCitationId;
    }

    /**
     * Adds a citation that is referred by this sentence
     * @param referedCitationId the id of the citation referred by this sentence
     */
    public void addReferedCitationId(String referedCitationId) {
        this.referedCitationId.add(referedCitationId);
        refersCitation = true;
    }

    /**
     * Gets the list of FigureIds referred by this sentence. If no figures
     * are referred to by this sentence, then an empty list is returned.
     * @return the list of figures referred by this sentence
     */
    public List<String> getReferedFigureId() {
        return referedFigureId;
    }

    /**
     * Adds a figure that is referred by this sentence
     * @param referedFigureId the id of the figure referred by this sentence
     */
    public void addReferedFigureId(String referedFigureId) {
        this.referedFigureId.add(referedFigureId);
        refersFigure = true;
    }

    /**
     * Gets the list of TableIds referred by this sentence. If no tables 
     * are referred to by this sentence, then an empty list is returned.
     * @return the list of tables referred by this sentence
     */
    public List<String> getReferedTableId() {
        return referedTableId;
    }

    /**
     * Adds a table that is referred by this sentence
     * @param referedTableId the id of the table referred by this sentence
     */
    public void addReferedTableId(String referedTableId) {
        this.referedTableId.add(referedTableId);
        refersTable = true;
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

    public Set<String> getWordSet() {
        return wordSet;
    }
}
