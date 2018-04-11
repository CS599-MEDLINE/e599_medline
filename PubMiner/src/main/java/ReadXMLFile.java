import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import edu.uwm.pmcarticleparser.PMCArticle;
import edu.uwm.pmcarticleparser.structuralelements.*;

import java.util.HashMap;
import java.util.Iterator;

import java.util.*;
import java.util.Map.*;
import java.util.stream.Collectors;

/**
 * Created by bingao on 3/10/18.
 */
public class ReadXMLFile {

    private static final String TABLE_NAME = "demographics";
    private static final String PMCID_COLUMN_NAME = "pmcid";
    private static final String SENTENCES_COLUMN_NAME = "sentences";
    private static final String ERROR_STATUS_COLUMN_NAME = "errorStatus";

    public static void main(String argv[]) {
        int updateCount = 0;

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        Map<String, AttributeValue> lastKeyEvaluated = null;
        Table table = dynamoDB.getTable(TABLE_NAME);
        do {
            ScanRequest scanRequest = new ScanRequest()
                    .withTableName(TABLE_NAME).withExclusiveStartKey(lastKeyEvaluated);
            ScanResult result = client.scan(scanRequest);
            System.out.println("Item Count: " + result.getItems().size());

            for (Map<String, AttributeValue> row : result.getItems()) {
                String pmcid = row.get(PMCID_COLUMN_NAME).getS();

                if (row.containsKey(SENTENCES_COLUMN_NAME)) {
                    continue;
                }

                if (row.containsKey(ERROR_STATUS_COLUMN_NAME)) {
                    System.out.println("pmcid " + pmcid + " has error: " + row.get(ERROR_STATUS_COLUMN_NAME).getS());
                    continue;
                }

                System.out.println("pmcid: " + pmcid);

                // PMCArticle pa = new PMCArticle(pmcid, 0);
                PMCArticle pa = new PMCArticle("./PMC"+pmcid+".nxml");

                /*
                List<PMCArticleSentence> sentences = ft.getFullTextSentences();
                for (PMCArticleSentence sentence : sentences) {
                    if (sentence.getInParagraphIndex() == 0) {
                        System.out.println("");
                    }
                    System.out.print(" " + sentence.getText());
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

                // Skip articles that are empty.
                if (allSentences.isEmpty()) {
                    UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(PMCID_COLUMN_NAME, pmcid)
                            .withUpdateExpression("set errorStatus = :val").withValueMap(new ValueMap().withString
                                    (":val", "empty"));
                    UpdateItemOutcome updateItemOutcome = table.updateItem(updateItemSpec);

                    System.out.println("UpdateItemOutcome: " + updateItemOutcome);

                    continue;
                }

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
                        if (count == null) {
                            numCounts.put(currentLemma, 1);
                        } else {
                            numCounts.put(currentLemma, count + 1);
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
                    UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(PMCID_COLUMN_NAME, pmcid)
                            .withUpdateExpression("set errorStatus = :val").withValueMap(new ValueMap().withString
                                    (":val", "not randomized clinical trials"));
                    UpdateItemOutcome updateItemOutcome = table.updateItem(updateItemSpec);

                    System.out.println("UpdateItemOutcome: " + updateItemOutcome);

                    continue;
                }

                ValueMap valueMap = new ValueMap().withList(":val", sentenceList).withList(":empty_list", new ArrayList<>());

                updateCount++;
                System.out.println("update count: " + updateCount + " valueMap: " + valueMap);

                try {
                    UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(PMCID_COLUMN_NAME, pmcid)
                            .withUpdateExpression("set sentences = list_append(if_not_exists(sentences, :empty_list)," +
                                    " :val)")

                            .withValueMap(valueMap).withConditionExpression("attribute_not_exists(sentences)");
                    UpdateItemOutcome updateItemOutcome = table.updateItem(updateItemSpec);
                } catch (ConditionalCheckFailedException ex) {
                    System.out.println("ConditionalCheckFailedException: " + ex);
                }
            }
            lastKeyEvaluated = result.getLastEvaluatedKey();
        } while (lastKeyEvaluated != null);

        /*
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
        */
    }

    public static void listMyTables(DynamoDB dynamoDB) {

        TableCollection<ListTablesResult> tables = dynamoDB.listTables();
        Iterator<Table> iterator = tables.iterator();

        System.out.println("Listing table names");

        while (iterator.hasNext()) {
            Table table = iterator.next();
            System.out.println(table.getTableName());
        }
    }
}
