package org.lucene.firstTask;

import org.lucene.*;

import java.util.*;
import java.util.logging.Logger;

/* todo: In general I would recommend to refactor this a little bit
 It would be great, if we introduce 2 components
 Indexer -- who will be responsible of loading documents, and creating a lucene index
         with method to index documents which returns a directory with prepared index
         `public Directory createIndex(String filePathWithDocuments)`


 Searcher -- who will be responsible for searching in the Lucene index
             Directory can be included in Searcher constructor - so we know when creating a Searcher, which directory we are interested in
             And searcher have a simple method to get list of matched paths by search string that will hide all implementation details from caller
             `public List<String> findPaths(String query)`

And in the Test itself, we can Create and Indexer, create Lucene index, Create a searcher based on produced Lucene Index and run assertions
f.e. with popular hamcrest https://www.baeldung.com/hamcrest-collections-arrays

something similar to this one
assertThat(searcher.findPaths("searchPhrase"), contains("expectedResult1", "expectedResult2"))
 */
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