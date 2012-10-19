package org.nutz.dao.impl.jdbc.mysql;

import java.util.List;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.entity.macro.SqlFieldMacro;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Pojos;

public class MysqlJdbcExpert extends AbstractJdbcExpert {

    private static final String META_ENGINE = "mysql-engine";

    private static final String META_CHARSET = "mysql-charset";

    public MysqlJdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    public String getDatabaseType() {
        return DB.MYSQL.name();
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
            sql.setSourceSql(sql.getSourceSql() + String.format(" LIMIT %d, %d", pager.getOffset(), pager.getPageSize()));
    }

    @Override
    protected String evalFieldType(MappingField mf) {
        if (mf.getCustomDbType() != null)
            return mf.getCustomDbType();
        // Mysql 的精度是按照 bit
        if (mf.getColumnType() == ColType.INT) {
            int width = mf.getWidth();
            if (width <= 0)
                return "INT(32)";
            else if (width <= 4) {
                return "TINYINT(" + (width * 4) + ")";
            } else if (width <= 8) {
                return "INT(" + (width * 4) + ")";
            }
            return "BIGINT(" + (width * 4) + ")";
        }
        if (mf.getColumnType() == ColType.BINARY) {
            return "MediumBlob"; //默认用16M的应该可以了吧?
        }
        // 其它的参照默认字段规则 ...
        return super.evalFieldType(mf);
    }

    public boolean createEntity(Dao dao, Entity<?> en) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + en.getTableName() + "(");
        // 创建字段
        for (MappingField mf : en.getMappingFields()) {
            sb.append('\n').append(mf.getColumnName());
            sb.append(' ').append(evalFieldType(mf));
            // 非主键的 @Name，应该加入唯一性约束
            if (mf.isName() && en.getPkType() != PkType.NAME) {
                sb.append(" UNIQUE NOT NULL");
            }
            // 普通字段
            else {
                // 下面的关于Timestamp处理，是因为MySql中第一出现Timestamp的话，如果没有设定default，数据库默认会设置为CURRENT_TIMESTAMP
                if (mf.isUnsigned())
                    sb.append(" UNSIGNED");

                if (mf.isNotNull()) {
                    sb.append(" NOT NULL");
                } else if (mf.getColumnType() == ColType.TIMESTAMP) {
                    sb.append(" NULL");
                }

                if (mf.isAutoIncreasement())
                    sb.append(" AUTO_INCREMENT");

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
                        sb.append(" DEFAULT '").append(getDefaultValue(mf)).append("'");
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
                sb.append(pk.getColumnName()).append(',');
            }
            sb.setCharAt(sb.length() - 1, ')');
            sb.append("\n ");
        }

        // 结束表字段设置
        sb.setCharAt(sb.length() - 1, ')');
        // 设置特殊引擎
        if (en.hasMeta(META_ENGINE)) {
            sb.append(" ENGINE=" + en.getMeta(META_ENGINE));
        }
        // 默认采用 UTF-8 编码
        if (en.hasMeta(META_CHARSET)) {
            sb.append(" CHARSET=" + en.getMeta(META_CHARSET));
        } else {
            sb.append(" CHARSET=utf8");
        }
        // 表名注释
        if (en.hasTableComment()) {
            sb.append(" COMMENT='").append(en.getTableComment()).append("'");
        }

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
    
    public Pojo fetchPojoId(Entity<?> en, MappingField idField) {
        String autoSql = "SELECT @@@@IDENTITY";
        Pojo autoInfo = new SqlFieldMacro(idField, autoSql);
        autoInfo.setEntity(en);
        return autoInfo;
    }
}
