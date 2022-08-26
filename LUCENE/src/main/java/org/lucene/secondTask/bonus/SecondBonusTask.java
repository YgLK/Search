package org.lucene.secondTask.bonus;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.queries.spans.SpanNearQuery;
import org.apache.lucene.queries.spans.SpanQuery;
import org.apache.lucene.queries.spans.SpanTermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.lucene.CommonMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Done nicely,

Same recommendations as in FirstBonusTask
 */
public class SecondBonusTask {


    public static void main(String[] args) throws IOException {
        List<List<String>> fields = new ArrayList<>();
        // fieldName, testField or stringField -> "text" or "string", Field.Store.YES/NO
        fields.add(Arrays.asList("fileName", "string", "YES"));
        fields.add(Arrays.asList("fileContent", "text", "YES"));

        // the data can be entered with keyboard input but this way is more convenient
        List<List<String>> fieldValues = new ArrayList<>();
        fieldValues.add(Arrays.asList("file1", "to be or not to be that is the question"));
        fieldValues.add(Arrays.asList("file2", "make a long story short"));
        fieldValues.add(Arrays.asList("file3", "make a long story sho"));
        fieldValues.add(Arrays.asList("file4", "make a long short story"));

        // custom query "long story sho*" put in the search() method code
        search(fields, fieldValues);
    }


    private static void search(List<List<String>> fields, List<List<String>> fieldValues) throws IOException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        CommonMethods.addDocs(w, fields, fieldValues);
        w.close();

        // 2. query
        Integer maxSlop = 0;

        WildcardQuery wildcard = new WildcardQuery(new Term("fileContent", "sho*"));
        SpanQuery spanWildcard = new SpanMultiTermQueryWrapper<>(wildcard);

        //  "long story sho*" with the same order as in the query
        SpanNearQuery q = new SpanNearQuery(new SpanQuery[] {
                new SpanTermQuery(new Term("fileContent", "long")),
                new SpanTermQuery(new Term("fileContent", "story")),
                spanWildcard},
                maxSlop,
                true);

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
        result.append("\"").append("long story sho*").append("\"\t").append(maxSlop).append(" - ").append(resultFiles);
        // print the results
        System.out.println(result);

        // reader can only be closed when there
        // is no need to access the documents anymore.
        reader.close();
    }
}