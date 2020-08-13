package org.nutz.dao.impl.jdbc.psql;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.impl.jdbc.BlobValueAdaptor3;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class PsqlJdbcExpert extends AbstractJdbcExpert {

    private static final Log log = Logs.get();

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
            pojo.append(Pojos.Items.wrapf(" LIMIT %d OFFSET %d",
                                          pager.getPageSize(),
                                          pager.getOffset()));
    }

    public void formatQuery(Sql sql) {
        Pager pager = sql.getContext().getPager();
        if (null != pager && pager.getPageNumber() > 0) {
            sql.setSourceSql(sql.getSourceSql()
                             + String.format(" LIMIT %d OFFSET %d",
                                             pager.getPageSize(),
                                             pager.getOffset()));
        }
    }

    public boolean createEntity(Dao dao, Entity<?> en) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + en.getTableName() + "(");
        // 创建字段
        for (MappingField mf : en.getMappingFields()) {
            if (mf.isReadonly())
                continue;
            sb.append('\n').append(mf.getColumnNameInSql());
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
                        addDefaultValue(sb, mf);
                }
            }
            sb.append(',');
        }
        // 创建主键
        List<MappingField> pks = en.getPks();
        if (!pks.isEmpty()) {
            sb.append('\n');
            sb.append(String.format("CONSTRAINT %s_pkey PRIMARY KEY (",
                                    en.getTableName().replace('.', '_').replace('"', '_')));
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

        // 添加注释(表注释与字段注释)
        addComment(dao, en);

        return true;
    }

    public String evalFieldType(MappingField mf) {
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
            if (mf.getMirror().isDouble())
                return "NUMERIC(15,10)";
            return "NUMERIC";

        case BINARY:
            return "BYTEA";

        case DATETIME:
            return "TIMESTAMP";

        case PSQL_JSON:
            return "JSON";

        case PSQL_ARRAY:
            return "ARRAY";

        default:
            break;
        }
        return super.evalFieldType(mf);
    }

    protected String createResultSetMetaSql(Entity<?> en) {
        return "SELECT * FROM " + en.getViewName() + " LIMIT 1";
    }

    @Override
    public ValueAdaptor getAdaptor(MappingField ef) {
        if (ef.getMirror().isOf(Blob.class)) {
            return new BlobValueAdaptor3(Jdbcs.getFilePool());
        } else if (ColType.PSQL_JSON == ef.getColumnType()) {
            return new PsqlJsonAdaptor();
        } else if (ColType.PSQL_ARRAY == ef.getColumnType()) {
            return new PsqlArrayAdaptor(ef.getCustomDbType());
        } else {
            return super.getAdaptor(ef);
        }
    }

    public String wrapKeyword(String columnName, boolean force) {
        if (force || keywords.contains(columnName.toUpperCase()))
            return "\"" + columnName + "\"";
        return null;
    }

    @Override
    public void checkDataSource(Connection conn) throws SQLException {
        if (log.isDebugEnabled()) {
            String sql = "SELECT * FROM information_schema.character_sets";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                for (String name : Arrays.asList("character_set_catalog",
                                                 "character_set_schema",
                                                 "character_set_name",
                                                 "character_repertoire",
                                                 "form_of_use",
                                                 "default_collate_catalog",
                                                 "default_collate_schema",
                                                 "default_collate_name")) {
                    log.debugf("Postgresql : %s=%s", name, rs.getString(name));
                }
            }
            rs.close();
            // 打印当前数据库名称
            rs = stmt.executeQuery("SELECT CURRENT_DATABASE()");
            if (rs.next()) {
                log.debug("Postgresql : database=" + rs.getString(1));
            }
            rs.close();
            // 打印当前连接用户名
            rs = stmt.executeQuery("SELECT CURRENT_USER");
            if (rs.next()) {
                log.debug("Postgresql : user=" + rs.getString(1));
            }
            rs.close();
            stmt.close();
        }
    }

    /**
     * @author enzozhong( haowen.zhong@foxmail.com )
     */
    @Override
    public List<String> getIndexNames(Entity<?> en, Connection conn) throws SQLException {

        String tableName = en.getTableName();
        String sql = "SELECT * FROM pg_indexes WHERE tablename='" + tableName + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<String> indexNames = new ArrayList<String>(17);
        while (rs.next()) {
            indexNames.add(rs.getString("indexname"));
        }

        return indexNames;
    }
}
