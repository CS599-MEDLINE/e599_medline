import edu.uwm.pmcarticleparser.PMCArticle;
import edu.uwm.pmcarticleparser.structuralelements.PMCArticleSentence;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by bingao on 5/5/18.
 */
public class SentencesMinerTest {
    @Test
    public void getDemographicSentences_pmc3978729() throws Exception {
        PMCArticle pmcArticle = new PMCArticle("3978729", 0);
        List<PMCArticleSentence> pmcArticleSentences = SentencesMiner.getDemographicSentences(pmcArticle);

        assertTrue(pmcArticleSentences.stream().anyMatch(s -> s.getText().equals("A total of 585 women completed the " +
                "study through the three-month follow-up (see Figure 1 for details on participant flow through the " +
                "intervention).")));
    }

    @Test
    public void getDemographicSentences_pmc4177075() throws Exception {
        PMCArticle pmcArticle = new PMCArticle("4177075", 0);
        List<PMCArticleSentence> pmcArticleSentences = SentencesMiner.getDemographicSentences(pmcArticle);

        assertTrue(pmcArticleSentences.stream().anyMatch(s -> s.getText().equals("1137 people of an internet panel " +
                "with an intention to use a diagnostic self-test for cholesterol or diabetes were enrolled in a " +
                "web-based randomized controlled trial consisting of four groups: a cholesterol intervention and " +
                "control group and a diabetes intervention and control group.")));
    }

    @Test
    public void getDemographicSentences_pmc5012277() throws Exception {
        PMCArticle pmcArticle = new PMCArticle("5012277", 0);
        List<PMCArticleSentence> pmcArticleSentences = SentencesMiner.getDemographicSentences(pmcArticle);

        assertTrue(pmcArticleSentences.stream().anyMatch(s -> s.getText().equals("Approximately 450 patients will be " +
                "randomized to receive either (i) standard of care or “usual\" transplantation education, or (ii) " +
                "standard of care plus iChoose Kidney.")));
    }

    @Test
    public void getDemographicSentences_pmc3480321() throws Exception {
        PMCArticle pmcArticle = new PMCArticle("3480321", 0);
        List<PMCArticleSentence> pmcArticleSentences = SentencesMiner.getDemographicSentences(pmcArticle);

        assertTrue(pmcArticleSentences.stream().anyMatch(s -> s.getText().equals("Patients aged 50–74 years and " +
                "overdue for CRC screening were randomized to the web-based decision aid or a control program seen " +
                "immediately before a scheduled primary care appointment.")));
    }

    @Test
    public void getDemographicSentences_pmc3727283() throws Exception {
        PMCArticle pmcArticle = new PMCArticle("3727283", 0);
        List<PMCArticleSentence> pmcArticleSentences = SentencesMiner.getDemographicSentences(pmcArticle);

        assertTrue(pmcArticleSentences.stream().anyMatch(s -> s.getText().equals("Men (N=543) were 54.9 (SD=8.1) " +
                "years old and 61% were African-American.")));
    }

    @Test
    public void getDemographicSentences_pmc4822955() throws Exception {
        PMCArticle pmcArticle = new PMCArticle("4822955", 0);
        List<PMCArticleSentence> pmcArticleSentences = SentencesMiner.getDemographicSentences(pmcArticle);

        assertTrue(pmcArticleSentences.stream().anyMatch(s -> s.getText().equals("1,447 men were randomly allocated " +
                "to either a standard decision aid with a fixed set of five attributes or a personalised decision aid" +
                " with choice over the inclusion of up to 10 attributes.")));
    }

    @Test
    public void getDemographicSentences_pmc4972478() throws Exception {
        PMCArticle pmcArticle = new PMCArticle("4972478", 0);
        List<PMCArticleSentence> pmcArticleSentences = SentencesMiner.getDemographicSentences(pmcArticle);

        assertTrue(pmcArticleSentences.stream().anyMatch(s -> s.getText().equals("Patients aged at least 18 years and" +
                " 80 years or less with an office SBP at least 150 mmHg and a 24-h SBP at least 140 mmHg despite a " +
                "prescribed therapeutic schedule with an appropriate combination of three or more full-dose " +
                "antihypertensive drugs, including a diuretic, and maintained for the last 3 months, were eligible to" +
                " participate in the trial.")));
    }

    @Test
    public void getDemographicSentences_pmc5268489() throws Exception {
        PMCArticle pmcArticle = new PMCArticle("5268489", 0);
        List<PMCArticleSentence> pmcArticleSentences = SentencesMiner.getDemographicSentences(pmcArticle);

        assertTrue(pmcArticleSentences.stream().anyMatch(s -> s.getText().equals("Other reasons for exclusion: 384 " +
                "women had used captopril and/or clonidine previously, six were smokers, five had heart disease, one " +
                "used illicit drugs, two had contraindications to oral medication, 17 had contraindications to " +
                "captopril and three had contraindications to clonidine (Fig 1).")));
    }
}
