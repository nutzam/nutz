package org.nutz.dao.impl.sql;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.castor.Castors;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Lang;

/**
 * 仿照Spring JdbcTemplate实现nutz的SqlTemplate，方便sql的调用
 * 
 * @author hzl7652(hzl7652@sina.com)
 */
public class SqlTemplate {

    private Dao dao;

    public SqlTemplate() {}

    public SqlTemplate(Dao dao) {
        setDao(dao);
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Dao dao() {
        return this.dao;
    }

    /**
     * 执行一个SQL更新操作（如插入，更新或删除语句）。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param params
     *            参数map,无参数时，可为null
     * 
     * @return SQL 语句所影响的行数
     */
    public int update(String sql, Map<String, Object> params) {
        return update(sql, null, params);
    }

    /**
     * 执行一个SQL更新操作（如插入，更新或删除语句）。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return SQL 语句所影响的行数
     */
    public int update(String sql, Map<String, Object> vars, Map<String, Object> params) {
        Sql sqlObj = createSqlObj(sql, params);
        execute(sqlObj, vars, params);
        return sqlObj.getUpdateCount();
    }

    /**
     * 执行SQL批量更新操作（如插入，更新或删除语句）。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param batchValues
     *            批量更新参数集合
     * 
     * @return SQL 语句所影响的行数
     */
    public int batchUpdate(String sql, List<Map<String, Object>> batchValues) {
        return batchUpdate(sql, null, batchValues);
    }

    /**
     * 执行SQL批量更新操作（如插入，更新或删除语句）。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param batchValues
     *            批量更新参数集合
     * 
     * @return SQL 语句所影响的行数
     */
    public int batchUpdate(String sql,
                           Map<String, Object> vars,
                           List<Map<String, Object>> batchValues) {
        Sql sqlObj = null;
        if (batchValues != null && batchValues.size() > 0) {
            sqlObj = createSqlObj(sql, batchValues.get(0));
            for (Map<String, Object> params : batchValues) {
                Map<String, Object> newParams = paramProcess(params);
                sqlObj.params().putAll(newParams);
                sqlObj.addBatch();
            }
            dao.execute(sqlObj);
        } else {
            sqlObj = createSqlObj(sql, null);
            execute(sqlObj, vars, null);
        }

        return sqlObj.getUpdateCount();
    }

    /**
     * 执行一个SQL查询操作，结果为一个int形数值。
     * <p>
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return int数值，当查询为null时返回0
     */
    public int queryForInt(String sql, Map<String, Object> params) {
        return queryForInt(sql, null, params);
    }

    /**
     * 执行一个SQL查询操作，结果为一个int形数值。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return int数值，当查询为null时返回0
     */
    public int queryForInt(String sql, Map<String, Object> vars, Map<String, Object> params) {
        Sql sqlObj = createSqlObj(sql, params);
        sqlObj.setCallback(Sqls.callback.integer());
        execute(sqlObj, vars, params);
        return sqlObj.getInt();
    }

    /**
     * 执行一个SQL查询操作，结果为一个long形数值。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return long数值，当查询为null时返回0
     */
    public long queryForLong(String sql, Map<String, Object> params) {
        return queryForLong(sql, null, params);
    }

    /**
     * 执行一个SQL查询操作，结果为一个long形数值。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return long数值，当查询为null时返回0
     */
    public long queryForLong(String sql, Map<String, Object> vars, Map<String, Object> params) {
        Sql sqlObj = createSqlObj(sql, params);
        sqlObj.setCallback(Sqls.callback.longValue());
        execute(sqlObj, vars, params);
        Long result = sqlObj.getObject(Long.class);
        return result == null ? 0 : result;
    }

    /**
     * 执行一个SQL查询操作，结果为给定对象类型的对象，适用于明确SQL查询结果的类型。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param params
     *            参数map 无参数时，可为null
     * @param classOfT
     *            对象类型，SQL查询结果所对应的类型，如Date.class等
     * 
     * @return 对象，无查询结果时返回null
     */
    public <T> T queryForObject(String sql, Map<String, Object> params, Class<T> classOfT) {
        return queryForObject(sql, null, params, classOfT);
    }

    /**
     * 执行一个SQL查询操作，结果为给定对象类型的对象，适用于明确SQL查询结果的类型。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param params
     *            参数map，无参数时，可为null
     * @param classOfT
     *            对象类型，SQL查询结果所对应的类型，如Date.class等
     * 
     * @return 对象，无查询结果时返回null
     */
    public <T> T queryForObject(String sql,
                                Map<String, Object> vars,
                                Map<String, Object> params,
                                Class<T> classOfT) {
        Sql sqlObj = createSqlObj(sql, params);
        sqlObj.setCallback(new SqlCallback() {
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                if (null != rs && rs.next())
                    return rs.getObject(1);
                return null;
            }
        });

        execute(sqlObj, vars, params);
        return sqlObj.getObject(classOfT);
    }

    /**
     * 执行一个SQL查询操作，结果为给定实体的对象。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param params
     *            参数map，无参数时，可为null
     * @param entity
     *            实体类型，无参数时，可为null
     * 
     * @return 对象，无查询结果时返回null
     */
    public <T> T queryForObject(String sql, Map<String, Object> params, Entity<T> entity) {
        return queryForObject(sql, null, params, entity);
    }

    /**
     * 执行一个SQL查询操作，结果为给定实体的对象。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param params
     *            参数map，无参数时，可为null
     * @param entity
     *            实体类型
     * 
     * @return 对象，无查询结果时返回null
     */
    public <T> T queryForObject(String sql,
                                Map<String, Object> vars,
                                Map<String, Object> params,
                                Entity<T> entity) {
        Sql sqlObj = createSqlObj(sql, params);
        sqlObj.setCallback(Sqls.callback.entity());
        sqlObj.setEntity(entity);

        execute(sqlObj, vars, params);

        return sqlObj.getObject(entity.getType());
    }

    /**
     * 执行一个SQL查询操作，结果为Record的对象。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return Record对象，无查询结果时返回null
     */
    public Record queryForRecord(String sql, Map<String, Object> params) {
        return queryForRecord(sql, null, params);
    }

    /**
     * 执行一个SQL查询操作，结果为Record的对象。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param params
     *            参数map，无参数时，可为null
     * @return Record对象，无查询结果时返回null
     */
    public Record queryForRecord(String sql, Map<String, Object> vars, Map<String, Object> params) {
        Sql sqlObj = createSqlObj(sql, params);
        sqlObj.setCallback(Sqls.callback.record());

        execute(sqlObj, vars, params);

        return sqlObj.getObject(Record.class);
    }

    /**
     * 执行一个SQL查询操作，结果为一组对象。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param params
     *            参数map，无参数时，可为null
     * @param entity
     *            对象类型，无参数时，可为null
     * 
     * @return 对象列表，无查询结果时返回长度为0的List对象
     */
    public <T> List<T> query(String sql, Map<String, Object> params, Entity<T> entity) {
        return query(sql, null, params, entity);
    }

    /**
     * 执行一个SQL查询操作，结果为一组对象。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param params
     *            参数map，无参数时，可为null
     * @param classOfT
     *            对象类类
     * 
     * @return 对象列表，无查询结果时返回长度为0的List对象
     */
    public <T> List<T> query(String sql,
                             Map<String, Object> params,
                             Class<T> classOfT) {
        return query(sql, null, params, dao.getEntity(classOfT));
    }

    /**
     * 执行一个SQL查询操作，结果为一组对象。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param params
     *            参数map，无参数时，可为null
     * @param entity
     *            对象类型
     * 
     * @return 对象列表，无查询结果时返回长度为0的List对象
     */
    public <T> List<T> query(String sql,
                             Map<String, Object> vars,
                             Map<String, Object> params,
                             Entity<T> entity) {
        Sql sqlObj = createSqlObj(sql, params);
        sqlObj.setCallback(Sqls.callback.entities());
        sqlObj.setEntity(entity);

        execute(sqlObj, vars, params);

        return sqlObj.getList(entity.getType());
    }

    /**
     * 执行一个SQL查询操作，结果为一组对象。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param params
     *            参数map，无参数时，可为null
     * @param classOfT
     *            对象类型
     * 
     * @return 对象列表，无查询结果时返回长度为0的List对象
     */
    public <T> List<T> queryForList(String sql,
                                    Map<String, Object> vars,
                                    Map<String, Object> params,
                                    final Class<T> classOfT) {
        Sql sqlObj = createSqlObj(sql, params);

        sqlObj.setCallback(new SqlCallback() {
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                List<T> list = new ArrayList<T>();
                while (rs.next()) {
                    T result = Castors.me().castTo(rs.getObject(1), classOfT);
                    list.add(result);
                }
                return list;
            }
        });

        execute(sqlObj, vars, params);

        return sqlObj.getList(classOfT);
    }

    /**
     * 执行一个SQL查询操作，结果为Record对象列表。
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param vars
     *            变量map，无参数时，可为null
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return Record列表，无查询结果时返回长度为0的List对象
     */
    public List<Record> queryRecords(String sql,
                                     Map<String, Object> vars,
                                     Map<String, Object> params) {
        Sql sqlObj = createSqlObj(sql, params);
        sqlObj.setCallback(Sqls.callback.records());

        execute(sqlObj, vars, params);

        return sqlObj.getList(Record.class);
    }

    /**
     * 设置sql参数并执行sql。
     */
    private void execute(Sql sqlObj, Map<String, Object> vars, Map<String, Object> params) {
        if (vars != null)
            sqlObj.vars().putAll(vars);

        if (params != null) {
            Map<String, Object> newParams = paramProcess(params);
            sqlObj.params().putAll(newParams);
        }

        dao().execute(sqlObj);
    }

    /**
     * 创建Sql对象。
     * <p>
     * 在这里处理Array Collection类型参数，方便SQL IN 表达式的设置
     * 
     * @param sql
     *            包含变量占位符的SQL
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return Sql对象
     */
    private Sql createSqlObj(String sql, Map<String, Object> params) {

        if (params == null)
            return Sqls.create(sql);

        String newSql = sqlProcess(sql, params);

        return Sqls.create(newSql);
    }

    /**
     * 将Array Collection类型参数对应的sql占位符进行处理
     * 
     * @param originSql
     *            原包含变量占位符的SQL
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return 包含处理IN表达式的sql
     */
    private String sqlProcess(String originSql, Map<String, Object> params) {

        if (params == null || params.size() == 0)
            return originSql;

        String newSql = originSql;
        for (Entry<String, Object> entry : params.entrySet()) {
            String paramName = entry.getKey();
            Object paramObj = entry.getValue();

            if (paramObj.getClass().isArray()) {
                String inSqlExp = inSqlProcess(paramName, paramObj);
                newSql = newSql.replaceAll("@" + paramName, inSqlExp);
            }

            if (paramObj instanceof Collection) {
                Collection<?> collection = (Collection<?>) paramObj;
                Object[] paramVals = Lang.collection2array(collection);
                String inSqlExp = inSqlProcess(paramName, paramVals);
                newSql = newSql.replaceAll("@" + paramName, inSqlExp);
            }

        }

        return newSql;
    }

    /**
     * sql参数处理，在这里处理Array Collection类型参数，方便SQL IN 表达式的设置
     * 
     * @param params
     *            参数map，无参数时，可为null
     * 
     * @return 包含处理IN表达式的sql
     */
    private Map<String, Object> paramProcess(Map<String, Object> params) {
        if (params == null || params.size() == 0)
            return null;
        Map<String, Object> newParams = new HashMap<String, Object>(params);
        for (Entry<String, Object> entry : params.entrySet()) {
            String paramName = entry.getKey();
            Object paramObj = entry.getValue();

            if (paramObj.getClass().isArray()) {
                inParamProcess(paramName, paramObj, newParams);
                newParams.remove(paramName);
            }

            if (paramObj instanceof Collection) {
                Collection<?> collection = (Collection<?>) paramObj;
                Object[] paramVals = Lang.collection2array(collection);
                inParamProcess(paramName, paramVals, newParams);
                newParams.remove(paramName);
            }

        }

        return newParams;
    }

    private static String inSqlProcess(String paramName, Object paramObj) {
        int len = Array.getLength(paramObj);
        StringBuilder inSqlExp = new StringBuilder();
        for (int i = 0; i < len; i++) {
            inSqlExp.append("@").append(paramName).append(i).append(",");
        }
        inSqlExp.deleteCharAt(inSqlExp.length() - 1);

        return inSqlExp.toString();
    }

    private static void inParamProcess(String paramName, Object paramObj, Map<String, Object> newParams) {
        int len = Array.getLength(paramObj);
        for (int i = 0; i < len; i++) {
            String inParamName = paramName + i;
            newParams.put(inParamName, Array.get(paramObj, i));
        }
    }

}
