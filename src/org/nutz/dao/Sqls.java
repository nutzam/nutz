package org.nutz.dao;

import org.nutz.castor.Castors;
import org.nutz.dao.impl.sql.NutSql;
import org.nutz.dao.impl.sql.ValueEscaper;
import org.nutz.dao.impl.sql.callback.*;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.born.Borning;

/**
 * 提供了 Sql 相关的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Sqls {

    private static final ValueEscaper ES_FLD_VAL = new ValueEscaper();
    private static final ValueEscaper ES_SQL_FLD = new ValueEscaper();
    private static final ValueEscaper ES_CND_VAL = new ValueEscaper();

    private static Borning<? extends Sql> sqlBorning;

    static {
        ES_FLD_VAL.add('\'', "''").add('\\', "\\\\").ready();
        ES_SQL_FLD.add('\'', "''").add('\\', "\\\\").add('$', "$$").add('@', "@@").ready();
        ES_CND_VAL.add('\'', "''").add('\\', "\\\\").add('_', "\\_").add('%', "\\%").ready();

        setSqlBorning(NutSql.class);
    }

    /**
     * 改变 Sql 接口的实现类，如果你调用了这个方法，以后你再调用本类其他帮助函数创建的 SQL 就是你提供的这个实现类
     * <p>
     * 默认的，将用 org.nutz.dao.sql.impl.sql.NutSql 作为实现类
     * <p>
     * 你给出的 Sql 实现类必须有一个可访问的构造函数，接受一个字符串型参数
     * 
     * @param type
     *            你的 Sql 接口实现类
     */
    public static <T extends Sql> void setSqlBorning(Class<T> type) {
        sqlBorning = Mirror.me(type).getBorningByArgTypes(String.class);
    }

    /**
     * 创建了一个 Sql 对象。
     * <p>
     * 传入的 Sql 语句支持变量和参数占位符：
     * <ul>
     * <li>变量： 格式为 <b>$XXXX</b>，在执行前，会被预先替换
     * <li>参数： 格式为<b>@XXXX</b>，在执行前，会替换为 '?'，用以构建 PreparedStatement
     * </ul>
     * 
     * @param sql
     *            Sql 语句
     * @return Sql 对象
     * 
     * @see org.nutz.dao.sql.Sql
     */
    public static Sql create(String sql) {
        return sqlBorning.born(Lang.array(sql));
    }

    /**
     * 创建了一个 Sql 对象。
     * <p>
     * 传入的 Sql 语句支持变量和参数占位符：
     * <ul>
     * <li>变量： 格式为 <b>$XXXX</b>，在执行前，会被预先替换
     * <li>参数： 格式为<b>@XXXX</b>，在执行前，会替换为 '?'，用以构建 PreparedStatement
     * </ul>
     * 
     * @param fmt
     *            格式字符，格式参看 String.format 函数
     * @param args
     *            格式字符串的参数
     * @return Sql 对象
     */
    public static Sql createf(String fmt, Object... args) {
        return create(String.format(fmt, args));
    }

    /**
     * 创建一个获取单个实体对象的 Sql。
     * <p>
     * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取实体对象。
     * <p>
     * <b style=color:red>注意：</b>返回的 Sql 对象在执行前，一定要通过 setEntity 设置
     * 一个有效的实体，否则，会抛出异常。
     * 
     * @param sql
     *            Sql 语句
     * @return Sql 对象
     * 
     * @see org.nutz.dao.sql.Sql
     * @see org.nutz.dao.entity.Entity
     */
    public static Sql fetchEntity(String sql) {
        return create(sql).setCallback(callback.entity());
    }

    /**
     * 创建一个获取单个 Record 对象的 Sql。
     * <p>
     * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取实体对象。
     * 
     * @param sql
     *            Sql 语句
     * @return Sql 对象
     * 
     * @see org.nutz.dao.sql.Sql
     * @see org.nutz.dao.entity.Entity
     */
    public static Sql fetchRecord(String sql) {
        return create(sql).setCallback(callback.record());
    }

    /**
     * 创建一个获取整数的 Sql。
     * <p>
     * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取整数值。
     * <p>
     * <b style=color:red>注意：</b>你的 Sql 语句返回的 ResultSet 的第一列必须是数字
     * 
     * @param sql
     *            Sql 语句
     * @return Sql 对象
     * 
     * @see org.nutz.dao.sql.Sql
     */
    public static Sql fetchInt(String sql) {
        return create(sql).setCallback(callback.integer());
    }

    /**
     * 创建一个获取长整数的 Sql。
     * <p>
     * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取长整数值。
     * <p>
     * <b style=color:red>注意：</b>你的 Sql 语句返回的 ResultSet 的第一列必须是数字
     * 
     * @param sql
     *            Sql 语句
     * @return Sql 对象
     * 
     * @see org.nutz.dao.sql.Sql
     */
    public static Sql fetchLong(String sql) {
        return create(sql).setCallback(callback.longValue());
    }

    /**
     * 创建一个获取字符串的 Sql。
     * <p>
     * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取字符串。
     * <p>
     * <b style=color:red>注意：</b>你的 Sql 语句返回的 ResultSet 的第一列必须是字符串
     * 
     * @param sql
     *            Sql 语句
     * @return Sql 对象
     * 
     * @see org.nutz.dao.sql.Sql
     */
    public static Sql fetchString(String sql) {
        return create(sql).setCallback(callback.str());
    }

    /**
     * 创建一个获取一组实体对象的 Sql。
     * <p>
     * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取一组实体对象。
     * <p>
     * <b style=color:red>注意：</b>返回的 Sql 对象在执行前，一定要通过 setEntity 设置
     * 一个有效的实体，否则，会抛出异常。
     * 
     * @param sql
     *            Sql 语句
     * @return Sql 对象
     * 
     * @see org.nutz.dao.sql.Sql
     * @see org.nutz.dao.entity.Entity
     */
    public static Sql queryEntity(String sql) {
        return create(sql).setCallback(callback.entities());
    }

    /**
     * 创建一个获取一组 Record 实体对象的 Sql。
     * <p>
     * 这个函数除了执行 create(String)外，还会为这个 Sql 语句设置回调，用来获取一组实体对象。
     * 
     * @param sql
     *            Sql 语句
     * @return Sql 对象
     */
    public static Sql queryRecord(String sql) {
        return create(sql).setCallback(callback.records());
    }

    /**
     * 一些内置的回调对象
     */
    public static CallbackFactory callback = new CallbackFactory();

    public static class CallbackFactory {
        /**
         * @return 从 ResultSet获取一个对象的回调对象
         */
        public SqlCallback entity() {
            return new FetchEntityCallback();
        }

        /**
         * @return 从 ResultSet 获取一个 Record 的回调对象
         */
        public SqlCallback record() {
            return new FetchRecordCallback();
        }

        /**
         * @return 从 ResultSet 获取一个整数的回调对象
         */
        public SqlCallback integer() {
            return new FetchIntegerCallback();
        }

        /**
         * @return 从 ResultSet 获取一个长整型数的回调对象
         */
        public SqlCallback longValue() {
            return new FetchLongCallback();
        }

        /**
         * @return 从 ResultSet 获取一个字符串的回调对象
         */
        public SqlCallback str() {
            return new FetchStringCallback();
        }

        /**
         * @return 从 ResultSet 获得一个整数数组的回调对象
         */
        public SqlCallback ints() {
            return new QueryIntCallback();
        }

        /**
         * @return 从 ResultSet 获得一个长整型数组的回调对象
         */
        public SqlCallback longs() {
            return new QueryLongCallback();
        }

        /**
         * @return 从 ResultSet 获得一个字符串数组的回调对象
         */
        public SqlCallback strs() {
            return new QueryStringArrayCallback();
        }

        /**
         * @return 从 ResultSet 获得一个字符串列表的回调对象
         */
        public SqlCallback strList() {
            return new QueryStringCallback();
        }

        /**
         * @return 从 ResultSet获取一组对象的回调对象
         */
        public SqlCallback entities() {
            return new QueryEntityCallback();
        }

        /**
         * @return 从 ResultSet 获取一组 Record 的回调对象
         */
        public SqlCallback records() {
            return new QueryRecordCallback();
        }
        
        public SqlCallback bool() {
            return new FetchBooleanCallback();
        }
        
        public SqlCallback bools() {
            return new QueryBooleanCallback();
        }
    }

    /**
     * 格式化值，根据值的类型，生成 SQL 字段值的部分，它会考虑 SQL 注入
     * 
     * @param v
     *            字段值
     * @return 格式化后的 Sql 字段值，可以直接拼装在 SQL 里面
     */
    public static CharSequence formatFieldValue(Object v) {
        if (null == v)
            return "NULL";
        else if (Sqls.isNotNeedQuote(v.getClass()))
            return Sqls.escapeFieldValue(v.toString());
        else
            return new StringBuilder("'").append(Sqls.escapeFieldValue(Castors.me().castToString(v))).append('\'');
    }

    /**
     * 格式化值，根据值的类型，生成 SQL 字段值的部分，它会考虑 SQL 注入，以及 SQL 的 '$' 和 '@' 转义
     * 
     * @param v
     *            字段值
     * @return 格式化后的 Sql 字段值，可以直接拼装在 SQL 里面
     */
    public static CharSequence formatSqlFieldValue(Object v) {
        if (null == v)
            return "NULL";
        else if (Sqls.isNotNeedQuote(v.getClass()))
            return Sqls.escapeSqlFieldValue(v.toString());
        else
            return new StringBuilder("'").append(Sqls.escapeSqlFieldValue(v.toString()))
                                            .append('\'');
    }

    /**
     * 将 SQL 的字段值进行转意，可以用来防止 SQL 注入攻击
     * 
     * @param s
     *            字段值
     * @return 格式化后的 Sql 字段值，可以直接拼装在 SQL 里面
     */
    public static CharSequence escapeFieldValue(CharSequence s) {
        if (null == s)
            return null;
        return ES_FLD_VAL.escape(s);
    }

    /**
     * 将 SQL 的字段值进行转意，可以用来防止 SQL 注入攻击，<br>
     * 同时，它也会将 Sql 的特殊标记 '$' 和 '@' 进行转译
     * 
     * @param s
     *            字段值
     * @return 格式化后的 Sql 字段值，可以直接拼装在 SQL 里面
     */
    public static CharSequence escapeSqlFieldValue(CharSequence s) {
        if (null == s)
            return null;
        return ES_SQL_FLD.escape(s);
    }

    /**
     * 将 SQL 的 WHERE 条件值进行转意，可以用来防止 SQL 注入攻击
     * 
     * @param s
     *            字段值
     * @return 格式化后的 Sql 字段值，可以直接拼装在 SQL 里面
     */
    public static CharSequence escapteConditionValue(CharSequence s) {
        if (null == s)
            return null;
        return ES_CND_VAL.escape(s);
    }

    /**
     * 判断一个值，在 SQL 中是否需要单引号
     * 
     * @param type
     *            类型
     * @return 是否需要加上单引号
     */
    public static boolean isNotNeedQuote(Class<?> type) {
        Mirror<?> me = Mirror.me(type);
        return me.isBoolean() || me.isPrimitiveNumber();
    }

}
