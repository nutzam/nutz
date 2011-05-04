package org.nutz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.nutz.dao.impl.NutDao;
import org.nutz.json.Json;
import org.nutz.lang.Stopwatch;

public class DaoPerformanceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable{
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setUsername("sa");
		ds.setPassword("sa");
		ds.setUrl("jdbc:h2:mem:~");
		ds.setDefaultAutoCommit(false);
		Json.toJson(ds);
		
		NutDao dao = new NutDao(ds);
		
		dao.create(Pojo.class, true);
		List<Pojo> list = new ArrayList<Pojo>();
		for (int i = 0; i < 50000; i++) {
			Pojo pojo = new Pojo();
			pojo.setName("abc"+i);
			list.add(pojo);
		}
		dao.fastInsert(list);
		dao.create(Pojo.class, true);
		System.out.println("预热完成,开始测试");
		Stopwatch sw = Stopwatch.begin();
		dao.fastInsert(list);
		sw.stop();
		System.out.println("Dao 批量插入5w条,耗时"+sw.getDuration()+"ms");

		sw.start();
		{
			Connection conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement("insert into tb_pojo(name) values(?)");
			for (Pojo pojo : list) {
				ps.setString(1, pojo.getName());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.close();
		}
		sw.stop();

		System.out.println("JDBC 批量插入5w条,耗时"+sw.getDuration()+"ms");
//		System.out.println("Time = " + sw.getDuration());
		
//		System.out.println(dao.count(Pojo.class));
	}

}
