package org.nutz.dao.sql;

import org.nutz.dao.entity.Entity;

public interface PojoMaker {

    Pojo makePojo(SqlType type);

    Pojo makeInsert(Entity<?> en);

    Pojo makeUpdate(Entity<?> en, Object refer);

    Pojo makeQuery(Entity<?> en);

    Pojo makeQuery(String tableName);
    
    Pojo makeQuery(String tableName, String fields);

    Pojo makeDelete(Entity<?> en);

    Pojo makeDelete(String tableName);

    Pojo makeFunc(String tableName, String funcName, String colName);

    Pojo makeQueryByJoin(Entity<?> en, String regex);
    
    Pojo makeCountByJoin(Entity<?> en, String regex);
}
