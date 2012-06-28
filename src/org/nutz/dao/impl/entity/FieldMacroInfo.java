package org.nutz.dao.impl.entity;

import org.nutz.dao.DB;
import org.nutz.dao.entity.MacroType;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.SQL;

/**
 * 封装对 '@El' 以及 '@SQL' 注解
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class FieldMacroInfo {

    private MacroType type;

    /**
     * 数据库类型
     */
    private DB db;

    /**
     * 值，可能是一个表达式或者 SQL 语句
     */
    private String value;

    public FieldMacroInfo(EL el) {
        type = MacroType.EL;
        db = el.db();
        value = el.value();
    }

    public FieldMacroInfo(SQL sql) {
        type = MacroType.SQL;
        db = sql.db();
        value = sql.value();
    }

    public boolean isEl() {
        return MacroType.EL == type;
    }

    public boolean isSql() {
        return MacroType.SQL == type;
    }

    public DB getDb() {
        return db;
    }

    public String getValue() {
        return value;
    }

    public void setDb(DB db) {
        this.db = db;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
