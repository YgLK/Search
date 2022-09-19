package org.lucene.secondTask.bonus;

import org.lucene.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/*
Done nicely,
But it would be better if we can test our solution with other queries as well :)

I think if we can apply recommendations from FirstTaskLucene, that we simply can extend our "Searcher" component with additional methods

In this case `List<String> searchWithPrefix(String query)`


 */
public class FirstBonusTask {
    private static final Logger LOGGER = Logger.getLogger(FirstBonusTask.class.getName());

    public static void main(String[] args) throws Exception {
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
        documents.add(
                new CustomDocument()
                        .addField("fileName", "file3")
                        .addField("fileContent", "make a long short story")
        );

        // index and search
        Indexer indexer = new Indexer(fields, documents);
        Searcher searcher = new SecondTaskBonusSearcher(indexer.getIndex());

        String stringQuery = "long story sho*";
        // task differs only by inOrder parameter in searchWithPrefix()
        firstBonusTask(searcher, stringQuery);
        secondBonusTask(searcher, stringQuery);
    }


    private static void firstBonusTask(Searcher searcher, String stringQuery) throws Exception {
        LOGGER.info("FIRST BONUS TASK");
        // FIRST BONUS TASK
        List<String> results = searcher.searchWithPrefix(stringQuery, "fileContent", 0, false);
        StringBuilder resString = new StringBuilder();
        resString.append("FIRST BONUS TASK - QUERY: ").append(stringQuery).append("\n").append("RESULTS:").append("\n");
        results.forEach(result -> resString.append(result).append("\n"));
    }

    private static void secondBonusTask(Searcher searcher, String stringQuery) throws Exception {
        LOGGER.info("SECOND BONUS TASK");
        // SECOND BONUS TASK
        List<String> results = searcher.searchWithPrefix(stringQuery, "fileContent", 0, true);
        StringBuilder resString = new StringBuilder();
        resString.append("SECOND BONUS TASK - QUERY: ").append(stringQuery).append("\n").append("RESULTS:").append("\n");
        results.forEach(result -> resString.append(result).append("\n"));
    }
}