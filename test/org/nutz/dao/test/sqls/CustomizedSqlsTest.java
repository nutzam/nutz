package org.nutz.dao.test.sqls;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.dao.TableName;
import org.nutz.dao.sql.SQLs;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
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
		Sql sql = dao.sqls().create("tank.insert");
		sql.vars().set("id", platoonId);
		sql.params().set("code", "T1").set("weight", 12);
		dao.execute(sql);

		sql = dao.sqls().create("tank.insert");
		sql.vars().set("id", platoonId);
		sql.params().set("code", "T2").set("weight", 13);
		dao.execute(sql);

		sql = dao.sqls().create("tank.insert");
		sql.vars().set("id", platoonId);
		sql.params().set("code", "T3").set("weight", 14);
		dao.execute(sql);

		sql = dao.sqls().create("tank.insert");
		sql.vars().set("id", platoonId);
		sql.params().set("code", "T4").set("weight", 15);
		dao.execute(sql);

		TableName.run(platoonId, new Atom() {
			public void run() {
				assertEquals(4, dao.count(Tank.class));
			}
		});
	}

	@Test
	public void test_dynamic_query() {
		pojos.init();
		Platoon p = pojos.create4Platoon(Base.make("xyz"), "GG");
		Sql sql = dao.sqls().create("tank.query").setEntity(dao.getEntity(Tank.class));
		sql.vars().set("id", p.getId());
		sql.setCallback(SQLs.callback.queryEntity());
		dao.execute(sql);
		assertEquals(2, sql.getList(Tank.class).size());
	}
}