package org.nutz.dao.sql;

import org.nutz.dao.Condition;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.cri.SqlExpressionGroup;

/**
 * 这个接口是对 Condition 接口进行扩充，主要为了能够更好的利用 PreparedStatement
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Criteria extends Condition, PItem {

    SqlExpressionGroup where();

    OrderBy getOrderBy();

    Pager getPager();
}
