package org.nutz.dao.impl.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;

import org.nutz.dao.DaoException;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.PojoMaker;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

public class NutPojoMaker implements PojoMaker {

    private JdbcExpert expert;

    public NutPojoMaker(JdbcExpert expert) {
        this.expert = expert;
    }

    public Pojo makePojo(SqlType type) {
        return expert.createPojo(type);
    }

    public Pojo makeInsert(final Entity<?> en) {
        Pojo pojo = Pojos.pojo(expert, en, SqlType.INSERT);
        pojo.setEntity(en);
        pojo.append(Pojos.Items.entityTableName());
        pojo.append(Pojos.Items.insertFields());
        pojo.append(Pojos.Items.insertValues());
        if (expert.isSupportAutoIncrement()) {
        	MappingField mf = en.getIdField();
        	if (mf != null && mf.isAutoIncreasement()) {
        		pojo.setAfter(new GeneratedKeys());
        		pojo.getContext().attr("RETURN_GENERATED_KEYS", true);
        	}
        }
        return pojo;
    }

    public Pojo makeUpdate(Entity<?> en, Object refer) {
        Pojo pojo = Pojos.pojo(expert, en, SqlType.UPDATE);
        pojo.setEntity(en);
        pojo.append(Pojos.Items.entityTableName());
        pojo.append(Pojos.Items.updateFields(refer));
        return pojo;
    }

    public Pojo makeQuery(Entity<?> en) {
        Pojo pojo = Pojos.pojo(expert, en, SqlType.SELECT);
        pojo.setEntity(en);
        pojo.append(Pojos.Items.queryEntityFields());
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.entityViewName());
        return pojo;
    }

    public Pojo makeQuery(String tableName) {
        String[] ss = tableName.split(":");
        // String idFieldName = ss.length > 1 ? ss[1] : "*";//按id字段来统计,比较快
        Pojo pojo = makePojo(SqlType.SELECT);
        // pojo.append(Pojos.Items.wrap(idFieldName));//与org.nutz.dao.test.normal.QueryTest.query_records_pager()冲突
        pojo.append(Pojos.Items.wrap("*"));
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.wrap(ss[0]));
        return pojo;
    }

    public Pojo makeDelete(Entity<?> en) {
        Pojo pojo = Pojos.pojo(expert, en, SqlType.DELETE);
        pojo.setEntity(en);
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.entityTableName());
        return pojo;
    }

    public Pojo makeDelete(String tableName) {
        Pojo pojo = makePojo(SqlType.DELETE);
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.wrap(tableName));
        return pojo;
    }

    public Pojo makeFunc(String tableName, String funcName, String colName) {
        Pojo pojo = makePojo(SqlType.SELECT);
        pojo.append(Pojos.Items.wrapf("%s(%s) FROM %s", funcName, colName, tableName));
        return pojo;
    }

    static class GeneratedKeys implements PojoCallback {
    	
    	public Object invoke(Connection conn, ResultSet rs, final Pojo pojo, Statement stmt) throws SQLException {
			final ResultSet _rs = stmt.getGeneratedKeys();
			Object obj = pojo.getOperatingObject();
			if (obj instanceof Map) {
			    obj = Arrays.asList(obj);
			}
			Lang.each(obj, new Each<Object>() {
				public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop, LoopException {
					try {
						if (!_rs.next())
							throw new ExitLoop();
						Object key = _rs.getObject(1);
						pojo.getEntity().getIdField().setValue(ele, key);
					} catch (SQLException e) {
						throw new DaoException(e);
					}
				}
			});
			return pojo.getOperatingObject();
		}
    }
}
