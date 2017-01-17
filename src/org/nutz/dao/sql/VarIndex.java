package org.nutz.dao.sql;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 变量索引
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface VarIndex extends Serializable {

    int[] indexesOf(String name);

    String nameOf(int i);

    int[] getOrderIndex(String name);

    String getOrderName(int i);
    
    List<String> getOrders();

    Set<String> names();

    int size();

}