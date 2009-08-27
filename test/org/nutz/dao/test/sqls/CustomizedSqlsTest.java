package org.nutz.dao.test.sqls;

import static org.junit.Assert.*;

import java.sql.ResultSet;

import org.junit.Test;

import org.nutz.dao.ExecutableSql;
import org.nutz.dao.QuerySql;
import org.nutz.dao.TableName;
import org.nutz.dao.callback.SqlCallback;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.meta.Tank;
import org.nutz.trans.Atom;

public class CustomizedSqlsTest extends DaoCase {


	@Override
	protected void after() {}

	@Override
	protected void before() {
		((NutDao) dao).setSqlManager(new FileSqlManager("org/nutz/dao/test/sqls/exec.sqls"));
	}

	@Test
	public void test_dynamic_insert() {
		pojos.init();
		int platoonId = 23;
		pojos.initPlatoon(platoonId);
		dao.execute(dao.sqls().createSql(ExecutableSql.class, "tank.insert").set(".id", platoonId)
				.set("code", "T1").set("weight", 12));
		dao.execute(dao.sqls().createSql(ExecutableSql.class, "tank.insert").set(".id", platoonId)
				.set("code", "T2").set("weight", 13));
		dao.execute(dao.sqls().createSql(ExecutableSql.class, "tank.insert").set(".id", platoonId)
				.set("code", "T3").set("weight", 14));
		dao.execute(dao.sqls().createSql(ExecutableSql.class, "tank.insert").set(".id", platoonId)
				.set("code", "T4").set("weight", 15));
		TableName.run(platoonId, new Atom() {
			public void run() {
				assertEquals(4, dao.count(Tank.class));
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_dynamic_query() {
		pojos.init();
		Platoon p = pojos.create4Platoon(Base.make("xyz"), "GG");
		QuerySql<Tank> sql = dao.sqls().createSql(QuerySql.class, "tank.query");
		sql.set(".id", p.getId());
		sql.setCallback(new SqlCallback<Tank, ResultSet>() {
			public Tank invoke(ResultSet rs) throws Exception {
				Tank t = Tank.make(rs.getString("code"));
				t.setId(rs.getInt("id"));
				t.setWeight(rs.getInt("weight"));
				return t;
			}
		});
		dao.execute(sql);
		assertEquals(2, sql.getResult().size());
	}
}