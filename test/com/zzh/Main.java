package com.zzh;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl("jdbc:mysql://localhost:3306/zzhtest");
			dataSource.setUsername("root");
			dataSource.setPassword("123456");
			Connection conn = dataSource.getConnection();
			System.out.println("OK");
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
