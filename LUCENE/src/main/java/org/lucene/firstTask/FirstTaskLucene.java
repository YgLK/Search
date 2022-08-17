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

public class FirstTaskLucene {

    private static final Logger LOGGER = Logger.getLogger(FirstTaskLucene.class.getName());


    public static void main(String[] args) throws IOException, ParseException {
        List<List<String>> fields = new ArrayList<>();
        // fieldName, testField or stringField -> "text" or "string", Field.Store.YES/NO
        fields.add(Arrays.asList("path", "string", "YES"));
        // prepare docs' values
        List<List<String>> fieldValues = new ArrayList<>();
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