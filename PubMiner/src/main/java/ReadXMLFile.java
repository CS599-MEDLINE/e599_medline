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
import java.util.*;
import java.util.Map.*;
import java.util.stream.Collectors;

import org.slf4j.impl.StaticLoggerBinder;

import edu.stanford.nlp.simple.*;

import static java.util.Arrays.asList;

/**
 * Created by bingao on 3/10/18.
 */
public class ReadXMLFile {
    public static void main(String argv[]) {
        PMCArticle pa = new PMCArticle("/Users/bingao/Desktop/SampleFiles/PMC4724680.nxml");

        pa = new PMCArticle("2848718", 0);

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
        */

        PMCArticleAbstract pmcArticleAbstract = pa.getAbstract();
        /*
        for (PMCArticleSentence s : pmcArticleAbstract.getAbstractSentences()) {
            System.out.println(s.getText());
        }
        */

        PMCArticleFullText fullText = pa.getFullText();
        List<PMCArticleSentence> allSentences = new ArrayList<>();
        allSentences.addAll(pmcArticleAbstract.getAbstractSentences());
        allSentences.addAll(fullText.getFullTextSentences());

        /*
        List<PMCArticleSentence> demographicSentences = allSentences.stream().filter(s -> s.hasAnchors() && s
                .getNummodCount() > 0).sorted(Comparator.comparing
                (PMCArticleSentence::getDemographicScore).reversed()).collect(Collectors.toList());
        */

        List<PMCArticleSentence> demographicSentences = allSentences.stream().filter(s -> s.hasAnchors() && s
                .getNummodCount() > 0).collect(Collectors.toList());

        Map<String, Integer> numCounts = new HashMap<>();
        for (PMCArticleSentence s : demographicSentences) {
            for (int index : s.getNummodIndices()) {
                String currentLemma = s.getLemmas().get(index);
                Integer count = numCounts.get(currentLemma);
                if (count==null) {
                    numCounts.put(currentLemma, 1);
                } else {
                    numCounts.put(currentLemma, count+1);
                }
            }
        }
        numCounts.entrySet().stream()
                .sorted(Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(System.out::println);

        System.out.println("Number of sentences: " + allSentences.size());

        demographicSentences = demographicSentences.stream().sorted((s1, s2) ->
                s2.getDemographicScoreBasedOnNumCounts(numCounts).compareTo(s1.getDemographicScoreBasedOnNumCounts
                        (numCounts))).collect(Collectors.toList());

        int i = 0;
        for (PMCArticleSentence s : demographicSentences) {
            // SemanticGraph semanticGraph = sentence.dependencyGraph();
            // System.out.println("dependencyGraph: " + semanticGraph);

            System.out.println(i + ": " + s.getText());
            for (int index : s.getNummodIndices()) {
                System.out.println(s.getLemmas().get(index) + " " + s.getLemmas().get(index+1) + "\t" + numCounts.get(s.getLemmas().get(index)));
            }

            System.out.println("End of numeric modifier list. Score: " + s.getDemographicScoreBasedOnNumCounts(numCounts) + "\n");

            // System.out.println("\tnerTags: " + s.getNerTags());

            ++i;
            System.out.println(s.getInParagraphIndex() + "/" + s.getTotalSentencesInContainingParagraph());
            System.out.println(s.getSectionName());
            System.out.println(s.getSubSectionName());

            /*
            if (s.isRefersCitation()) {
                List<String> citations = s.getReferedCitationId();
                for (String citation : citations) {
                    System.out.println("  Citation ID: " + citation);
                }
            }
            */

            System.out.println();

            if (i>=5) {
                break;
            }
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

        List<PMCArticleTable> tables = pa.getTables();
        for (PMCArticleTable tab : tables) {
            System.out.println("ID: " + tab.getId());
            System.out.println("Label: " + tab.getLabel());
            System.out.println("Caption: " + tab.getCaption());
            System.out.println("");
        }

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
