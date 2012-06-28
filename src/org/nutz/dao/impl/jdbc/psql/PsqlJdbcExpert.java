package org.nutz.dao.impl.jdbc.psql;

import java.util.List;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.Lang;

public class PsqlJdbcExpert extends AbstractJdbcExpert {

    public PsqlJdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    public String getDatabaseType() {
        return DB.PSQL.name();
    }

    public void formatQuery(Pojo pojo) {
        Pager pager = pojo.getContext().getPager();
        // 需要进行分页
        if (null != pager && pager.getPageNumber() > 0)
            pojo.append(Pojos.Items.wrapf(    " LIMIT %d OFFSET %d",
                                            pager.getPageSize(),
                                            pager.getOffset()));
    }
    
    public void formatQuery(Sql sql) {
        Pager pager = sql.getContext().getPager();
        if (null != pager && pager.getPageNumber() > 0) {
            sql.setSourceSql(sql.getSourceSql() + String.format(" LIMIT %d OFFSET %d",
                                            pager.getPageSize(),
                                            pager.getOffset()));
        }
    }

    public boolean createEntity(Dao dao, Entity<?> en) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + en.getTableName() + "(");
        // 创建字段
        for (MappingField mf : en.getMappingFields()) {
            sb.append('\n').append(mf.getColumnName());
            // 自增主键特殊形式关键字
            if (mf.isId() && mf.isAutoIncreasement()) {
                sb.append(" SERIAL");
            } else {
                sb.append(' ').append(evalFieldType(mf));
                // 非主键的 @Name，应该加入唯一性约束
                if (mf.isName() && en.getPkType() != PkType.NAME) {
                    sb.append(" UNIQUE NOT NULL");
                }
                // 普通字段
                else {
                    if (mf.isUnsigned())
                        sb.append(" UNSIGNED");
                    if (mf.isNotNull())
                        sb.append(" NOT NULL");
                    if (mf.isAutoIncreasement())
                        throw Lang.noImplement();
                    if (mf.hasDefaultValue())
                        sb.append(" DEFAULT '").append(getDefaultValue(mf)).append('\'');
                }
            }
            sb.append(',');
        }
        // 创建主键
        List<MappingField> pks = en.getPks();
        if (!pks.isEmpty()) {
            sb.append('\n');
            sb.append(String.format("CONSTRAINT %s_pkey PRIMARY KEY (", en.getTableName()));
            for (MappingField pk : pks) {
                sb.append(pk.getColumnName()).append(',');
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

        // 添加注释(表注释与字段注释)
        addComment(dao, en);

        return true;
    }

    @Override
    protected String evalFieldType(MappingField mf) {
        if (mf.getCustomDbType() != null)
            return mf.getCustomDbType();
        switch (mf.getColumnType()) {
        case INT:
            // 用户自定义了宽度
            if (mf.getWidth() > 0)
                return "NUMERIC(" + mf.getWidth() + ")";
            // 用数据库的默认宽度
            return "INT";

        case FLOAT:
            // 用户自定义了精度
            if (mf.getWidth() > 0 && mf.getPrecision() > 0) {
                return "NUMERIC(" + mf.getWidth() + "," + mf.getPrecision() + ")";
            }
            // 用默认精度
            if (mf.getTypeMirror().isDouble())
                return "NUMERIC(15,10)";
            return "NUMERIC";

        case BINARY:
            return "BYTEA";
            
        case DATETIME:
            return "TIMESTAMP";
        default :
            break;
        }
        return super.evalFieldType(mf);
    }

    protected String createResultSetMetaSql(Entity<?> en) {
        return "SELECT * FROM " + en.getViewName() + " LIMIT 1";
    }

}
