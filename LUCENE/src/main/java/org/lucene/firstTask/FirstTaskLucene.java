package org.lucene.firstTask;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class FirstTaskLucene {

    private static final Logger LOGGER = Logger.getLogger(FirstTaskLucene.class.getName());


    // TODO:
    //  - An index schema with a correct analyzer chain. // still to be done
    //  - return list of answers for every user's query. // DONE

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


    private static void search(List<List<String>> fields, List<List<String>> fieldValues, List<String> queries) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addDocs(w, fields, fieldValues);
        w.close();

        for(String query : queries){
            // 2. query
            String termString = prepareQueryRegexp(query);

            // prepare query
            Query q = new RegexpQuery(new Term("path", termString));
            LOGGER.info("Query regexp: " + termString);
            // Solution:
            // to avoid tokenization and keep the input string (in this case path) in one token and match Regexp keep
            // data field as StringField (NOT TextField because TextField is tokenized)
            // helpful link: https://stackoverflow.com/questions/35448522/regexpquery-in-lucene-not-working (first element in the numbering)


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

    private static String prepareQueryRegexp(String query){
        String preparedQuery;
        // match all strings which can be reduced to the input query (query's a subsequence of the path)
        preparedQuery = query.replace("", ".*").trim();
        return preparedQuery;
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