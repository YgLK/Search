package org.lucene;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelloLucene {

    // code source
    //    https://www.lucenetutorial.com/lucene-in-5-minutes.html
    // (didn't work so couple modifications were done)


    public static void main(String[] args) throws IOException, ParseException {
        List<List<String>> fields = new ArrayList<>();
        // fieldName, testField or stringField -> "text" or "string", Field.Store.YES/NO
//        fields.add(Arrays.asList("title", "text", "YES"));
        fields.add(Arrays.asList("title", "string", "YES"));
        fields.add(Arrays.asList("isbn", "string", "YES"));
        // doc values
        List<List<String>> fieldValues = new ArrayList<>();
        fieldValues.add(Arrays.asList("Lucene in Action", "193398817"));
        fieldValues.add(Arrays.asList("Lucene for Dummies", "55320055Z"));
        fieldValues.add(Arrays.asList("lucene/queryparser/docs/xml/img/plus.gif", "55063554A"));
        fieldValues.add(Arrays.asList("The Art of Computer Science", "9900333X"));
        search(fields, fieldValues);
    }


    private static void search(List<List<String>> fields, List<List<String>> fieldValues) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addDocs(w, fields, fieldValues);
        w.close();

        // 2. query
        String querystr = "/.*luc.*for.*/";

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
//        Query q = new QueryParser("title", analyzer).parse(querystr);

        // - - - - - - - - - - - - -
        // test wildcard query
        Query q = new RegexpQuery(new Term("title", ".*l.*q.*d.*o.*c.*s.*p.*l.*g.*i.*")); // <-- here we have the solution
        // Solution:
        // to avoid tokenization and keep the input string (in this case path) in one token and match Regexp keep
        // data field as StringField (NOT TextField because TextField is tokenized)

        // it fails even though on regexr.com "Lucene in Action" is accepted - ask Andrei about this task
        // - - - - - - - - - - - - -

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
        }

        // reader can only be closed when there
        // is no need to access the documents anymore.
        reader.close();
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