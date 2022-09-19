package org.lucene.secondTask;

import org.lucene.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Overall Lucene query looks good
 * Same recommendations as from task 1 applies here as well to increase readability and supportability
 */
public class SecondTaskLuceneWithParse {

    // In this case String query parse() method is used instead of PhraseQuery
    private static final Logger LOGGER = Logger.getLogger(SecondTaskLuceneWithParse.class.getName());


    public static void main(String[] args) throws Exception {
        // prepare fields and documents
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

        // create list of queries
        List<String> userQueries = new ArrayList<>();
        userQueries.add("to be not:1");
        userQueries.add("to or to:1");
        userQueries.add("to:1");
        userQueries.add("long story short:0");
        userQueries.add("long short:0");
        userQueries.add("long short:1");
        userQueries.add("story long:1");
        userQueries.add("story long:2");

        // index and search
        Indexer indexer = new Indexer(fields, documents);
        Searcher searcher = new SecondTaskSearcher(indexer.getIndex());
        Map<String, List<String>> results = searcher.search(userQueries, "fileContent");
        results.forEach((k, v) -> {
            StringBuilder resString = new StringBuilder();
            resString.append("QUERY: ").append(k).append("\n").append("RESULTS:").append("\n");
            v.forEach(s -> resString.append(s).append("\n"));
            LOGGER.info("\n" + resString);
        });

        // Switch between Query with parsing AND Phrase query
        // can be done in the SecondTaskSearcher class (search method)
    }
}