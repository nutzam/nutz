package org.nutz.dao.pager;

/**
 * SQL Server 2000, 不能支持一条 SQL 语句进行带查询条件的分页
 * <p>
 * 因此，只能利用 JDBC 的游标移动功能进行分页。或者利用存储过程
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SqlServer2000Pager extends OtherPager {}
