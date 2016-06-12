package org.nutz.dao.sql;

import org.nutz.dao.Condition;
import org.nutz.dao.sql.PItem;

public interface OrderBy extends Condition,PItem {

    OrderBy asc(String name);

    OrderBy desc(String name);
    
    OrderBy orderBy(String name, String dir);
}
