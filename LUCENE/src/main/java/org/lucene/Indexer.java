package org.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Indexer {
    private Directory index;
    private Analyzer analyzer;
    private Map<String, CustomField> fields;
    private List<CustomDocument> documents;

    public Indexer(Map<String, CustomField> _fields, List<CustomDocument> _documents){
        this(_fields, _documents, new StandardAnalyzer());
    }

    public Indexer(Map<String, CustomField> _fields, List<CustomDocument> _documents, Analyzer _analyzer){
        this.fields = _fields;
        this.documents = _documents;
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        this.analyzer = _analyzer;
        try {
            this.index = createIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Directory createIndex() throws IOException {
        // 1. create the index
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addDocs(w);
        w.close();

        return index;
    }

    public void addDocs(IndexWriter w) throws IOException {
        for(CustomDocument document : documents){
            Document doc = new Document();
            for(Map.Entry<String, String> fieldNameValue : document.getFields().entrySet()){
                CustomField customField = fields.get(fieldNameValue.getKey());
                String fieldValue = fieldNameValue.getValue();

                Field.Store fieldStore = customField.isStore() ? Field.Store.YES : Field.Store.NO;

                String fieldType =  customField.getType();
                if(fieldType.equals("text")){
                    doc.add(new TextField(customField.getName(), fieldValue, fieldStore));
                } else if(fieldType.equals("string")){
                    // use a string field for isbn because we don't want it tokenized
                    doc.add(new StringField(customField.getName(), fieldValue, fieldStore));
                }
            }
            w.addDocument(doc);
        }
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Directory getIndex() {
        return index;
    }

    public void setIndex(Directory index) {
        this.index = index;
    }

    public Map<String, CustomField> getFields() {
        return fields;
    }

    public void setFields(Map<String, CustomField> fields) {
        this.fields = fields;
    }

    public List<CustomDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<CustomDocument> documents) {
        this.documents = documents;
    }
}
