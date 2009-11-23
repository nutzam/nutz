package org.nutz.dao.test.sqls;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.dao.Sqls;
import org.nutz.dao.TableName;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Country;
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
		
		pojos.dropPlatoon(platoonId);
	}

	@Test
	public void test_dynamic_query() {
		pojos.init();
		Platoon p = pojos.create4Platoon(Base.make("xyz"), "GG");
		Sql sql = dao.sqls().create("tank.query").setEntity(dao.getEntity(Tank.class));
		sql.vars().set("id", p.getId());
		sql.setCallback(Sqls.callback.queryEntity());
		dao.execute(sql);
		assertEquals(2, sql.getList(Tank.class).size());
		
		pojos.dropPlatoon(p.getId());
	}

	@Test
	public void test_statice_null_field() {
		pojos.init();
		Sql sql = Sqls.create("INSERT INTO dao_country (name,detail) VALUES(@name,@detail)");
		sql.params().set("name", "ABC").set("detail", "haha");
		dao.execute(sql);
		assertEquals(1, dao.count("dao_country"));

		sql = Sqls.create("UPDATE dao_country SET detail=@detail WHERE name=@name");
		sql.params().set("name", "ABC").set("detail", null);
		dao.execute(sql);
		Country c = dao.fetch(Country.class, "ABC");
		assertNull(c.getDetail());
	}
}