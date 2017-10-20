package org.nutz.dao.impl.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.sql.VarIndex;
import org.nutz.lang.util.LinkedIntArray;

class VarIndexImpl implements VarIndex {

    private static final long serialVersionUID = 1L;

    private Map<String, LinkedIntArray> indexes;

    private Map<Integer, String> names;

    private ArrayList<String> orders;

    VarIndexImpl() {
        indexes = new LinkedHashMap<String, LinkedIntArray>();
        names = new LinkedHashMap<Integer, String>();
        orders = new ArrayList<String>();
    }

    void add(String name, int index) {
        LinkedIntArray lia = indexes.get(name);
        if (null == lia) {
            lia = new LinkedIntArray();
            indexes.put(name, lia);
        }
        lia.push(index);
        names.put(index, name);
        orders.add(name);
    }

    Collection<LinkedIntArray> values() {
        return indexes.values();
    }

    public int[] getOrderIndex(String name) {
        LinkedIntArray re = new LinkedIntArray(orders.size());
        int i = 0;
        for (String od : orders) {
            if (od.equals(name))
                re.push(i);
            i++;
        }
        return re.toIntArray();
    }

    public List<String> getOrders() {
        return orders;
    }

    public String getOrderName(int i) {
        return orders.get(i);
    }

    public String nameOf(int i) {
        return names.get(i);
    }

    public int[] indexesOf(String name) {
        LinkedIntArray lia = indexes.get(name);
        if (null == lia)
            return null;
        return lia.toIntArray();
    }

    public Set<String> names() {
        return indexes.keySet();
    }

    public int size() {
        return indexes.size();
    }
}
