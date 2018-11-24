package org.nutz.dao.impl.entity;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.TableName;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityIndex;
import org.nutz.lang.segment.CharSegment;

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

    public String getName(Entity<?> en) {
        if (name.contains("$"))
            return TableName.render(new CharSegment(name));
        else if (name.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(isUnique() ? "UX_" : "IX_");
            sb.append(en.getTableName());
            for (EntityField field : getFields())
                sb.append("_").append(field.getName());
            return sb.toString();
        } else
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
