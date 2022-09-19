package org.lucene;

public class CustomField {
    private final String name;
    private final String type;
    private final boolean store;

    public CustomField(String _name, String _type, boolean _store){
        this.name = _name;
        this.type = _type;
        this.store = _store;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isStore() {
        return store;
    }
}
