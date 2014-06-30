package org.nutz.dao.sql;

import org.nutz.lang.util.LinkedIntArray;

import java.util.List;
import java.util.Map;
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

    Map<String, LinkedIntArray> getName2IndexMap();

    Map<Integer, String> getIndex2NameMap();
}