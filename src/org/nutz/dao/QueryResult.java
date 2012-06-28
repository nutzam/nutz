package org.nutz.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.dao.pager.Pager;

/**
 * 封装了一个分页查询的结果集合，包括本页数据列表以及分页信息
 * <p/><b>虽然这个类实现了Serializable,但需要注意的是,list的值必须也是Serializable的,才能确保整个对象可Serializable</b>
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.pager.Pager
 */
public class QueryResult implements Serializable {

    private static final long serialVersionUID = 5104522523949248573L;
    private List<?> list;
    private Pager pager;

    public QueryResult() {}

    public QueryResult(List<?> list, Pager pager) {
        this.list = list;
        this.pager = pager;
    }

    public List<?> getList() {
        return list;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(Class<T> eleType) {
        return (List<T>) list;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> convertList(Class<T> eleType) {
        if (null == list || list.isEmpty())
            return (List<T>) list;

        List<T> re = new ArrayList<T>(list.size());
        Castors castors = Castors.me();
        for (Object obj : list)
            re.add(castors.castTo(obj, eleType));

        return re;
    }

    public QueryResult setList(List<?> list) {
        this.list = list;
        return this;
    }

    public Pager getPager() {
        return pager;
    }

    public QueryResult setPager(Pager pager) {
        this.pager = pager;
        return this;
    }

}
