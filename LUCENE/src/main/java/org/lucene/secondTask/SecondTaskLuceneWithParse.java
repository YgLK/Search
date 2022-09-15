package org.lucene.secondTask;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.javatuples.Pair;
import org.lucene.CommonMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 * Overall Lucene query looks good
 * Same recommendations as from task 1 applies here as well to increase readability and supportability
 */
public class SecondTaskLuceneWithParse {

    // In this case String query parse() method is used instead of PhraseQuery
    private static final Logger LOGGER = Logger.getLogger(SecondTaskLuceneWithParse.class.getName());


    public static void main(String[] args) throws IOException, ParseException {
        List<List<String>> fields = new ArrayList<>();
        // fieldName, testField or stringField -> "text" or "string", Field.Store.YES/NO
        fields.add(Arrays.asList("fileName", "string", "YES"));
        fields.add(Arrays.asList("fileContent", "text", "YES"));

        // the data can be entered with keyboard input but this way is more convenient
        List<List<String>> fieldValues = new ArrayList<>();
        fieldValues.add(Arrays.asList("file1", "to be or not to be that is the question"));
        fieldValues.add(Arrays.asList("file2", "make a long story short"));
        fieldValues.add(Arrays.asList("file3", "see eye to eye"));
        // create list of queries
        List<Pair<String, Integer>> userQueries = new ArrayList<>();
        userQueries.add(new Pair<>("to be not", 1));
        userQueries.add(new Pair<>("to or to", 1));
        userQueries.add(new Pair<>("to",1));
        userQueries.add(new Pair<>("long story short", 0));
        userQueries.add(new Pair<>("long short", 0));
        userQueries.add(new Pair<>("long short", 1));
        userQueries.add(new Pair<>("story long", 1));
        userQueries.add(new Pair<>("story long", 2));
        search(fields, fieldValues, userQueries);
    }


    private static void search(List<List<String>> fields, List<List<String>> fieldValues, List<Pair<String, Integer>> queries) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        CommonMethods.addDocs(w, fields, fieldValues);
        w.close();

        for(Pair<String,Integer> query : queries){
            // 2. query
            String queryString = query.getValue0();
            Integer maxSlop = query.getValue1();

            // Parse a simple query that searches for "something that u want to search":
            QueryParser parser = new QueryParser("fileContent", analyzer);
            // Query can be parsed with parser by entering Query in string
            String stringQuery = "\"" + queryString + "\"~" + maxSlop;
            Query q = parser.parse(stringQuery);


            // 3. search
            int hitsPerPage = 10;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            // 4. display results
            StringBuilder result = new StringBuilder();
            StringBuilder resultFiles = new StringBuilder().append("[");
            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document d = searcher.doc(docId);
                resultFiles.append(" ").append(d.get("fileName"));
            }
            resultFiles.append(" ]");
            // prepare result string
            result.append("\"").append(queryString).append("\"\t").append(maxSlop).append(" - ").append(resultFiles);
            // print the results
            System.out.println(result);

            // reader can only be closed when there
            // is no need to access the documents anymore.
            reader.close();
        }
    }
}