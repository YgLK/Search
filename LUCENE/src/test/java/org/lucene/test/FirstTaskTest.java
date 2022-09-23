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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;


public class FirstTaskTest {

    private static final Logger LOGGER = Logger.getLogger(FirstTaskLucene.class.getName());
    private Searcher searcher;

    @Before
    public void init(){
        Map<String, CustomField> fields = new HashMap<>();
        CustomField field = new CustomField("path", "string", true);
        fields.put(field.getName(), field);

        List<CustomDocument> documents = new ArrayList<>();
        documents.add(new CustomDocument().addField("path", "lucene/queryparser/docs/xml/img/plus.gif"));
        documents.add(new CustomDocument().addField("path", "lucene/queryparser/docs/xml/img/join.gif"));
        documents.add(new CustomDocument().addField("path", "lucene/queryparser/docs/xml/img/minusbottom.gif"));

        Indexer indexer = new Indexer(fields, documents);
        this.searcher = new Searcher(indexer.getIndex());
    }

    @Test
    public void testFirstQueryResults() throws Exception {
        List<String> searchResults = searcher.findPaths("lqdocspg", "path");

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("lucene/queryparser/docs/xml/img/plus.gif"));
    }

    @Test
    public void testSecondQueryResults() throws Exception {
        List<String> searchResults = searcher.findPaths("lucene/queryparser/docs/xml/img/plus.gif", "path");

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("lucene/queryparser/docs/xml/img/plus.gif"));
    }

    @Test
    public void testThirdQueryResults() throws Exception {
        List<String> searchResults = searcher.findPaths("lqd///gif", "path");

        assertThat(searchResults, hasSize(3));
        assertThat(searchResults,
                contains("lucene/queryparser/docs/xml/img/plus.gif",
                        "lucene/queryparser/docs/xml/img/join.gif",
                        "lucene/queryparser/docs/xml/img/minusbottom.gif"));
    }

    @Test
    public void testFourthQueryResults() throws Exception {
        List<String> searchResults = searcher.findPaths("lucene/queryparser/docs/xml/img/join.gif", "path");

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("lucene/queryparser/docs/xml/img/join.gif"));
    }

    @Test
    public void testFifthQueryResults() throws Exception {
        List<String> searchResults = searcher.findPaths("lucene/queryparser/docs/xml/img/minusbottom.gif", "path");

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("lucene/queryparser/docs/xml/img/minusbottom.gif"));
    }

    @Test
    public void testSixthQueryResults() throws Exception {
        List<String> searchResults = searcher.findPaths("minusbottom.gif", "path");

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("lucene/queryparser/docs/xml/img/minusbottom.gif"));
    }
}
