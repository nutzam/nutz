package org.nutz.dao.test.exec;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;

/**
 * 执行简单的存储过程,无输出参数
 * 
 * @author wendal
 * 
 */
public class SimpleDaoExecTest extends DaoCase {

	// H2 支持简单的CALL语句
	@Test
	public void test_simple_h2_call() {
		if (!dao.meta().isH2())
			return; // Only test for h2 now
		dao.create(Pet.class, true);
		dao.insert(Pet.create("wendal"));
		dao.execute(Sqls.create("CALL SELECT MAX(ID) FROM t_PET"));

		Sql sql = Sqls.fetchInt("CALL SELECT MAX(ID) FROM t_PET");
		dao.execute(sql);
		assertEquals(1, sql.getInt());

		sql = Sqls.fetchInt("CALL 15*25");
		dao.execute(sql);
		assertEquals(15 * 25, sql.getInt());
	}

	// Mysql支持存储过程
	@Test
	public void test_simple_mysql_exec() {
		if (!dao.meta().isMySql())
			return; // Only test for mysql now
		dao.create(Pet.class, true);
		dao.insert(Pet.create("wendal"));
		dao.execute(Sqls.create("DROP PROCEDURE IF EXISTS proc_pet_getCount"));

		dao.execute(Sqls.create("CREATE PROCEDURE proc_pet_getCount()\nBEGIN\n\tSELECT name FROM t_pet;\nEND"));
		Sql sql = Sqls.fetchString("CALL proc_pet_getCount()"); // 单一结果集,且没有输入输出参数
		dao.execute(sql);
		assertEquals("wendal", sql.getString());
	}

	// Mysql支持存储过程
	@Test
	public void test_simple_mysql_exec2() {
		if (!dao.meta().isMySql())
			return; // Only test for mysql now
		dao.create(Pet.class, true);
		dao.insert(Pet.create("wendal"));
		dao.execute(Sqls.create("DROP PROCEDURE IF EXISTS proc_pet_fetch"));

		dao.execute(Sqls.create("CREATE PROCEDURE proc_pet_fetch(IN nm varchar(1024))\nBEGIN\n\tSELECT * FROM t_pet where name=nm;\nEND"));
		Sql sql = Sqls.fetchEntity("CALL proc_pet_fetch(@nm)");
		sql.setEntity(dao.getEntity(Pet.class));
		sql.params().set("nm", "wendal");
		dao.execute(sql);
		
		Pet pet = sql.getObject(Pet.class);
		assertNotNull(pet);
		assertEquals("wendal", pet.getName());
	}

}
