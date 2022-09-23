package org.lucene;

import java.util.HashMap;
import java.util.Map;

public class CustomDocument {
    private Map<String, String> fields;

    public CustomDocument(Map<String, String> _fields){
        this.fields = _fields;
    }

    public CustomDocument(){
        this.fields = new HashMap<>();
    }

    public CustomDocument addField(String _fieldName, String  _fieldValue){
        this.fields.put(_fieldName, _fieldValue);
        return this;
    }

    public void removeField(String _fieldName){
        this.fields.remove(_fieldName);
    }


    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
