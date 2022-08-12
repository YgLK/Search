package org.lucene.secondTask;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class SecondTaskLuceneWithParse {

    // In this case String query parse() method is used instead of PhraseQuery

    private static final Logger LOGGER = Logger.getLogger(SecondTaskLuceneWithPhraseQuery.class.getName());


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
        addDocs(w, fields, fieldValues);
        w.close();

        for(Pair<String,Integer> query : queries){
            // 2. query
            String queryString = query.getValue0();
            Integer maxSlop = query.getValue1();

            // Parse a simple query that searches for "something that u want to search":
            QueryParser parser = new QueryParser("fileContent", analyzer);
            // Query can be parsed with parser by entering Query in string
            String stringQuery = "\"" + queryString + "\"~" + maxSlop;
            Query query2 = parser.parse(stringQuery);


            // 3. search
            int hitsPerPage = 10;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(query2, hitsPerPage);
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


    private static void displayTokens(Analyzer analyzer, String text) throws IOException {
        TokenStream stream = analyzer.tokenStream(null, new StringReader(text));
        CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            System.out.println(cattr.toString());
        }
        stream.end();
        stream.close();
    }



    private static void addDocs(IndexWriter w, List<List<String>> fields, List<List<String>> docsFieldValues) throws IOException {

        for(List<String> docFieldValues : docsFieldValues){
            Document doc = new Document();
            for(List<String> fieldData : fields){
                // get field store enum value
                Field.Store fieldStore;
                if(fieldData.get(2).equals("YES")){
                    fieldStore = Field.Store.YES;
                } else {
                    fieldStore = Field.Store.NO;
                }

                // add doc
                String fieldName = fieldData.get(0);
                String fieldValue = docFieldValues.get(fields.indexOf(fieldData));
                // declare fields
                if(fieldData.get(1).equals("text")){
                    doc.add(new TextField(fieldName, fieldValue, fieldStore));
                } else if(fieldData.get(1).equals("string")){
                    // use a string field for isbn because we don't want it tokenized
                    doc.add(new StringField(fieldName, fieldValue, fieldStore));
                }
            }
            w.addDocument(doc);
        }
    }
}