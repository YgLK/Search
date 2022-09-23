package org.lucene.test;

import org.junit.Before;
import org.junit.Test;
import org.lucene.*;
import org.lucene.firstTask.FirstTaskLucene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class SecondTaskTest {

    private static final Logger LOGGER = Logger.getLogger(FirstTaskLucene.class.getName());
    private Searcher searcher;
    private static final String SEARCH_FIELD = "fileContent";

    @Before
    public void init(){
        Map<String, CustomField> fields = new HashMap<>();
        CustomField fieldFileName = new CustomField("fileName", "string", true);
        CustomField fieldFileContent = new CustomField("fileContent", "text", true);
        fields.put(fieldFileName.getName(), fieldFileName);
        fields.put(fieldFileContent.getName(), fieldFileContent);

        List<CustomDocument> documents = new ArrayList<>();
        documents.add(
                new CustomDocument()
                        .addField("fileName", "file1")
                        .addField("fileContent", "to be or not to be that is the question")
        );
        documents.add(
                new CustomDocument()
                        .addField("fileName", "file2")
                        .addField("fileContent", "make a long story short")
        );
        documents.add(
                new CustomDocument()
                        .addField("fileName", "file3")
                        .addField("fileContent", "see eye to eye")
        );

        Indexer indexer = new Indexer(fields, documents);
        this.searcher = new Searcher(indexer.getIndex());
    }

    @Test
    public void testFirstQueryResults() throws Exception {
        List<String> searchResults = searcher.searchWithSlop("to be not", SEARCH_FIELD, 1);

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("to be or not to be that is the question"));
    }

    @Test
    public void testSecondQueryResults() throws Exception {
        List<String> searchResults = searcher.searchWithSlop("to or to", SEARCH_FIELD, 1);

        assertThat(searchResults, is(empty()));
    }

    @Test
    public void testThirdQueryResults() throws Exception {
        List<String> searchResults = searcher.searchWithSlop("to", SEARCH_FIELD, 1);

        assertThat(searchResults, hasSize(2));
        assertThat(searchResults,
                contains("to be or not to be that is the question",
                        "see eye to eye"));
    }

    @Test
    public void testFourthQueryResults() throws Exception {
        List<String> searchResults = searcher.searchWithSlop("long story short", SEARCH_FIELD, 0);

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("make a long story short"));
    }

    @Test
    public void testFifthQueryResults() throws Exception {
        List<String> searchResults = searcher.searchWithSlop("long short", SEARCH_FIELD, 0);

        assertThat(searchResults, is(empty()));
    }

    @Test
    public void testSixthQueryResults() throws Exception {
        List<String> searchResults = searcher.searchWithSlop("long short", SEARCH_FIELD, 1);

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("make a long story short"));
    }

    @Test
    public void testSeventhQueryResults() throws Exception {
        List<String> searchResults = searcher.searchWithSlop("story long", SEARCH_FIELD, 1);

        assertThat(searchResults, is(empty()));
    }

    @Test
    public void testEighthQueryResults() throws Exception {
        List<String> searchResults = searcher.searchWithSlop("story long", SEARCH_FIELD, 2);

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("make a long story short"));
    }
}
