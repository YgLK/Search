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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer {
    private Directory index;
    private Analyzer analyzer;
    private Map<String, CustomField> fields;
    private List<CustomDocument> documents;

    public Indexer(String filePathWithDocuments) {
        this(filePathWithDocuments, new StandardAnalyzer());
    }

    public Indexer(String filePathWithDocuments, Analyzer _analyzer){
        // extract fields and documents from file
        HashMap<String, Object> indexerData = readDataFromFile(filePathWithDocuments);
        this.fields =  (Map<String, CustomField>) indexerData.get("fields");
        this.documents =  (List<CustomDocument>) indexerData.get("documents");
        this.analyzer = _analyzer;
        try {
            this.index = createIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    private HashMap<String, Object> readDataFromFile(String filePathWithDocuments) {
        HashMap<String, CustomField> extractedFields = new HashMap<>();
        List<CustomDocument> extractedDocuments = new ArrayList<>();

        JSONArray arr;
        try {
            JSONParser parser = new JSONParser();
            Object obj  = parser.parse(new FileReader(filePathWithDocuments));
            arr = new JSONArray();
            arr.add(obj);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        // retrieve object with data from json file
        JSONObject jsonObj  = (JSONObject) arr.get(0);

        // add fields
        JSONArray fieldsData = (JSONArray) jsonObj.get("fields");
        for (Object o : fieldsData)
        {
            JSONObject field = (JSONObject) o;

            String name = (String) field.get("name");
            String type = (String) field.get("type");
            Boolean fieldStore = (Boolean) field.get("fieldStore");

            extractedFields.put(
                    name,
                    new CustomField(name, type, fieldStore)
            );
        }

        // add documents
        JSONArray documentsData = (JSONArray) jsonObj.get("data");

        for (Object o : documentsData)
        {
            JSONObject document = (JSONObject) o;
            CustomDocument customDocument = new CustomDocument();
            for(String fieldName : extractedFields.keySet()){
                customDocument.addField(fieldName, (String) document.get(fieldName));
            }
            extractedDocuments.add(customDocument);
        }
        // return map with indexer data
        HashMap<String, Object> indexerData = new HashMap<>();
        indexerData.put("fields", extractedFields);
        indexerData.put("documents", extractedDocuments);
        return indexerData;
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
