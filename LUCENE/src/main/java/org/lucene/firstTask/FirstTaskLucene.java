package org.lucene.firstTask;

import org.lucene.*;

import java.util.*;
import java.util.logging.Logger;

public class FirstTaskLucene {

    private static final Logger LOGGER = Logger.getLogger(FirstTaskLucene.class.getName());

    public static void main(String[] args) throws Exception {
        Map<String, CustomField> fields = new HashMap<>();
        CustomField field = new CustomField("path", "string", true);
        fields.put(field.getName(), field);

        // TODO: Additionally we can add load documents from file - that will make it easy to play around with it.
        List<CustomDocument> documents = new ArrayList<>();
        documents.add(new CustomDocument().addField("path", "lucene/queryparser/docs/xml/img/plus.gif"));
        documents.add(new CustomDocument().addField("path", "lucene/queryparser/docs/xml/img/join.gif"));
        documents.add(new CustomDocument().addField("path", "lucene/queryparser/docs/xml/img/minusbottom.gif"));

        // create list of queries
        List<String> userQueries = Arrays.asList("lqdocspg", "lqd///gif", "minusbottom.gif");

        Indexer indexer = new Indexer(fields, documents);
        Searcher searcher = new Searcher(indexer.getIndex());
        Map<String, List<String>> results = new HashMap<>();
        for(String query : userQueries) {
            results.put(query, searcher.findPaths(query, "path"));
        }
        results.forEach((k, v) -> {
            StringBuilder resString = new StringBuilder();
            resString.append("QUERY: ").append(k).append("\n").append("RESULTS:").append("\n");
            v.forEach(s -> resString.append(s).append("\n"));
            LOGGER.info("\n" + resString);
        });
    }
}