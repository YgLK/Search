package org.lucene.test;

import org.junit.Before;
import org.junit.Test;
import org.lucene.CustomDocument;
import org.lucene.CustomField;
import org.lucene.Indexer;
import org.lucene.Searcher;
import org.lucene.firstTask.FirstTaskLucene;

import java.util.*;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SecondTaskBonusTest {

    private static final Logger LOGGER = Logger.getLogger(FirstTaskLucene.class.getName());
    private Searcher searcher;
    private Indexer indexer;
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
                        .addField("fileContent", "long story shoe make")
        );
        documents.add(
                new CustomDocument()
                        .addField("fileName", "file4")
                        .addField("fileContent", "make long short story")
        );

        this.indexer = new Indexer(fields, documents);
        this.searcher = new Searcher(indexer.getIndex());
    }

    @Test
    public void testIndexingFromFile(){
        String path = "src/data/documents.json";
        Indexer indexerWithFileData = new Indexer(path);

        Map<String, CustomField> indexerFields = indexer.getFields();
        Map<String, CustomField> indexerFieldsFromFile = indexerWithFileData.getFields();
        List<CustomDocument> indexerDocsFromFile = indexerWithFileData.getDocuments();


        assertThat(indexerDocsFromFile, hasSize(indexer.getDocuments().size()));
        assertEquals(indexerFieldsFromFile.keySet(), indexerFields.keySet());

        // check if all fields are equal
        indexerFields.keySet().forEach(key -> {
            assertEquals(indexerFields.get(key).getType(),indexerFieldsFromFile.get(key).getType());
            assertEquals(indexerFields.get(key).getName(),indexerFieldsFromFile.get(key).getName());
            assertEquals(indexerFields.get(key).isStore(),indexerFieldsFromFile.get(key).isStore());
        });
        // check if all documents contain fields
        for(CustomDocument custDoc : indexerDocsFromFile){
            Set<String> docFieldNames = custDoc.getFields().keySet();
            indexerFields.keySet().forEach(
                    fieldName -> assertTrue(docFieldNames.contains(fieldName))
            );
        }
    }

    @Test
    public void testPrefixQueryResults1() throws Exception {
        List<String> searchResults = searcher.searchWithPrefix("long story sho*", SEARCH_FIELD, 0, false);

        assertThat(searchResults, hasSize(3));
        assertThat(searchResults, containsInAnyOrder("make a long story short", "long story shoe make", "make long short story"));
    }

    @Test
    public void testPrefixQueryResults2() throws Exception {
        List<String> searchResults = searcher.searchWithPrefix("long story sho*", SEARCH_FIELD, 0, true);

        assertThat(searchResults, hasSize(2));
        assertThat(searchResults, containsInAnyOrder("make a long story short", "long story shoe make"));
    }

    @Test
    public void testPrefixQueryResults3() throws Exception {
        List<String> searchResults = searcher.searchWithPrefix("que* is", SEARCH_FIELD, 1, false);

        assertThat(searchResults, hasSize(1));
        assertThat(searchResults, contains("to be or not to be that is the question"));
    }

    @Test
    public void testPrefixQueryResults4() throws Exception {
        List<String> searchResults = searcher.searchWithPrefix("que* is", SEARCH_FIELD, 1, true);

        assertThat(searchResults, is(empty()));
    }
}
