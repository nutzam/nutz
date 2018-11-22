package org.nutz.dao.impl.jdbc.sqlserver2012;

import org.nutz.dao.impl.jdbc.sqlserver2005.Sqlserver2005JdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Pojos;

public class Sqlserver2012JdbcExpert extends Sqlserver2005JdbcExpert {

    public Sqlserver2012JdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    public void formatQuery(Pojo pojo) {
        Pager pager = pojo.getContext().getPager();
        // 需要进行分页
        if (null != pager && pager.getPageNumber() > 0)
            pojo.append(Pojos.Items.wrapf(" OFFSET %d ROWS FETCH NEXT %d ROW ONLY", pager.getOffset(), pager.getPageSize()));
    }
    
    public void formatQuery(Sql sql) {
        Pager pager = sql.getContext().getPager();
        // 需要进行分页
        if (null != pager && pager.getPageNumber() > 0)
            sql.setSourceSql(sql.getSourceSql() + String.format(" OFFSET %d ROWS FETCH NEXT %d ROW ONLY", pager.getOffset(), pager.getPageSize()));
    }
}
