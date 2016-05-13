package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;

public class FetchMultiEntitySqlCallback implements SqlCallback {

    protected Map<String, Entity<?>> mappings = new LinkedHashMap<String, Entity<?>>();
    
    public FetchMultiEntitySqlCallback() {
    }
    
    public FetchMultiEntitySqlCallback(Entity<?> ... entites) {
        for (Entity<?> en : entites) {
            addEntity(en);
        }
    }
    
    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        if (!rs.next())
            return null;
        int size = mappings.size();
        String[] tableNames = mappings.keySet().toArray(new String[size]);
        Entity<?>[] entites = mappings.values().toArray(new Entity<?>[size]);
        return next(mappings, rs, tableNames, entites);
    }
    
    public void addEntity(Entity<?> en) {
        putEntity(en.getViewName(), en);
    }
    
    public void putEntity(String tableName, Entity<?> en) {
        mappings.put(tableName, en);
    }
    
    public static Object[] next(Map<String, Entity<?>> mappings, 
                         ResultSet rs, String[] tableNames, 
                         Entity<?>[] entites) throws SQLException {
        
        int size = mappings.size();
        
        Object[] objs = new Object[size+1];
        for (int i = 0; i < size; i++) {
            if (entites[i] instanceof NutEntity)
                objs[i] = entites[i].born(rs);
            else
                objs[i] = entites[i].getMirror().born();
        }
        Record re = new Record();
        objs[size] = re;
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        
        for (int i = 1; i <= count; i++) {
            String name = meta.getColumnLabel(i);
            String tableName = meta.getTableName(i);
            int index = Arrays.binarySearch(tableNames, tableName);
            if (index > -1) {
                MappingField mf = entites[i].getColumn(name);
                if (mf != null) {
                    mf.injectValue(objs[i], rs, null);
                    continue;
                }
                // 用Record兜底
                switch (meta.getColumnType(i)) {
                case Types.TIMESTAMP: {
                    re.put(name, rs.getTimestamp(i));
                    break;
                }
                case Types.DATE: {// ORACLE的DATE类型包含时间,如果用默认的只有日期没有时间 from
                                    // cqyunqin
                    re.put(name, rs.getTimestamp(i));
                    break;
                }
                case Types.CLOB: {
                    re.put(name, rs.getString(i));
                    break;
                }
                default:
                    re.put(name, rs.getObject(i));
                    break;
                }
            }
        }
        return objs;
    }
}
