package org.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.queries.spans.SpanNearQuery;
import org.apache.lucene.queries.spans.SpanQuery;
import org.apache.lucene.queries.spans.SpanTermQuery;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class Searcher {

    private static final Logger LOGGER = Logger.getLogger(Searcher.class.getName());

    protected Directory index;
    protected Analyzer analyzer;

    public Searcher(Directory _index){
        this.index = _index;
        this.analyzer = new StandardAnalyzer();
    }


    public Searcher(Directory _index, Analyzer _analyzer){
        this.index = _index;
        this.analyzer = _analyzer;
    }

    // - - - TASK 1 - - -
    public List<String> findPaths(String queryString, String searchField) throws IOException {
        // 2. query
        Query q = getPathQuery(queryString, searchField);
        return findMatchingDocuments(queryString, searchField, q);
    }

    // - - - TASK 2 - - -
    public List<String> searchWithSlop(String queryString, String searchField, int maxSlop) throws Exception {
        // 2. query
        Query q = getPhraseQuery(queryString, searchField, maxSlop);
        return findMatchingDocuments(queryString, searchField, q);
    }

    // - - - TASK 2 - BONUS - - -
    public List<String> searchWithPrefix(String queryString, String searchField, int maxSlop, boolean inOrder) throws Exception {
        // 2. query
        Query q = getPrefixQuery(queryString, searchField, maxSlop, inOrder);
        return findMatchingDocuments(queryString, searchField, q);
    }

    // - - - TASK 3 - - -
    public List<String> searchWithCustomAnalyzer(String queryString, String searchField) throws Exception {
        // 2. query
        Query q = getCustomAnalyzerQuery(queryString, searchField);
        return findMatchingDocuments(queryString, searchField, q);
    }

    private Query getPathQuery(String queryString, String searchField){
        // 2. query
        String termString = prepareRegexpQuery(queryString);
        // prepare query
        return new RegexpQuery(new Term(searchField, termString));
        // ALTERNATIVE (remember to change line in the prepareQuery method too in case of using the WildcardQuery):
        // Query q = new WildcardQuery(new Term("path", termString));
    }

    protected static String prepareRegexpQuery(String query){
        String preparedQuery;
        // match all strings which can be reduced to the input query (query's a subsequence of the path)
        preparedQuery = query.replace("", ".*").trim();
        // when WildCardQuery used uncomment the following line:
        // preparedQuery = query.replace("", "*").trim();
        return preparedQuery;
    }

    protected Query getPhraseQuery(String queryString, String searchField, int maxSlop) {
        // prepare query
        String[] terms = queryString.split(" ");

        // 'terms' must be an array of strings since there is 'String... terms' parameter in the constructor
        return new PhraseQuery(maxSlop,searchField, terms);
    }

    protected Query getPrefixQuery(String queryString, String searchField, int maxSlop, boolean inOrder) {
        List<String> queryTokens = Arrays.asList(queryString.split(" "));
        HashMap<String, SpanQuery> spanQueries = new HashMap<>();
        queryTokens.forEach(word -> {
            SpanQuery spanQuery;
            if(word.contains("*")) {
                WildcardQuery wildcard = new WildcardQuery(new Term(searchField, word));
                spanQuery = new SpanMultiTermQueryWrapper<>(wildcard);
            } else {
                spanQuery = new SpanTermQuery(new Term(searchField, word));
            }
            spanQueries.put(word, spanQuery);
        });

        List<SpanQuery> spanQueriesFinalOrder = new ArrayList<>();
        queryTokens.forEach(t -> spanQueriesFinalOrder.add(spanQueries.get(t)));

        SpanQuery[] spanQueriesArray = new SpanQuery[ spanQueriesFinalOrder.size() ];
        spanQueriesFinalOrder.toArray(spanQueriesArray);

        return new SpanNearQuery(spanQueriesArray, maxSlop, inOrder);
    }

    private Query getCustomAnalyzerQuery(String query, String searchField){
        // 2. query
        StringBuilder result =  new StringBuilder();
        try(TokenStream tokenStream = getAnalyzer().tokenStream(searchField, query)){
            // get the CharTermAttribute from the TokenStream
            CharTermAttribute termAtt = tokenStream.addAttribute(CharTermAttribute.class);

            tokenStream.reset();
            // print all tokens until stream is exhausted
            while (tokenStream.incrementToken()) {
                result.append(termAtt.toString());
            }
            tokenStream.end();
        } catch (Exception ex){
            LOGGER.warning(String.valueOf(ex));
        }
        // prepare query
        return new TermQuery(new Term(searchField, result.toString()));
    }

    protected List<String> findMatchingDocuments(String queryString, String searchField,Query q) throws IOException {
        // 3. search
        List<String> results = new ArrayList<>();
        StringBuilder resultStr = new StringBuilder();

        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        // 4. display results
        resultStr.append("- - - - - QUERY: ").append(queryString).append(" - - - - -\n");
        resultStr.append("Found ").append(hits.length).append(" hits.\n");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            resultStr.append(i + 1).append(". ").append(d.get(searchField)).append("\n");
            results.add(d.get(searchField));
        }
        resultStr.append("- - - - - - - - - - - - - - - - - - - -\n");
        reader.close();
        LOGGER.info(resultStr.toString());
        return results;
    }

    public Directory getIndex() {
        return index;
    }

    public void setIndex(Directory index) {
        this.index = index;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }
}
