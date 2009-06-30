package org.nutz.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.nutz.dao.mysql.MysqlTest;
import org.nutz.dao.psql.PostgresqlTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { MysqlTest.class, PostgresqlTest.class })
public class TestAllDBCases {

}
