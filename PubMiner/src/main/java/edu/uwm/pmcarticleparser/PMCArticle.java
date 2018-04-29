package edu.uwm.pmcarticleparser;

import edu.uwm.pmcarticleparser.structuralelements.*;
import generalutils.SentenceTokenizer;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a PMC article
 *
 * https://orbit.nlm.nih.gov/browse-repository/software/other/44-pmc-parser
 *
 * @author Shashank Agarwal
 * @author gaob@github, extend the class to read from pubmed efetch API and InputStream (such as from AWS S3 bucket).
 */
public class PMCArticle {
    public static final String TITLE_XPATH = "//title-group/article-title";
    public static final String JOURNAL_XPATH = "//journal-title";
    public static final String AUTHORS_XPATH = "//contrib-group/contrib[@contrib-type='author']";
    public static final String ABSTRACT_XPATH = "//abstract";
    public static final String PUBLICATION_DATE_DAY_XPATH = "//pub-date/day";
    public static final String PUBLICATION_DATE_MONTH_XPATH = "//pub-date/month";
    public static final String PUBLICATION_DATE_YEAR_XPATH = "//pub-date/year";
    public static final String PMC_ID_XPATH = "//article-id[@pub-id-type='pmc']";
    public static final String PUBMED_ID_XPATH = "//article-id[@pub-id-type='pmid']";
    public static final String VOLUME_XPATH = "//volume";
    public static final String FIRST_PAGE_XPATH = "//fpage";
    public static final String LAST_PAGE_XPATH = "//lpage";
    public static final String REFERENCES_XPATH = "//ref-list/ref";
    public static final String FULL_TEXT_XPATH = "//body";
    public static final String FULL_TEXT_TEXT_XPATH = "//body";
    public static final String FIGURE_XPATH = "//fig";
    public static final String TABLE_XPATH = "//table-wrap";

    public static final String NO_TITLE_DEFAULT = "No Title Found";
    public static final String NO_JOURNAL_NAME_DEFAULT = "No Journal Name Found";
    public static final String NO_DAY_DEFAULT = "No Publication Day Found";
    public static final String NO_MONTH_DEFAULT = "No Publication Month Found";
    public static final String NO_YEAR_DEFAULT = "No Publication Year Found";
    public static final String NO_PMC_ID_DEFAULT = "No PMC ID Found";
    public static final String NO_PUBMED_ID_DEFAULT = "No Pubmed ID Found";
    public static final String NO_VOLUME_DEFAULT = "No Volume Found";
    public static final String NO_FIRST_PAGE_DEFAULT = "No First Page Found";
    public static final String NO_LAST_PAGE_DEFAULT = "No Last Page Found";

    public static final String DEFAULT_ABSTRACT_SECTION = "";
    public static final String DEFAULT_FULL_TEXT_SECTION = "";
    public static final String DEFAULT_FULL_TEXT_SUBSECTION = "";

    public static final int INDEX_FROM = 0; // Eg. Array index starts from 0.
    public static String citationReplacement = "citation";

    private Document document;
    private int abstractSentenceIndex;
    private int fullTextSentenceIndex;

    /**
     * Constructs an instance of PMCArticle.
     * @param articleLocation the location of the article xml file
     */
    public PMCArticle(String articleLocation) {
        try {
            DOMParser parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parser.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, false);
            parser.parse(articleLocation);
            document = parser.getDocument();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Constructs an instance of PMCArticle.
     * @param inputStream the input stream of the article xml file
     */
    public PMCArticle(InputStream inputStream) {
        try {
            DOMParser parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parser.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, false);
            parser.parse(new InputSource(inputStream));
            document = parser.getDocument();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Constructs an instance of PMCArticle with the pmc id of the article.
     * This requires accessing the article at the PMC server, hence by using
     * this constructor, you are allowing this program to connect to the
     * internet.
     * @param pmcId the PMC id of the article
     * @param timeToWaitBeforeConnecting the time to wait in ms before
     * connecting to PMC server. If you are accessing a single article, 0 will
     * be fine. If you are doing a batch job, then read the guidelines at -
     * http://www.pubmedcentral.nih.gov/about/oai.html (accessed: 07/18/2009)
     * for high-volume retrievals and set time to wait in milli-seconds
     * accordingly.
     */
    public PMCArticle(String pmcId, int timeToWaitBeforeConnecting) {
        try {
            Thread.sleep(timeToWaitBeforeConnecting);
            DOMParser parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parser.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, false);
            parser.parse("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pmc&id=" + pmcId);

            document = parser.getDocument();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The default constructor is not available
     */
    private PMCArticle() {
    }

    /**
     * Gets the title of the article. 
     * @return the title of the article
     */
    public String getTitle() {
        return commonElementParser(TITLE_XPATH, NO_TITLE_DEFAULT);
    }

    /**
     * Gets the pubmed central id of the article
     * @return the pubmed central id of the article
     */
    public String getPmcId() {
        return commonElementParser(PMC_ID_XPATH, NO_PMC_ID_DEFAULT);
    }

    /**
     * Gets the pubmed id of the article
     * @return pubmed id of the article
     */
    public String getPubmedId() {
        return commonElementParser(PUBMED_ID_XPATH, NO_PUBMED_ID_DEFAULT);
    }

    /**
     * Gets the journal name of the article
     * @return the name of the journal
     */
    public String getJournal() {
        return commonElementParser(JOURNAL_XPATH, NO_JOURNAL_NAME_DEFAULT);
    }

    /**
     * Gets the journal volume of the article
     * @return volume of the journal in which the article was published.
     */
    public String getVolume() {
        return commonElementParser(VOLUME_XPATH, NO_VOLUME_DEFAULT);
    }

    /**
     * Gets the number of the first page of the article in the journal
     * @return the first page number
     */
    public String getFirstPage() {
        return commonElementParser(FIRST_PAGE_XPATH, NO_FIRST_PAGE_DEFAULT);
    }

    /**
     * Gets the number of the first page of the article in the journal
     * @return the last page number
     */
    public String getLastPage() {
        return commonElementParser(LAST_PAGE_XPATH, NO_LAST_PAGE_DEFAULT);
    }

    /**
     * Gets the publication date of the article
     * @return the PMCArticlePublicationDate object for this article
     */
    public PMCArticlePublicationDate getPublicationDate() {
        String day = commonElementParser(PUBLICATION_DATE_DAY_XPATH, NO_DAY_DEFAULT);
        String month = commonElementParser(PUBLICATION_DATE_MONTH_XPATH, NO_MONTH_DEFAULT);
        String year = commonElementParser(PUBLICATION_DATE_YEAR_XPATH, NO_YEAR_DEFAULT);
        return new PMCArticlePublicationDate(day, month, year);
    }

    /**
     * Gets a list of authors of this article
     * @return a list of PMCArticleAuthors
     */
    public List<PMCArticleAuthor> getAuthors() {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile(AUTHORS_XPATH);
            NodeList sectionNodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
            if (sectionNodes.getLength() == 0) {
                return new ArrayList<>();
            }

            List<PMCArticleAuthor> authorList = new ArrayList<>();
            Node node, childNode, grandChildNode;
            String firstName;
            String lastName;
            String email;
            NodeList childNodes;
            NodeList grandChildNodes;

            for (int i = 0; i < sectionNodes.getLength(); ++i) {
                firstName = "";
                lastName = "";
                email = "";
                node = sectionNodes.item(i);
                childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); ++j) {
                    childNode = childNodes.item(j);
                    if ("name".equalsIgnoreCase(childNode.getNodeName())) {
                        grandChildNodes = childNode.getChildNodes();
                        for (int k = 0; k < grandChildNodes.getLength(); ++k) {
                            grandChildNode = grandChildNodes.item(k);
                            if ((grandChildNode.getNodeName() != null) && "given-names".equalsIgnoreCase(grandChildNode.getNodeName())) {
                                firstName = grandChildNode.getTextContent();
                            } else if ((grandChildNode.getNodeName() != null) && "surname".equalsIgnoreCase(grandChildNode.getNodeName())) {
                                lastName = grandChildNode.getTextContent();
                            }
                        }
                    }
                    if ("email".equalsIgnoreCase(childNode.getNodeName())) {
                        email = childNode.getTextContent();
                    }
                }

                PMCArticleAuthor author = new PMCArticleAuthor(firstName, lastName);
                author.setEmail(email);
                authorList.add(author);
            }
            return authorList;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Gets a list of figures of the article
     * @return List of figures in this article
     */
    public List<PMCArticleFigure> getFigures() {
        List<PMCArticleFigure> figureList = new ArrayList<>();
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile(FIGURE_XPATH);
            NodeList sectionNodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < sectionNodes.getLength(); ++i) {
                String id = "";
                String label = "";
                String caption = "";
                String graphicLocation = "";
                Node node = sectionNodes.item(i);
                NamedNodeMap figureNodeAttributes = node.getAttributes();
                Node nodeId = figureNodeAttributes.getNamedItem("id");
                if (nodeId != null) {
                    id = nodeId.getTextContent();
                }
                NodeList nodeChildren = node.getChildNodes();
                for (int j = 0; j < nodeChildren.getLength(); ++j) {
                    Node childNode = nodeChildren.item(j);
                    if ((childNode.getNodeName() != null) && "caption".equalsIgnoreCase(childNode.getNodeName())) {
                        caption = childNode.getTextContent();
                    } else if ((childNode.getNodeName() != null) && "label".equalsIgnoreCase(childNode.getNodeName())) {
                        label = childNode.getTextContent();
                    } else if ((childNode.getNodeName() != null) && "graphic".equalsIgnoreCase(childNode.getNodeName())) {
                        NamedNodeMap graphicNodeAttributes = childNode.getAttributes();
                        Node nodeGraphicLocation = graphicNodeAttributes.getNamedItem("xlink:href");
                        if (nodeGraphicLocation != null) {
                            graphicLocation = nodeGraphicLocation.getTextContent();
                        }
                    }
                }
                figureList.add(new PMCArticleFigure(id, label, caption, graphicLocation));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return figureList;
    }

    /**
     * Gets a list of tables in this article
     * @return the list of tables in this article. Does not get the actual 
     * content in the table
     */
    public List<PMCArticleTable> getTables() {
        List<PMCArticleTable> tableList = new ArrayList<>();
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile(TABLE_XPATH);
            NodeList sectionNodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < sectionNodes.getLength(); ++i) {
                String id = "";
                String label = "";
                String caption = "";
                Node node = sectionNodes.item(i);
                NamedNodeMap figureNodeAttributes = node.getAttributes();
                Node nodeId = figureNodeAttributes.getNamedItem("id");
                if (nodeId != null) {
                    id = nodeId.getTextContent();
                }
                NodeList nodeChildren = node.getChildNodes();
                for (int j = 0; j < nodeChildren.getLength(); ++j) {
                    Node childNode = nodeChildren.item(j);
                    if ((childNode.getNodeName() != null) && "caption".equalsIgnoreCase(childNode.getNodeName())) {
                        caption = childNode.getTextContent();
                    } else if ((childNode.getNodeName() != null) && "label".equalsIgnoreCase(childNode.getNodeName())) {
                        label = childNode.getTextContent();
                    }
                }
                tableList.add(new PMCArticleTable(id, label, caption));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tableList;
    }

    /**
     * Gets the text of the abstract.
     * @return the abstract text.
     */
    public String getAbstractText() {
        return commonElementParser(ABSTRACT_XPATH, DEFAULT_ABSTRACT_SECTION);
    }

    /**
     * Gets the text from the full text of the article. No distiction is made
     * between section and subsection headings, or figures and tables within
     * the article.
     * @return the text from the full text of the article
     */
    public String getFullTextText() {
        return commonElementParser(FULL_TEXT_TEXT_XPATH, DEFAULT_FULL_TEXT_SECTION);
    }

    /**
     * Gets the PMCArticleAbstract object of this article. Individual sentences
     * can be extracted using this format.
     * @return the abstract object
     */
    public PMCArticleAbstract getAbstract() {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile(ABSTRACT_XPATH);
            NodeList sectionNodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
            abstractSentenceIndex = INDEX_FROM;
            PMCArticleAbstract articleAbstract = new PMCArticleAbstract();
            // Use only the first node because subsequent "abstract" nodes might be editor's summary
            if (sectionNodes.getLength() != 0) {
                getAbstractHelper(articleAbstract, sectionNodes.item(0), DEFAULT_ABSTRACT_SECTION);
            }
            return articleAbstract;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new PMCArticleAbstract();
        }
    }

    /**
     * Returns the full text object of this article. A list of sentences can be
     * obtained from this object, which has information about the sections, 
     * subsections, refering references, figures and tables.
     * @return the full text of the article
     */
    public PMCArticleFullText getFullText() {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile(FULL_TEXT_XPATH);
            NodeList sectionNodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
            fullTextSentenceIndex = INDEX_FROM;
            PMCArticleFullText articleFullText = new PMCArticleFullText();
            getFullTextHelper(articleFullText, sectionNodes, DEFAULT_FULL_TEXT_SECTION, DEFAULT_FULL_TEXT_SUBSECTION);
            return articleFullText;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new PMCArticleFullText();
        }
    }

    /**
     * Gets the list of references cited by the article
     * @return list of references in the article
     */
    public List<PMCArticleReference> getReferences() {
        List<PMCArticleReference> references = new ArrayList<>();
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile(REFERENCES_XPATH);
            NodeList sectionNodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < sectionNodes.getLength(); ++i) {
                String id = "";
                Node node = sectionNodes.item(i);
                NamedNodeMap refAttributes = node.getAttributes();
                Node idNode = refAttributes.getNamedItem("id");
                if (idNode != null) {
                    id = idNode.getTextContent();
                }
                references.add(new PMCArticleReference(id, node.getTextContent()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return references;
    }

    /**
     * Private recursive function to get abstract text.
     * @param articleAbstract the article's abstract object
     * @param node node
     * @param section section
     */
    private void getAbstractHelper(PMCArticleAbstract articleAbstract, Node node, String section) {
        int indexInParagraph;
        String[] sentences;

        if ((node.getNodeName() != null) && "p".equalsIgnoreCase(node.getNodeName())) {
            indexInParagraph = INDEX_FROM;
            String paragraphText = getTextHelper(node.getChildNodes()).toString();
            sentences = SentenceTokenizer.getSentences(paragraphText);
            for (String sentence : sentences) {
                articleAbstract.addSentence(postProcessSentence(sentence, indexInParagraph, sentences.length, abstractSentenceIndex, section, ""));
                ++abstractSentenceIndex;
                ++indexInParagraph;
            }
        } else if ((node.getNodeName() != null) && "sec".equalsIgnoreCase(node.getNodeName())) {
            NodeList children = node.getChildNodes();
            int numChildren = children.getLength();
            for (int i = 0; i < numChildren; ++i) {
                getAbstractHelper(articleAbstract, children.item(i), section);
            }
        } else if ((node.getNodeName() != null) && "title".equalsIgnoreCase(node.getNodeName())) {
            section = node.getTextContent();
        } else if ((node.getNodeName() != null) && "abstract".equalsIgnoreCase(node.getNodeName())) {
            NodeList children = node.getChildNodes();
            int numChildren = children.getLength();
            for (int i = 0; i < numChildren; ++i) {
                getAbstractHelper(articleAbstract, children.item(i), section);
            }
        }
    }

    private PMCArticleSentence postProcessSentence(String s, int indexInParagraph,
            int totalSentencesInParagraph, int indexInDocument,
            String section, String subsection) {

        String text = s.replaceAll("</?xref.*?>", "");

        PMCArticleSentence sentence = new PMCArticleSentence(text);
        sentence.setInParagraphIndex(indexInParagraph);
        sentence.setTotalSentencesInContainingParagraph(totalSentencesInParagraph);
        sentence.setIndexInDocument(indexInDocument);
        sentence.setSectionName(section);
        sentence.setSubSectionName(subsection);

        return sentence;
    }

    private StringBuffer getTextHelper(NodeList nodes) {
        StringBuffer text = new StringBuffer();
        Node node;
        for (int i = 0; i < nodes.getLength(); ++i) {
            node = nodes.item(i);
            if ("#text".equalsIgnoreCase(node.getNodeName())) {
                text.append(node.getTextContent());
            } else if ((node.getNodeName() != null) && ("xref".equalsIgnoreCase(node.getNodeName()))) {
                NamedNodeMap nodeAttributes = node.getAttributes();
                String refType, rid;
                try {
                    refType = nodeAttributes.getNamedItem("ref-type").getTextContent();
                } catch (Exception ex) {
                    refType = "";
                }
                try {
                    rid = nodeAttributes.getNamedItem("rid").getTextContent();
                } catch (Exception ex) {
                    rid = "";
                }
                text.append("<xref ref-type=\"").append(refType).append("\" rid=\"").append(rid).append("\">").append(node.getTextContent()).append("</xref>");
            } else if ((node.getNodeName() != null) && ("fig".equalsIgnoreCase(node.getNodeName()))) {
                // Ignore figures
            } else if ((node.getNodeName() != null) && ("table-wrap".equalsIgnoreCase(node.getNodeName()))) {
                // Ignore tables
            } else {
                text.append(getTextHelper(node.getChildNodes()));
            }
        }
        return text;
    }

    private void getFullTextHelper(PMCArticleFullText articleFullText, NodeList nodes, String section, String subSection) {
        Node node;
        int indexInParagraph;
        String[] sentences;

        for (int i = 0; i < nodes.getLength(); ++i) {
            node = nodes.item(i);
            if ((node.getNodeName() != null) && "p".equalsIgnoreCase(node.getNodeName())) {
                indexInParagraph = INDEX_FROM;
                String paragraphText = getTextHelper(node.getChildNodes()).toString();
                sentences = SentenceTokenizer.getSentences(paragraphText);
                for (String sentence : sentences) {
                    articleFullText.addSentence(postProcessSentence(sentence, indexInParagraph, sentences.length, fullTextSentenceIndex, section, subSection));
                    ++fullTextSentenceIndex;
                    ++indexInParagraph;
                }
            } else if ((node.getNodeName() != null) && "sec".equalsIgnoreCase(node.getNodeName())) {
                getFullTextHelper(articleFullText, node.getChildNodes(), section, subSection);
            } else if ((node.getNodeName() != null) && "body".equalsIgnoreCase(node.getNodeName())) {
                getFullTextHelper(articleFullText, node.getChildNodes(), section, subSection);
            } else if ((node.getNodeName() != null) && "title".equalsIgnoreCase(node.getNodeName())) {
                if (DEFAULT_FULL_TEXT_SECTION.equalsIgnoreCase(section)) {
                    section = node.getTextContent();
                } else if (DEFAULT_FULL_TEXT_SUBSECTION.equalsIgnoreCase(subSection)) {
                    subSection = node.getTextContent();
                }
            } else if ((node.getNodeName() != null) && "fig".equalsIgnoreCase(node.getNodeName())) {
                //Ignore
            } else if ((node.getNodeName() != null) && "table-wrap".equalsIgnoreCase(node.getNodeName())) {
                //Ignore
            }
        }
    }

    private String commonElementParser(String xPathString, String defaultText) {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile(xPathString);
            NodeList sectionNodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
            if (sectionNodes.getLength() == 0) {
                return defaultText;
            }
            return sectionNodes.item(0).getTextContent();
        } catch (Exception ex) {
            System.err.println("Exception");
            ex.printStackTrace();
            return defaultText;
        }
    }
}
