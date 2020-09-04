package org.nutz.dao.sql;

import org.nutz.dao.Condition;

public interface OrderBy extends Condition,PItem {

    OrderBy asc(String name);

    OrderBy desc(String name);
    
    OrderBy orderBy(String name, String dir);
}
