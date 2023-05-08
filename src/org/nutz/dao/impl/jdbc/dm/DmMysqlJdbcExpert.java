package org.nutz.dao.impl.jdbc.dm;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Pojos;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.List;

/**
 * @author wizzer.cn
 */
public class DmMysqlJdbcExpert extends AbstractJdbcExpert {

    private static final Log log = Logs.get();

    public DmMysqlJdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    public String getDatabaseType() {
        return DB.DM_MYSQL.name();
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

    public String evalFieldType(MappingField mf) {
        if (mf.getCustomDbType() != null)
            return mf.getCustomDbType();
        if (mf.getColumnType() == ColType.INT) {
            int width = mf.getWidth();
            if (width <= 0) {
                return "INT";
            } else if (width <= 4) {
                return "TINYINT";
            } else if (width <= 6) {
                return "SMALLINT";
            } else if (width <= 10) {
                return "INT";
            }
            return "BIGINT";
        }
        if (mf.getColumnType() == ColType.BOOLEAN) {
            return "TINYINT";
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
            // 非主键的 @Name，应该加入唯一性约束
            if (mf.isName() && en.getPkType() != PkType.NAME) {
                sb.append(" UNIQUE NOT NULL");
            }
            // 普通字段
            else {

                if (mf.isNotNull()) {
                    sb.append(" NOT NULL");
                } else if (mf.getColumnType() == ColType.TIMESTAMP) {
                    sb.append(" NULL");
                }

                if (mf.isAutoIncreasement())
                    sb.append(" IDENTITY(1,1)");

                if (mf.getColumnType() == ColType.TIMESTAMP) {
                    if (mf.hasDefaultValue()) {
                        sb.append(" ").append(getDefaultValue(mf));
                    } else {
                        if (mf.isNotNull()) {
                            sb.append(" DEFAULT 0");
                        } else {
                            sb.append(" DEFAULT NULL");
                        }
                    }
                } else {
                    if (mf.hasDefaultValue())
                        addDefaultValue(sb, mf);
                }
            }

            if (mf.hasColumnComment()) {
                sb.append(" COMMENT '").append(mf.getColumnComment()).append("'");
            }

            sb.append(',');
        }
        // 创建主键
        List<MappingField> pks = en.getPks();
        if (!pks.isEmpty()) {
            sb.append('\n');
            sb.append("PRIMARY KEY (");
            for (MappingField pk : pks) {
                sb.append(pk.getColumnNameInSql()).append(',');
            }
            sb.setCharAt(sb.length() - 1, ')');
            sb.append("\n ");
        }

        // 结束表字段设置
        sb.setCharAt(sb.length() - 1, ')');
        // 执行创建语句
        dao.execute(Sqls.create(sb.toString()));

        // 创建索引
        dao.execute(createIndexs(en).toArray(new Sql[0]));

        // 创建关联表
        createRelation(dao, en);

        return true;
    }

    protected String createResultSetMetaSql(Entity<?> en) {
        return "SELECT * FROM " + en.getViewName() + " LIMIT 1";
    }

    public boolean canCommentWhenAddIndex() {
        return true;
    }

    protected Sql createRelation(Dao dao, LinkField lf) {
        Sql sql = super.createRelation(dao, lf);
        if (sql == null)
            return null;
        StringBuilder sb = new StringBuilder(sql.getSourceSql());
        return Sqls.create(sb.toString());
    }
}
