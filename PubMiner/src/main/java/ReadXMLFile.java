import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreNLPProtos;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphFactory.Mode;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.uwm.pmcarticleparser.PMCArticle;
import edu.uwm.pmcarticleparser.structuralelements.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.SyncFailedException;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import org.slf4j.impl.StaticLoggerBinder;

import edu.stanford.nlp.simple.*;

/**
 * Created by bingao on 3/10/18.
 */
public class ReadXMLFile {
    public static void main(String argv[]) {
        PMCArticle pa = new PMCArticle("/Users/bingao/Desktop/SampleFiles/PMC4724680.nxml");

        pa = new PMCArticle("4724680", 0);
        // pa = new PMCArticle("1308868", 0);
        PMCArticleFullText ft = pa.getFullText();

        /*
        List<PMCArticleSentence> sentences = ft.getFullTextSentences();
        for (PMCArticleSentence sentence : sentences) {
            if (sentence.getInParagraphIndex() == 0) {
                System.out.println("");
            }
            System.out.print(" " + sentence.getText());
        }

        for (PMCArticleAuthor author : pa.getAuthors()) {
            System.out.println(author.getFirstName() + " - " + author.getLastName() + " - " + author.getEmail());
        }

        PMCArticleAbstract abs = pa.getAbstract();
        for (PMCArticleSentence s : abs.getAbstractSentences()) {
            System.out.println(s.getText());
        }
        */

        PMCArticleFullText f = pa.getFullText();
        System.out.println("Number of sentences: " + f.getFullTextSentences().size());
        int i = 0;
        for (PMCArticleSentence s : f.getFullTextSentences()) {
            if (!s.getWordSet().contains("age")) {
                continue;
            }

            System.out.println(i + ": " + s.getText());

            // System.out.println("WordSet: " + s.getWordSet());

            Sentence sentence = new Sentence(s.getText());
            List<String> words = sentence.words();
            SemanticGraph semanticGraph = sentence.dependencyGraph();
            // System.out.println("dependencyGraph: " + semanticGraph);

            List<Optional<String>> labels = sentence.incomingDependencyLabels();
            for (int index=0; index < labels.size(); index++) {
                Optional<String> labelOptional = labels.get(index);
                if (labelOptional.isPresent() && labelOptional.get().equals("nummod")) {
                    System.out.println(words.get(index) + " " + words.get(index+1));
                }
            }

            System.out.println("End of numeric modifier list.\n");

            ++i;
            System.out.println(s.getInParagraphIndex() + "/" + s.getTotalSentencesInContainingParagraph());
            System.out.println(s.getSectionName());
            System.out.println(s.getSubSectionName());
            if (s.isRefersCitation()) {
                List<String> citations = s.getReferedCitationId();
                for (String citation : citations) {
                    System.out.println("  Citation ID: " + citation);
                }
            }
            System.out.println("");
        }

        /*
        List<PMCArticleFigure> figs = pa.getFigures();
        for (PMCArticleFigure fig : figs) {
            System.out.println("ID: " + fig.getId());
            System.out.println("Label: " + fig.getLabel());
            System.out.println("Caption: " + fig.getCaption());
            System.out.println("Graphic Location: " + fig.getGraphicLocation());
            System.out.println("");
        }
        */

        List<PMCArticleTable> tables = pa.getTables();
        for (PMCArticleTable tab : tables) {
            System.out.println("ID: " + tab.getId());
            System.out.println("Label: " + tab.getLabel());
            System.out.println("Caption: " + tab.getCaption());
            System.out.println("");
        }

        /*
        List<PMCArticleReference> refs = pa.getReferences();
        PMCArticleReference ref = refs.get(11);
        String refID = ref.getId();

        PMCArticleFullText paft = pa.getFullText();
        for (PMCArticleSentence sentence : paft.getFullTextSentences()) {
            if (sentence.getReferedCitationId().contains(refID)) {
                System.out.println(sentence.getText());
            }
        }

        for (PMCArticleReference reference : refs) {
            System.out.println(reference.getId());
            System.out.println(reference.getText());
            System.out.println();
        }
        */
    }
}
