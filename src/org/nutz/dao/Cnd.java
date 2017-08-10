package org.nutz.dao;

import java.lang.reflect.Array;
import java.util.Collection;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.SimpleNesting;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.GroupBy;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.cnd.SimpleCondition;
import org.nutz.dao.util.cri.Exps;
import org.nutz.dao.util.cri.NestExps;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Callback2;

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
 * <p/> <b>带括号的条件语句<b/> where (name="wendal" or age<18) and location != "地球" <p/>
 * <code>Cnd.where(Cnd.exps("name", "=", "wendal").or("age", "<", 18)).and("location", "!=", "地球")</code>
 * 
 * <p/><b>静态条件,直接拼入sql,不做任何转义. Oracle的日期传Date对象,而非用to_date等数据库方法</b><p/>
 * <code>Cnd.where(new Static("ct < to_date('2015-06-26')")).and(...........) </code>
 * <p/>
 * 
 * <p/><b>between用法</b><p/>
 * <code>Cnd.where("age", "between", new Object[]{19,29}).and(...........) </code>
 * <p/>
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
public class Cnd implements OrderBy, Criteria, GroupBy {

    private static final long serialVersionUID = 1L;

    /**
     * 用字符串和参数格式化出一个条件语句,注意,不会抹除特殊字符
     * @param format sql条件
     * @param args 参数
     * @return 条件对象
     */
    public static Condition format(String format, Object... args) {
        return Strings.isBlank(format) ? null : new SimpleCondition(format,
                                                                    args);
    }

    /***
     * 直接用字符串生成一个条件对象
     * @param str sql条件
     * @return 条件对象
     */
    public static Condition wrap(String str) {
        return Strings.isBlank(str) ? null : new SimpleCondition((Object) str);
    }

    /**
     * 使用CharSegment拼装一个条件对象
     * @param sql sql模板
     * @param value 参数
     * @return 条件对象
     * @see org.nutz.lang.segment.CharSegment
     */
    public static Condition wrap(String sql, Object value) {
        return Strings.isBlank(sql) ? null
                                   : new SimpleCondition(new CharSegment(sql).setBy(value));
    }

    /**
     * 生成一个条件表达式
     * @param name Java属性或字段名称
     * @param op   操作符,可以是 = like 等等
     * @param value 参数值.
     * @return 条件表达式
     */
    public static SqlExpression exp(String name, String op, Object value) {
    	if(value!=null && value instanceof Nesting){
    		return NestExps.create(name, op, (Nesting) value);
    	}
        return Exps.create(name, op, value);
    }

    /**
     * 生成一个条件表达式组
     * @param name Java属性或字段名称
     * @param op   操作符,可以是 = like 等等
     * @param value 参数值.
     * @return 条件表达式组
     */
    public static SqlExpressionGroup exps(String name, String op, Object value) {
        return exps(exp(name, op, value));
    }

    /**
     * 将一个条件表达式封装为条件表达式组
     * @param exp 原本的条件表达式
     * @return 条件表达式组
     */
    public static SqlExpressionGroup exps(SqlExpression exp) {
        return new SqlExpressionGroup().and(exp);
    }

    /**
     * 生成一个新的Cnd实例
     * @param name java属性或字段名称, 推荐用Java属性
     * @param op 操作符,可以是= like等等
     * @param value 参数值. 如果操作符是between,参数值需要是new Object[]{12,39}形式
     * @return Cnd实例
     */
    public static Cnd where(String name, String op, Object value) {
        return new Cnd(Cnd.exp(name, op, value));
    }

    /**
     * 用一个条件表达式构建一个Cnd实例
     * @param e 条件表达式
     * @return Cnd实例
     */
    public static Cnd where(SqlExpression e) {
        return new Cnd(e);
    }

    /**
     * 生成一个简单条件对象
     */
    public static SimpleCriteria cri() {
        return new SimpleCriteria();
    }

    /**
     * 单纯生成一个Orderby条件
     * @return OrderBy实例
     */
    public static OrderBy orderBy() {
        return new Cnd();
    }

    /**
     * @return 一个 Cnd 的实例
     * @deprecated Since 1.b.50 不推荐使用这个函数构建 Cnd 的实例，因为看起来语意不明的样子
     */
    public static Cnd limit() {
        return new Cnd();
    }

    /**
     * @return 一个 Cnd 的实例
     */
    public static Cnd NEW() {
        return new Cnd();
    }

    /**
     * 用SimpleCriteria生成一个Cnd实例
     * @param cri SimpleCriteria实例
     * @return Cnd实例
     */
    public static Cnd byCri(SimpleCriteria cri) {
        return new Cnd().setCri(cri);
    }

    /*------------------------------------------------------------------*/

    private SimpleCriteria cri;

    Cnd() {
        cri = new SimpleCriteria();
    }

    private Cnd setCri(SimpleCriteria cri) {
        this.cri = cri;
        return this;
    }

    /**
     * 获取内部的where属性
     * @return SimpleCriteria实例
     */
    public SimpleCriteria getCri() {
        return cri;
    }

    protected Cnd(SqlExpression exp) {
        this();
        cri.where().and(exp);
    }

    /**
     * 按Java属性/字段属性进行升序. <b>不进行SQL特殊字符抹除<b/>  cnd.asc("age")
     * @param name Java属性/字段属性
     */
    public OrderBy asc(String name) {
        cri.asc(name);
        return this;
    }
    
    /**
     * 按Java属性/字段属性进行降序. <b>不进行SQL特殊字符抹除<b/> cnd.desc("age")
     * @param name Java属性/字段属性
     */
    public OrderBy desc(String name) {
        cri.desc(name);
        return this;
    }

    /**
     * 当dir为asc时判断为升序,否则判定为降序. cnd.orderBy("age", "asc")
     * @param name Java属性/字段属性
     * @param dir asc或其他
     * @return OrderBy实例,事实上就是当前对象
     */
    public OrderBy orderBy(String name, String dir) {
        if ("asc".equalsIgnoreCase(dir)) {
            this.asc(name);
        } else {
            this.desc(name);
        }
        return this;
    }

    /**
     * Cnd.where(...).and(Cnd.exp(.........)) 或 Cnd.where(...).and(Cnd.exps(.........))
     * @param exp 条件表达式
     * @return 当前对象,用于链式调用
     */
    public Cnd and(SqlExpression exp) {
        cri.where().and(exp);
        return this;
    }

    /**
     * Cnd.where(...).and("age", "<", 40)
     * @param name Java属性或字段名称,推荐用Java属性,如果有的话
     * @param op 操作符,可以是 = like等
     * @param value 参数值, 如果是between的话需要传入new Object[]{19,28}
     * @return 当前对象,用于链式调用
     */
    public Cnd and(String name, String op, Object value) {
        return and(Cnd.exp(name, op, value));
    }

    /**
     * Cnd.where(...).or(Cnd.exp(.........)) 或 Cnd.where(...).or(Cnd.exps(.........))
     * @param exp 条件表达式
     * @return 当前对象,用于链式调用
     */
    public Cnd or(SqlExpression exp) {
        cri.where().or(exp);
        return this;
    }

    /**
     * Cnd.where(...).or("age", "<", 40)
     * @param name Java属性或字段名称,推荐用Java属性,如果有的话
     * @param op 操作符,可以是 = like等
     * @param value 参数值, 如果是between的话需要传入new Object[]{19,28}
     * @return 当前对象,用于链式调用
     */
    public Cnd or(String name, String op, Object value) {
        return or(Cnd.exp(name, op, value));
    }

    /**
     * and一个条件表达式并且取非
     * @param exp 条件表达式
     * @return 当前对象,用于链式调用
     */
    public Cnd andNot(SqlExpression exp) {
        cri.where().and(exp.setNot(true));
        return this;
    }

    /**
     * and一个条件,并且取非
     * @param name Java属性或字段名称,推荐用Java属性,如果有的话
     * @param op 操作符,可以是 = like等
     * @param value 参数值, 如果是between的话需要传入new Object[]{19,28}
     * @return 当前对象,用于链式调用
     */
    public Cnd andNot(String name, String op, Object value) {
        return andNot(Cnd.exp(name, op, value));
    }

    /**
     * @see Cnd#andNot(SqlExpression)
     */
    public Cnd orNot(SqlExpression exp) {
        cri.where().or(exp.setNot(true));
        return this;
    }

    /**
     * @see Cnd#andNot(String, String, Object)
     */
    public Cnd orNot(String name, String op, Object value) {
        return orNot(Cnd.exp(name, op, value));
    }

    /**
     * 获取分页对象,默认是null
     */
    public Pager getPager() {
        return cri.getPager();
    }

    /**
     * 根据实体Entity将本对象转化为sql语句, 条件表达式中的name属性将转化为数据库字段名称
     */
    public String toSql(Entity<?> en) {
        return cri.toSql(en);
    }

    /**
     * 判断两个Cnd是否相等
     */
    public boolean equals(Object obj) {
        return cri.equals(obj);
    }

    /**
     * 直接转为SQL语句, 如果setPojo未曾调用, 条件表达式中的name属性未映射为数据库字段
     */
    public String toString() {
        return cri.toString();
    }

    /**
     * 关联的Pojo,可以用于toString时的name属性映射
     */
    public void setPojo(Pojo pojo) {
        cri.setPojo(pojo);
    }

    /**
     * 获取已设置的Pojo, 默认为null
     */
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

    /**
     * 获取Cnd中的where部分,注意,对SqlExpressionGroup的修改也会反映到Cnd中,因为是同一个对象
     */
    public SqlExpressionGroup where() {
        return cri.where();
    }

    /**
     * 分组
     * @param names java属性或数据库字段名称
     */
    public GroupBy groupBy(String... names) {
        cri.groupBy(names);
        return this;
    }

    /**
     * 分组中的having条件
     * @param cnd 条件语句
     */
    public GroupBy having(Condition cnd) {
        cri.having(cnd);
        return this;
    }

    /**
     * 单独获取排序条件,建议使用asc或desc,而非直接取出排序条件. 取出的对象仅包含分组条件, 不包含where等部分
     */
    public OrderBy getOrderBy() {
        return cri.getOrderBy();
    }

    /**
     * 分页
     * @param pageNumber 页数, 若小于1则代表全部记录
     * @param pageSize 每页数量
     * @return 当前对象,用于链式调用
     */
    public Cnd limit(int pageNumber, int pageSize) {
        cri.setPager(pageNumber, pageSize);
        return this;
    }

    /**
     * 设置每页大小,并设置页数为1
     * @param pageSize 每页大小
     * @return 当前对象,用于链式调用
     */
    @Deprecated
    public Cnd limit(int pageSize) {
        cri.setPager(1, pageSize);
        return this;
    }

    /**
     * 直接设置分页对象, 可以new Pager或dao.createPager得到
     * @param pager 分页对象
     * @return 当前对象,用于链式调用
     */
    public Cnd limit(Pager pager) {
        cri.setPager(pager);
        return this;
    }
    
    protected static FieldMatcher dftFromFieldMatcher = new FieldMatcher().setIgnoreNull(true).setIgnoreZero(true);
    
    /**
     * 用默认规则(忽略零值和空值)生成Cnd实例
     * @param dao Dao实例,不能为null
     * @param obj 对象, 若为null,则返回值为null, 不可以是Class/字符串/数值/布尔类型
     * @return Cnd实例
     */
    public static Cnd from(Dao dao, Object obj) {
        return from(dao, obj, dftFromFieldMatcher);
    }
    
    /**
     * 根据一个对象生成Cnd条件, FieldMatcher详细控制.<p/>
     * <code>assertEquals(" WHERE name='wendal' AND age=0", Cnd.from(dao, pet, FieldMatcher.make("age|name", null, true).setIgnoreDate(true)).toString());</code>
     * @param dao Dao实例
     * @param obj 基对象,不可以是Class,字符串,数值和Boolean
     * @param matcher 过滤字段属性, 可配置哪些字段可用/不可用/是否忽略空值/是否忽略0值/是否忽略java.util.Date类及其子类的对象/是否忽略@Id所标注的主键属性/是否忽略 \@Name 所标注的主键属性/是否忽略 \@Pk 所引用的复合主键 
     * @return Cnd条件
     */
    public static Cnd from(Dao dao, Object obj, FieldMatcher matcher) {
        final SqlExpressionGroup exps = new SqlExpressionGroup();
        boolean re = Daos.filterFields(obj, matcher, dao, new Callback2<MappingField, Object>() {
            public void invoke(MappingField mf, Object val) {
                exps.and(mf.getName(), "=", val);
            }
        });
        if (re)
            return Cnd.where(exps);
        return null;
    }
    
    /**
     * 若value为null/空白字符串/空集合/空数组,则本条件不添加.
     * @see Cnd#and(String, String, Object)
     */
    public Cnd andEX(String name, String op, Object value) {
        return and(Cnd.expEX(name, op, value));
    }
    
    /**
     * 若value为null/空白字符串/空集合/空数组,则本条件不添加.
     * @see Cnd#or(String, String, Object)
     */
    public Cnd orEX(String name, String op, Object value) {
        return or(Cnd.expEX(name, op, value));
    }
    
    public static SqlExpression expEX(String name, String op, Object value) {
        if (_ex(value))
            return null;
        return Cnd.exp(name, op, value);
    }

    @SuppressWarnings("rawtypes")
    public static boolean _ex(Object value) {
        return value == null
                || (value instanceof CharSequence && Strings.isBlank((CharSequence)value))
                || (value instanceof Collection && ((Collection)value).isEmpty())
                || (value.getClass().isArray() && Array.getLength(value) == 0);
    }
    
    public GroupBy getGroupBy() {
        return cri.getGroupBy();
    }
    
    /**
     * 构造一个可嵌套条件，需要dao支持才能映射类与表和属性与列
     */
    public static Nesting nst(Dao dao){
    	return new SimpleNesting(dao);
    }
}
