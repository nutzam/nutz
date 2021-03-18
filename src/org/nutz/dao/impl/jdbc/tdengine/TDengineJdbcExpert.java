package org.nutz.dao.impl.jdbc.tdengine;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Pojos;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class TDengineJdbcExpert extends AbstractJdbcExpert {
    private static final Log log = Logs.get();

    public TDengineJdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    public String getDatabaseType() {
        return DB.TDENGINE.name();
    }

    public ValueAdaptor getAdaptor(MappingField ef) {
        return super.getAdaptor(ef);
    }

    public String evalFieldType(MappingField mf) {
        if (mf.getCustomDbType() != null)
            return mf.getCustomDbType();
        int intLen = 4;
        int width = mf.getWidth();
        switch (mf.getColumnType()) {
            case INT:
                if (width <= 0) {
                    return "INT(32)";
                } else if (width <= 2) {
                    return "TINYINT(" + (width * intLen) + ")";
                } else if (width <= 4) {
                    return "SMALLINT(" + (width * intLen) + ")";
                } else if (width <= 8) {
                    return "INT(" + (width * intLen) + ")";
                }
                return "BIGINT(" + (width * intLen) + ")";
            case FLOAT:
                return "FLOAT";
            case DOUBLE:
                return "DOUBLE";
            case BOOLEAN:
                return "BOOL";
            case BINARY:
                return "BINARY(" + width + ")";
            case CHAR:
            case VARCHAR:
                return "NCHAR(" + width + ")";
            default:
                break;
        }
        // 其它的参照默认字段规则 ...
        return super.evalFieldType(mf);
    }

    public boolean createEntity(Dao dao, Entity<?> en) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + en.getTableName() + "(");
        // 创建字段
        for (MappingField mf : en.getMappingFields()) {
            if (mf.isReadonly())
                continue;
            sb.append('\n').append(mf.getColumnNameInSql());
            sb.append(' ').append(evalFieldType(mf));
            sb.append(',');
        }
        // 结束表字段设置
        sb.setCharAt(sb.length() - 1, ')');
        // 执行创建语句
        dao.execute(Sqls.create(sb.toString()));
        return true;
    }


    public void formatQuery(Pojo pojo) {
        Pager pager = pojo.getContext().getPager();
        // 需要进行分页
        if (null != pager && pager.getPageNumber() > 0)
            pojo.append(Pojos.Items.wrapf(" LIMIT %d, %d", pager.getOffset(), pager.getPageSize()));
    }

    public void formatQuery(Sql sql) {
        Pager pager = sql.getContext().getPager();
        // 需要进行分页
        if (null != pager && pager.getPageNumber() > 0)
            sql.setSourceSql(sql.getSourceSql()
                    + String.format(" LIMIT %d, %d",
                    pager.getOffset(),
                    pager.getPageSize()));
    }
}
