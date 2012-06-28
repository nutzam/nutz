package org.nutz.dao.impl.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.LinkVisitor;
import org.nutz.lang.Strings;

/**
 * 提供一个帮助类，统一处理几种映射字段集合的常用操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class LinkFieldSet {

    private ArrayList<LinkField> lnks;

    private Map<String, ArrayList<LinkField>> cache;

    public LinkFieldSet() {
        lnks = new ArrayList<LinkField>(3);
        cache = new HashMap<String, ArrayList<LinkField>>();
    }

    void add(LinkField lnk) {
        lnks.add(lnk);
    }

    public List<LinkField> getAll() {
        return lnks;
    }

    List<LinkField> visit(Object obj, String regex, LinkVisitor visitor) {
        List<LinkField> list = getList(regex);
        if (null != visitor)
            for (LinkField lnk : list)
                visitor.visit(obj, lnk);
        return list;
    }

    List<LinkField> getList(String regex) {
        ArrayList<LinkField> list;
        if (Strings.isBlank(regex)) {
            list = lnks;
        } else {
            list = cache.get(regex);
            if (null == list) {
                synchronized (cache) {
                    list = cache.get(regex);
                    if (null == list) {
                        list = new ArrayList<LinkField>(lnks.size());
                        for (LinkField lnk : lnks)
                            if (Pattern.matches(regex, lnk.getName()))
                                list.add(lnk);
                        list.trimToSize();
                        cache.put(regex, list);
                    }
                }
            }
        }
        return list;
    }

}
