package org.nutz.dao.sql;

import java.util.List;
import java.util.Set;

/**
 * 变量索引
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface VarIndex {

    int[] indexesOf(String name);

    String nameOf(int i);

    int[] getOrderIndex(String name);

    String getOrderName(int i);
    
    List<String> getOrders();

    Set<String> names();

    int size();

}