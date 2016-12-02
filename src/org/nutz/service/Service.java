package org.nutz.service;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象的Service类. <b>辅助类,并非强制要求继承</b><p/>
 * <b>子类不应该也切勿再声明一个dao属性,以避免重复的属性,导致Ioc注入时混乱</b>
 * @author wendal(wendal1985@gmail.com)
 *
 */
public abstract class Service {

    /**
     * 新建Service,仍需要在调用setDao传入Dao实例才算完整
     */
    public Service() {}

    /**
     * 新建Service并同时传入Dao实例
     * @param dao Dao实例,不应该为null
     */
    public Service(Dao dao) {
        this.dao = dao;
    }

    private Dao dao;

    /**
     * 设置Dao实例
     * @param dao
     */
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    /**
     * 获取Dao实例
     * @return Dao 实例
     */
    public Dao dao() {
        return dao;
    }

    /**
     * @function 查询RECORD集合
     * @author yuro.v DateTime 2015-10-31 下午11:09:50
     * @param sqlStr
     * @return
     */
    protected List<Record> queryRecordes(String sqlStr) {

        Sql sql = Sqls.create(sqlStr);

        sql.setCallback(new SqlCallback() {

            @Override
            public Object invoke(Connection arg0, ResultSet rs, Sql arg2)
                    throws SQLException {
                List<Record> records = new ArrayList<Record>();
                while (rs.next()) {
                    records.add(Record.create(rs));
                }
                return records;
            }
        });
        dao.execute(sql);
        return sql.getList(Record.class);
    }

    /**
     * @function 分页查询RECORD集合
     * @author yuro.v DateTime 2015-10-31 下午11:09:52
     * @param sqlStr
     * @param pageNum
     * @param pageSize
     * @return
     */
    protected List<Record> queryRecordes(String sqlStr, int pageNum,
                                         int pageSize) {

        final List<Record> records = new ArrayList<Record>();
        // oracle 分页
        int end = pageNum * pageSize;
        int start = (pageNum - 1) * pageSize + 1;
        sqlStr = "SELECT *  FROM (SELECT T.*,ROWNUM NUM  FROM ( " + sqlStr
                + ") T  WHERE ROWNUM<=" + end + "   )  WHERE NUM >= " + start;

        Sql sql = Sqls.create(sqlStr);

        sql.setCallback(new SqlCallback() {

            @Override
            public Object invoke(Connection arg0, ResultSet rs, Sql arg2)
                    throws SQLException {
                while (rs.next()) {
                    records.add(Record.create(rs));
                }
                return null;
            }
        });
        dao.execute(sql);
        return records;
    }

    /**
     * @function 获取记录数
     * @author yuro.v DateTime 2015-11-11 上午11:14:30
     * @param sqlStr
     * @return
     * @throws Exception
     */
    protected int count(String sqlStr) throws Exception {
        Sql sql = Sqls.create(sqlStr);
        sql.setCallback(new SqlCallback() {

            @Override
            public Object invoke(Connection connection, ResultSet rs, Sql sql)
                    throws SQLException {
                int count = 0;
                while (rs.next())
                    count = rs.getInt(1);
                return count;
            }
        });
        dao.execute(sql);
        return sql.getInt();
    }

    protected String fetchString(String sqlStr) {
        Sql sql = Sqls.create(sqlStr);
        sql.setCallback(new SqlCallback() {

            @Override
            public Object invoke(Connection connection, ResultSet rs, Sql sql)
                    throws SQLException {
                String result = null;
                while(rs.next()){
                    result = rs.getString(1);
                }
                return result;
            }
        });

        dao.execute(sql);
        return sql.getString();
    }
}
