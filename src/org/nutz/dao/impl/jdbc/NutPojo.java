package org.nutz.dao.impl.jdbc;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.Chain;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.NutStatement;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.PItem;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.Lang;

public class NutPojo extends NutStatement implements Pojo {

    private PojoCallback before;

    private PojoCallback after;

    /**
     * 参数表
     */
    private LinkedList<Object> params;

    /**
     * 缓存语句共包括的参数数量
     */
    private int _pmnum;

    /**
     * 当前操作对象
     */
    private Object obj;

    /**
     * 供子类访问的语句元素
     */
    protected ArrayList<PItem> items;

    public NutPojo() {
        super();
        params = new LinkedList<Object>();
        items = new ArrayList<PItem>(6);
        _pmnum = -1;
        append(Pojos.Items.sqlType());
    }

    public ValueAdaptor[] getAdaptors() {
        ValueAdaptor[] adaptors = new ValueAdaptor[_params_count()];
        int i = 0;
        for (PItem item : items)
            i = item.joinAdaptor(getEntity(), adaptors, i);
        return adaptors;
    }

    public Object[][] getParamMatrix() {
        Object[][] re;
        /*
         * 木有参数对象，但是有参数，循环一下，看看元素们会给出什么样的参数
         */
        if (_params_count() > 0 && params.isEmpty()) {
            re = new Object[1][_params_count()];
            int i = 0;
            for (PItem item : items)
                i = item.joinParams(getEntity(), null, re[0], i);
        }
        /*
         * 依照参数列表循环获取参数矩阵
         */
        else {
            re = new Object[params.size()][_params_count()];
            int row = 0;
            for (Object obj : params) {
                int i = 0;
                for (PItem item : items)
                    i = item.joinParams(getEntity(), obj, re[row], i);
                row++;
            }
        }
        return re;
    }

    public String toPreparedStatement() {
        StringBuilder sb = new StringBuilder();
        for (PItem item : items)
            item.joinSql(getEntity(), sb);
        return sb.toString();
    }

    public void onBefore(Connection conn) throws SQLException {
        if (null != before)
            before.invoke(conn, null, this);
    }

    public void onAfter(Connection conn, ResultSet rs) throws SQLException {
        if (null != after)
            getContext().setResult(after.invoke(conn, rs, this));
    }

    public Pojo setBefore(PojoCallback before) {
        this.before = before;
        return this;
    }

    public Pojo setAfter(PojoCallback after) {
        this.after = after;
        return this;
    }

    public Pojo setPager(Pager pager) {
        this.getContext().setPager(pager);
        return this;
    }

    public Pojo addParamsBy(Object obj) {
        if (null == obj)
            return this;

        // 集合
        if (obj instanceof Collection<?>)
            for (Object ele : (Collection<?>) obj)
                addParamsBy(ele);
        // 数组
        else if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++)
                addParamsBy(Array.get(obj, i));
        }
        // 链: 变成 Map
        else if (obj instanceof Chain)
            params.add(((Chain) obj).updateBy(this.getEntity()).toMap());
        // 迭带器 : TODO 以后是不是考虑 params 也变成迭代器，这样可以允许无限多的对象被执行 ...
        else if (obj instanceof Iterator<?>) {
            Iterator<?> it = (Iterator<?>) obj;
            while (it.hasNext())
                addParamsBy(it.next());
        }
        // 其他对象，直接保存，占一行
        else
            params.add(obj);

        return this;
    }

    public Object getLastParams() {
        return params.isEmpty() ? null : params.getLast();
    }

    public List<Object> params() {
        return params;
    }

    public Object getOperatingObject() {
        return obj;
    }

    public Pojo setOperatingObject(Object obj) {
        this.obj = obj;
        return this;
    }

    public Pojo clear() {
        this.params.clear();
        return this;
    }

    public Pojo append(PItem... itemAry) {
        if (null != itemAry)
            for (PItem item : itemAry) {
                if (null != item) {
                    items.add(item);
                    item.setPojo(this);
                }
            }
        return this;
    }

    public Pojo insertFirst(PItem... itemAry) {
        items.addAll(0, Lang.list(itemAry));
        for (PItem pi : itemAry)
            pi.setPojo(this);
        return this;
    }

    public Pojo setItem(int index, PItem pi) {
        items.set(index, pi);
        pi.setPojo(this);
        return this;
    }

    public PItem getItem(int index) {
        return items.get(index);
    }

    public Pojo removeItem(int index) {
        items.remove(index);
        return this;
    }

    @Override
    public NutPojo setSqlType(SqlType sqlType) {
        return (NutPojo) super.setSqlType(sqlType);
    }

    public String toString() {
        if (SqlType.RUN == this.getSqlType()) {
            return this.getSqlType().name()
                   + (null == before ? "" : " :before{...}")
                   + (null == after ? "" : " :after{...}");
        }
        return super.toString();
    }

    public Pojo duplicate() {
        throw Lang.noImplement();
    }

    private int _params_count() {
        if (_pmnum < 0) {
            _pmnum = 0;
            Entity<?> en = getEntity();
            for (PItem item : items) {
                _pmnum += item.paramCount(en);
            }
        }
        return _pmnum;
    }
}
