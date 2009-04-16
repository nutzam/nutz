package com.zzh.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.zzh.dao.mysql.MysqlTest;
import com.zzh.dao.psql.PostgresqlTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { MysqlTest.class, PostgresqlTest.class })
public class TestAllDBCases {

}
