package org.nutz.dao.impl.entity;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityIndex;

public class NutEntityIndex implements EntityIndex {

    private boolean unique;

    private String name;

    private List<EntityField> fields;

    public NutEntityIndex() {
        this.fields = new ArrayList<EntityField>(3);
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addField(EntityField field) {
        fields.add(field);
    }

    public List<EntityField> getFields() {
        return fields;
    }

}
