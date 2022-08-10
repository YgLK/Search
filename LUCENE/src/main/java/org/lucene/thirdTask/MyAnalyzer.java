package org.lucene.thirdTask;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

public class MyAnalyzer extends Analyzer {

    private final String concatenationTokenFilterDelimiter;

    final List<String> stopWords =
            Arrays.asList(
                    "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is",
                    "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there",
                    "these", "they", "this", "to", "was", "will", "with");

    public MyAnalyzer(){
        // default delimiter
        this.concatenationTokenFilterDelimiter = " ";
    }

    public MyAnalyzer(String delimiter) {
        // custom delimiter
        this.concatenationTokenFilterDelimiter = delimiter;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new WhitespaceTokenizer();
        TokenStream stream = new StopFilter(tokenizer, StopFilter.makeStopSet(stopWords));
        // create concatenationTokenFilter with custom delimiter
        stream = new ConcatenationTokenFilter(stream, concatenationTokenFilterDelimiter);
        return new TokenStreamComponents(tokenizer, stream);
    }


    public static void main(String[] args) throws IOException {
        // text to tokenize
        final String text = "This is a demo of the TokenStream API";

        MyAnalyzer analyzer = new MyAnalyzer("-_-");
        TokenStream stream = analyzer.tokenStream("field", new StringReader(text));

        // get the CharTermAttribute from the TokenStream
        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        try {
            stream.reset();

            // print all tokens until stream is exhausted
            while (stream.incrementToken()) {
                System.out.println(termAtt.toString());
            }

            stream.end();
        } finally {
            stream.close();
        }
    }
}
 