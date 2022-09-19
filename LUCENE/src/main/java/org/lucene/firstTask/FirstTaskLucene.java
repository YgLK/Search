package org.lucene.firstTask;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.lucene.CommonMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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


    public static void main(String[] args) throws IOException, ParseException {

        // todo: instead of passing around List of Lists Please create a separate structure to hold field information
        // f.e. class Field(name: path; type: string, store: bool)
        // In this case we can pass List<Field> and should not remember in other methods which index is responsible for which attribute
        List<List<String>> fields = new ArrayList<>();
        // fieldName, testField or stringField -> "text" or "string", Field.Store.YES/NO
        fields.add(Arrays.asList("path", "string", "YES"));
        Field
        // prepare docs' values

        List<List<String>> fieldValues = new ArrayList<>();
        // todo: Instead of using list of lists, and keeping in mind that the value is supposed to be path value, let us introduce a Document class
        // Document will contain field names and field values
        // f.e. class Document(fields: Map<String,String>)
        // Additionally we can add load documents from file - that will make it easy to play around with it.
        fieldValues.add(Collections.singletonList("lucene/queryparser/docs/xml/img/plus.gif"));
        fieldValues.add(Collections.singletonList("lucene/queryparser/docs/xml/img/join.gif"));
        fieldValues.add(Collections.singletonList("lucene/queryparser/docs/xml/img/minusbottom.gif"));
        // create list of queries
        List<String> userQueries = Arrays.asList("lqdocspg", "lqd///gif", "minusbottom.gif");
        search(fields, fieldValues, userQueries);
    }


    private static void search(List<List<String>> fields, List<List<String>> fieldValues, List<String> queries) throws IOException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        CommonMethods.addDocs(w, fields, fieldValues);
        w.close();

        for(String query : queries){
            // 2. query
            String termString = prepareQuery(query);

            // prepare query
            Query q = new RegexpQuery(new Term("path", termString));
            // ALTERNATIVE (remember to change line in the prepareQuery method too in case of using the WildcardQuery):
            // Query q = new WildcardQuery(new Term("path", termString));


            // todo: minor: in the code Logger and Stdout println are mixed.
            // Is there a specific reason for it? :)
            // https://jqassistant.org/avoid-usage-system-err-system/ https://www.baeldung.com/java-system-out-println-vs-loggers
            LOGGER.info("Query: " + termString);
            // to avoid tokenization and keep the input string (in this case path) in one token and match Regexp keep
            // data field as StringField (NOT TextField because TextField gets tokenized)
            // helpful link: https://stackoverflow.com/questions/35448522/regexpquery-in-lucene-not-working (first element in the numbering in the answer)


            // 3. search
            int hitsPerPage = 10;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;


            // todo: instead of just displaying the results, let us us move this to "test" and add asserts in addition to printing results
            // In this way we can add additional queries easier, and we do not have to verify it works with our eyes
            // Benifit of using tests is that when you decide to change something in your code - tests will automatically verify if you did not broke something
            // 4. display results
            System.out.println("- - - - - QUERY: " + query + " - - - - -");
            System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("path"));
            }
            System.out.println("- - - - - - - - - - - - - - - - - - - -\n");
            // reader can only be closed when there
            // is no need to access the documents anymore.
            reader.close();
        }
    }

    private static String prepareQuery(String query){
        String preparedQuery;
        // match all strings which can be reduced to the input query (query's a subsequence of the path)
        preparedQuery = query.replace("", ".*").trim();
        // when WildCardQuery used uncomment the following line:
        // preparedQuery = query.replace("", "*").trim();
        return preparedQuery;
    }
}