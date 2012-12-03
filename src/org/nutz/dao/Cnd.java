package org.nutz.dao;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.cnd.SimpleCondition;
import org.nutz.dao.util.cri.Exps;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;

/**
 * 是 Condition 的一个实现，这个类给你比较方便的方法来构建 Condition 接口的实例。
 * 
 * <h4>在 Dao 接口中使用</h4><br>
 * 
 * 比如一个通常的查询:
 * <p>
 * List<Pet> pets = dao.query(Pet.class,
 * Cnd.where("name","LIKE","B%").asc("name"), null);
 * 
 * <h4>链式赋值示例</h4><br>
 * Cnd.where("id", ">", 34).and("name","LIKE","T%").asc("name"); <br>
 * 相当于<br>
 * WHERE id>34 AND name LIKE 'T%' ORDER BY name ASC
 * <p>
 * Cnd.orderBy().desc("id"); <br>
 * 相当于<br>
 * ORDER BY id DESC
 * 
 * <h4 style=color:red>你还需要知道的是:</h4><br>
 * <ul>
 * <li>你设置的字段名，是 java 的字段名 -- 如果 Entity 里有，那么会被转换成数据库字段名
 * <li>如果你设置的是 entity 中不存在的 java 字段名，则被认为是数据库字段名，将直接使用
 * <li>你的值，如果是字符串，或者其他类字符串对象（某种 CharSequence），那么在转换成 SQL 时，会正确被单引号包裹
 * <li>你的值如果是不可理解的自定义对象，会被转化成字符串处理
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.dao.Condition
 */
public class Cnd implements OrderBy, Criteria {

    /*------------------------------------------------------------------*/
    public static Condition format(String format, Object... args) {
        return Strings.isBlank(format) ? null : new SimpleCondition(format, args);
    }

    public static Condition wrap(String str) {
        return Strings.isBlank(str) ? null : new SimpleCondition((Object) str);
    }

    public static Condition wrap(String sql, Object value) {
        return Strings.isBlank(sql) ? null : new SimpleCondition(new CharSegment(sql).setBy(value));
    }

    public static SqlExpression exp(String name, String op, Object value) {
        return Exps.create(name, op, value);
    }

    public static SqlExpressionGroup exps(String name, String op, Object value) {
        return exps(exp(name, op, value));
    }

    public static SqlExpressionGroup exps(SqlExpression exp) {
        return new SqlExpressionGroup().and(exp);
    }

    public static Cnd where(String name, String op, Object value) {
        return new Cnd(Cnd.exp(name, op, value));
    }

    public static Cnd where(SqlExpression e) {
        return new Cnd(e);
    }

    public static SimpleCriteria cri() {
        return new SimpleCriteria();
    }

    public static OrderBy orderBy() {
        return new Cnd();
    }

    public static Cnd limit() {
        return new Cnd();
    }

    /*------------------------------------------------------------------*/

    private SimpleCriteria cri;

    Cnd() {
        cri = new SimpleCriteria();
    }

    protected Cnd(SqlExpression exp) {
        this();
        cri.where().and(exp);
    }

    public OrderBy asc(String name) {
        cri.asc(name);
        return this;
    }

    public OrderBy desc(String name) {
        cri.desc(name);
        return this;
    }

    public Cnd and(SqlExpression exp) {
        cri.where().and(exp);
        return this;
    }

    public Cnd and(String name, String op, Object value) {
        return and(Cnd.exp(name, op, value));
    }

    public Cnd or(SqlExpression exp) {
        cri.where().or(exp);
        return this;
    }

    public Cnd or(String name, String op, Object value) {
        return or(Cnd.exp(name, op, value));
    }

    public Cnd andNot(SqlExpression exp) {
        cri.where().and(exp.setNot(true));
        return this;
    }

    public Cnd andNot(String name, String op, Object value) {
        return andNot(Cnd.exp(name, op, value));
    }

    public Cnd orNot(SqlExpression exp) {
        cri.where().or(exp.setNot(true));
        return this;
    }

    public Cnd orNot(String name, String op, Object value) {
        return orNot(Cnd.exp(name, op, value));
    }

    public Pager getPager() {
        return cri.getPager();
    }

    public String toSql(Entity<?> en) {
        return cri.toSql(en);
    }

    public boolean equals(Object obj) {
        return cri.equals(obj);
    }

    public String toString() {
        return cri.toString();
    }

    public void setPojo(Pojo pojo) {
        cri.setPojo(pojo);
    }

    public Pojo getPojo() {
        return cri.getPojo();
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        cri.joinSql(en, sb);
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        return cri.joinAdaptor(en, adaptors, off);
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        return cri.joinParams(en, obj, params, off);
    }

    public int paramCount(Entity<?> en) {
        return cri.paramCount(en);
    }

    public SqlExpressionGroup where() {
        return cri.where();
    }

    public OrderBy getOrderBy() {
        return cri.getOrderBy();
    }

    public Cnd limit(int pageNumber, int pageSize) {
        cri.setPager(pageNumber, pageSize);
        return this;
    }

    public Cnd limit(int pageSize) {
        cri.setPager(1, pageSize);
        return this;
    }

    public Cnd limit(Pager pager) {
        cri.setPager(pager);
        return this;
    }

}
