package edu.uwm.pubmedparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses PubMed
 * @author shashank
 */
public class PubmedParser {
    public static final String PMID_NODE = "PMID";
    public static final String TITLE_NODE = "ArticleTitle";
    public static final String ABSTRACT_NODE = "Abstract";
    public static final String YEAR_NODE = "Year";
    public static final String MONTH_NODE = "Month";
    public static final String DAY_NODE = "Day";
    public static final String PUBLICATION_DATE_NODE = "PubDate";
    public static final String TEXT_NODE = "#text";
    public static final String ARTICLE_XPATH = "//MedlineCitation";


    private DOMParser parser;
    private boolean inPubDate;

    /**
     * Initializes the PubmedParser
     */
    public PubmedParser() {
        try {
            parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parser.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, false);
        } catch (SAXException ex) {
            Logger.getLogger(PubmedParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        inPubDate = false;
    }

    /**
     * Gets the list of pubmed articles in the given gziped xml file
     * @param file
     * @return
     */
    public List<PubmedArticle> getArticles(String file) {
        InputSource is = null;
        try {
            is = new InputSource(new GZIPInputStream(new FileInputStream(file)));
        } catch (IOException ex) {
            Logger.getLogger(PubmedParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getArticles(is);
    }

    /**
     * Gets the list of pubmed articles in the given xml file
     * @param xmlFile
     * @return
     */
    public List<PubmedArticle> getArticlesFromXml(String xmlFile) {
        InputSource is = null;
        try {
            is = new InputSource(new FileInputStream(xmlFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PubmedParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getArticles(is);
    }

    /**
     * Gets the list of pubmed articles in the given input source
     * @param is
     * @return
     */
    public List<PubmedArticle> getArticles(InputSource is) {
        List<PubmedArticle> articles = new LinkedList<PubmedArticle>();
        try {
            parser.parse(is);
            Document document = parser.getDocument();
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile(ARTICLE_XPATH);
            NodeList articleNodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
            int numNodes = articleNodes.getLength();
            for (int i = 0; i < numNodes; ++i) {
                Node articleNode = articleNodes.item(i);
                PubmedArticle pubmedArticle = new PubmedArticle();
                processNode(articleNode, pubmedArticle, 0);
                articles.add(pubmedArticle);
            }
        } catch (XPathExpressionException ex) {
            Logger.getLogger(PubmedParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PubmedParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PubmedParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return articles;
    }

    /**
     * Recursively processes the given node and populates the passed {@link
     * PubmedArticle} object
     * @param root
     * @param pubmedArticle
     * @param depth
     */
    private void processNode(Node root, PubmedArticle pubmedArticle, int depth) {
        String rootName = root.getNodeName();
        if (rootName == null || TEXT_NODE.equalsIgnoreCase(rootName)) {
            return;
        } else if (PMID_NODE.equalsIgnoreCase(rootName) && depth == 1) {
            pubmedArticle.setPmid(root.getTextContent());
        } else if (TITLE_NODE.equalsIgnoreCase(rootName)) {
            pubmedArticle.setTitle(root.getTextContent());
        } else if (ABSTRACT_NODE.equalsIgnoreCase(rootName)) {
            pubmedArticle.setAbstractText(root.getTextContent());
        } else if (PUBLICATION_DATE_NODE.equalsIgnoreCase(rootName)) {
            inPubDate = true;
            handleChildNodes(root, pubmedArticle, depth);
            inPubDate = false;
        } else if (inPubDate && YEAR_NODE.equalsIgnoreCase(rootName)) {
            pubmedArticle.setYear(root.getTextContent());
        } else if (inPubDate && MONTH_NODE.equalsIgnoreCase(rootName)) {
            pubmedArticle.setMonth(root.getTextContent());
        } else if (inPubDate && DAY_NODE.equalsIgnoreCase(rootName)) {
            pubmedArticle.setDay(root.getTextContent());
        } else {
            handleChildNodes(root, pubmedArticle, depth);
        }
    }

    /**
     * Gets the children of the given node and calls processNode for each child
     * @param root
     * @param pubmedArticle
     * @param depth
     */
    private void handleChildNodes(Node root, PubmedArticle pubmedArticle, int depth) {
        NodeList nodeList = root.getChildNodes();
        int numNodes = nodeList.getLength();
        int newDepth = depth + 1;
        for (int i = 0; i < numNodes; ++i) {
            Node child = nodeList.item(i);
            processNode(child, pubmedArticle, newDepth);
        }
    }
}
