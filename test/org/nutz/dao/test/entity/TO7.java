package org.nutz.dao.test.entity;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.SimpleColumn;

@SimpleColumn
public class TO7 {
    @Id
    private Integer primaryKey;

    @Name
    @SimpleColumn('#')
    private String foreignKey;

    private String simpleColumn;

    @SimpleColumn('-')
    private String hasSimpleColumnAnno;

    public Integer getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getSimpleColumn() {
        return simpleColumn;
    }

    public void setSimpleColumn(String simpleColumn) {
        this.simpleColumn = simpleColumn;
    }

    public String getHasSimpleColumnAnno() {
        return hasSimpleColumnAnno;
    }

    public void setHasSimpleColumnAnno(String hasSimpleColumnAnno) {
        this.hasSimpleColumnAnno = hasSimpleColumnAnno;
    }
}
