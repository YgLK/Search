package org.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class CommonMethods {

    public static void addDocs(IndexWriter w, List<List<String>> fields, List<List<String>> docsFieldValues) throws IOException {

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

    public static void displayTokens(Analyzer analyzer, String text) throws IOException {
        TokenStream stream = analyzer.tokenStream(null, new StringReader(text));
        CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            System.out.println(cattr.toString());
        }
        stream.end();
        stream.close();
    }

}
