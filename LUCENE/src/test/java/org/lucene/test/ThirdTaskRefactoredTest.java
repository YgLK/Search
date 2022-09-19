package org.lucene.test;

import org.junit.Before;
import org.junit.Test;
import org.lucene.*;
import org.lucene.thirdTask.CustomAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

public class ThirdTaskRefactoredTest {

    private static final Logger LOGGER = Logger.getLogger(ThirdTaskRefactoredTest.class.getName());
    private Searcher searcher;
    private Searcher searcherWithDotDelimiter;

    @Before
    public void init(){
        // init taken from 2nd task with couple modifications
        Map<String, CustomField> fields = new HashMap<>();
        CustomField fieldFileName = new CustomField("shortText", "text", true);
        fields.put(fieldFileName.getName(), fieldFileName);

        List<CustomDocument> documents = new ArrayList<>();
        documents.add(
                new CustomDocument()
                        .addField("shortText", "to be or not to be that is the question")
        );
        documents.add(
                new CustomDocument()
                        .addField("shortText", "this is a phrase")
        );
        documents.add(
                new CustomDocument()
                        .addField("shortText", "they was such happy for each other")
        );
        documents.add(
                new CustomDocument()
                        .addField("shortText", "see eye to eye")
        );
        documents.add(
                new CustomDocument()
                        .addField("shortText", "this apple")
        );
        documents.add(
                new CustomDocument()
                        .addField("shortText", "an apple was")
        );

        Indexer indexer = new Indexer(fields, documents, new CustomAnalyzer(" "));
        this.searcher = new Searcher(indexer.getIndex(), indexer.getAnalyzer());

        Indexer indexerDot = new Indexer(fields, documents, new CustomAnalyzer("."));
        this.searcherWithDotDelimiter = new Searcher(indexerDot.getIndex(), indexer.getAnalyzer());
    }

    @Test
    public void testCustomAnalyzer01() throws Exception {
        List<String> searchResults = searcher.searchWithCustomAnalyzer("this phrase", "shortText");

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("this is a phrase"));
    }

    @Test
    public void testCustomAnalyzer02() throws Exception {
        List<String> searchResults = searcher.searchWithCustomAnalyzer("happy that each other", "shortText");

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("they was such happy for each other"));
    }

    @Test
    public void testCustomAnalyzerWithDotDelimiter01() throws Exception {
        List<String> searchResults = searcherWithDotDelimiter.searchWithCustomAnalyzer("for apple", "shortText");

        assertThat(searchResults, hasSize(2));
        assertThat(searchResults, contains("this apple", "an apple was"));
    }
}
