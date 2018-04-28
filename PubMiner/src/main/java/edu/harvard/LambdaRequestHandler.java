package edu.harvard;

import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.uwm.pmcarticleparser.PMCArticle;
import edu.uwm.pmcarticleparser.structuralelements.PMCArticleAbstract;
import edu.uwm.pmcarticleparser.structuralelements.PMCArticleFullText;
import edu.uwm.pmcarticleparser.structuralelements.PMCArticleSentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by bingao on 4/16/18.
 */
public class LambdaRequestHandler implements RequestHandler<String, String> {
    @Override
    public String handleRequest(String input, Context context) {
        String pmcid = input;

        System.out.println("pmcid: " + pmcid);

        PMCArticle pa = new PMCArticle(pmcid, 0);
        // PMCArticle pa = new PMCArticle("./PMC"+pmcid+".nxml");

        PMCArticleAbstract pmcArticleAbstract = pa.getAbstract();

        PMCArticleFullText fullText = pa.getFullText();
        List<PMCArticleSentence> allSentences = new ArrayList<>();
        allSentences.addAll(pmcArticleAbstract.getAbstractSentences());
        allSentences.addAll(fullText.getFullTextSentences());

        // Skip articles that are empty.
        if (allSentences.isEmpty()) {
            return "Skip articles that are empty";
        }

        List<PMCArticleSentence> demographicSentences = allSentences.stream().filter(s -> s.hasAnchorWords() && s
                .getNummodCount() > 0).collect(Collectors.toList());

        Map<String, Integer> numCounts = new HashMap<>();
        for (PMCArticleSentence s : demographicSentences) {
            for (int index : s.getNummodIndices()) {
                String currentLemma = s.getLemmas().get(index);
                Integer count = numCounts.get(currentLemma);
                if (count == null) {
                    numCounts.put(currentLemma, 1);
                } else {
                    numCounts.put(currentLemma, count + 1);
                }
            }
        }
        numCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(System.out::println);

        System.out.println("Number of sentences: " + allSentences.size());

        demographicSentences = demographicSentences.stream().sorted((s1, s2) ->
                s2.getDemographicScoreBasedOnNumCounts(numCounts).compareTo(s1.getDemographicScoreBasedOnNumCounts

                        (numCounts))).collect(Collectors.toList());

        List<Map> sentenceList = new ArrayList<>();

        int i = 0;
        for (PMCArticleSentence s : demographicSentences) {
            // SemanticGraph semanticGraph = sentence.dependencyGraph();
            // System.out.println("dependencyGraph: " + semanticGraph);

            System.out.println(i + ": " + s.getText());
            System.out.println("lemmas: " + s.getLemmas());
            for (int index : s.getNummodIndices()) {
                System.out.println(s.getLemmas().get(index) + " " + s.getLemmas().get(index + 1) + "\t" + numCounts.get(s.getLemmas().get(index)));

            }

            System.out.println("End of numeric modifier list. Score: " + s.getDemographicScoreBasedOnNumCounts
                    (numCounts) + "\n");

            // System.out.println("\tnerTags: " + s.getNerTags());

            ++i;
            System.out.println(s.getInParagraphIndex() + "/" + s.getTotalSentencesInContainingParagraph());
            System.out.println(s.getSectionName());
            System.out.println(s.getSubSectionName());
            System.out.println();

            Map sentenceMap = new HashMap();
            sentenceMap.put("text", s.getText());
            String sectionName = null;
            if (!s.getSubSectionName().isEmpty()) {
                sectionName = s.getSubSectionName();
            } else if (!s.getSectionName().isEmpty()) {
                sectionName = s.getSectionName();
            } else {
                sectionName = "Abstract";
            }
            sentenceMap.put("section", sectionName);
            sentenceList.add(sentenceMap);

            if (i >= 10) {
                break;
            }
        }

        if (sentenceList.isEmpty()) {
            return "not randomized clinical trials";
        }

        ValueMap valueMap = new ValueMap().withList(":val", sentenceList).withList(":empty_list", new ArrayList<>());

        return sentenceList.toString();
    }
}