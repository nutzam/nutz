package org.nutz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.nutz.dao.impl.NutDao;
import org.nutz.json.Json;
import org.nutz.lang.Stopwatch;

public class DaoPerformanceTest {
	
	static int num = 100000;

	/**
	 * 务必先把log关闭!! 设置为Error或者NONE
	 */
	@Test
	public void test_dao() throws Throwable{
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setUsername("sa");
		
		ds.setPassword("sa");
		ds.setUrl("jdbc:h2:mem:~");
		ds.setDefaultAutoCommit(false);
		Json.toJson(ds);
		
		NutDao dao = new NutDao(ds);
		

		List<Pojo> list = new ArrayList<Pojo>();
		for (int i = 0; i < num; i++) {
			Pojo pojo = new Pojo();
			pojo.setName("abc"+i);
			list.add(pojo);
		}
		
		dao.create(Pojo.class, true);
		dao.fastInsert(list);
		System.out.println("预热完成,开始测试");
		
		//--------------------------------------------------
		dao.create(Pojo.class, true);
		dao(dao,list);
		
		dao.create(Pojo.class, true);
		jdbc(ds, list);
		
		dao.create(Pojo.class, true);
		dao(dao,list);
	}

	public static void dao(Dao dao, List<Pojo> list){
		Stopwatch sw = Stopwatch.begin();
		dao.fastInsert(list);
		sw.stop();
		System.out.printf("Dao 批量插入%d条,耗时%dms\n",num,sw.getDuration());
	}
	
	public static void jdbc(DataSource ds, List<Pojo> list) throws Throwable{
		
		Stopwatch sw = Stopwatch.begin();
		Connection conn = ds.getConnection();
		PreparedStatement ps = conn.prepareStatement("insert into tb_pojo(name) values(?)");
		for (Pojo pojo : list) {
			ps.setString(1, pojo.getName());
			ps.addBatch();
		}
		ps.executeBatch();
		conn.commit();
		conn.close();
		
		sw.stop();
		System.out.printf("JDBC 批量插入%d条,耗时%dms\n",num,sw.getDuration());
	}
}
