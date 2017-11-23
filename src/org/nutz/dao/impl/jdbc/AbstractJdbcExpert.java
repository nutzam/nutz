package org.nutz.dao.impl.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.Dao;
import org.nutz.dao.DaoException;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityIndex;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.impl.entity.macro.SqlFieldMacro;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlContext;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 对于所有数据库的抽象实现
 *
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class AbstractJdbcExpert implements JdbcExpert {

    private static final Log log = Logs.get();

    private static String DEFAULT_COMMENT_TABLE = "comment on table $table is '$tableComment'";

    private static String DEFAULT_COMMENT_COLUMN = "comment on column $table.$column is '$columnComment'";

    protected Set<String> keywords;

    /**
     * 提供给子类使用的配置文件对象
     */
    protected JdbcExpertConfigFile conf;

    // ====================================================================
    // 构造函数：子类需要将重载
    public AbstractJdbcExpert(JdbcExpertConfigFile conf) {
        this.conf = conf;
    }

    // ====================================================================
    // 下面为子类默认实现几个接口函数

    public void setupEntityField(Connection conn, Entity<?> en) {
        List<MappingField> mfs = new ArrayList<MappingField>();
        for (MappingField mf : en.getMappingFields()) {
            if (mf.getTypeMirror().isEnum()) {
                mfs.add(mf);
            }
        }
        if (mfs.isEmpty())
            return;
        Statement stat = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        try {
            // 获取数据库元信息
            stat = conn.createStatement();
            rs = stat.executeQuery(createResultSetMetaSql(en));
            rsmd = rs.getMetaData();
            // 循环字段检查
            List<String> columnNames = new ArrayList<String>();
            List<String> columnLabels = new ArrayList<String>();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(rsmd.getColumnName(i));
                columnLabels.add(rsmd.getColumnLabel(i));
            }
            for (MappingField mf : mfs) {
                try {
                    int ci = columnNames.indexOf(mf.getColumnName()) + 1;
                    if (ci == 0) {
                        log.debugf("Can not find @Column(%s) in table/view (%s), skip checking", mf.getColumnName(), rsmd.getTableName(1));
                        continue;
                    }
                    // 枚举类型在数据库中的值
                    if (Daos.isIntLikeColumn(rsmd, ci)) {
                        mf.setColumnType(ColType.INT);
                    } else {
                        mf.setColumnType(ColType.VARCHAR);
                    }
                }
                catch (Exception e) {
                }
            }
        }
        catch (Exception e) {
            if (log.isDebugEnabled())
                log.debugf("Table '%s' doesn't exist! class=%s", en.getViewName(), en.getType().getName());
        }
        // Close ResultSet and Statement
        finally {
            Daos.safeClose(stat, rs);
        }
    }

    public ValueAdaptor getAdaptor(MappingField ef) {
        Mirror<?> mirror = ef.getTypeMirror();
        // 为数字型枚举的特殊判断
        if (mirror.isEnum() && ColType.INT == ef.getColumnType())
            return Jdbcs.Adaptor.asEnumInt;
        // 用普通逻辑返回适配器
        return Jdbcs.getAdaptor(mirror);
    }

    public Pojo createPojo(SqlType type) {
        return new NutPojo().setSqlType(type);
    }

    public boolean dropEntity(Dao dao, Entity<?> en) {
        String tableName = en.getTableName();
        String viewName = en.getViewName();

        try {
            dropRelation(dao, en);
            if (!tableName.equals(viewName) && dao.exists(viewName)) {
                dao.execute(Sqls.create("DROP VIEW " + viewName));
            }
            dao.execute(Sqls.create("DROP TABLE " + tableName));
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public Map<String, Object> getConf() {
        return this.conf.getConfig();
    }

    // ====================================================================
    // 下面是提供给子类使用的一些帮助函数

    protected String createResultSetMetaSql(Entity<?> en) {
        return "SELECT * FROM " + en.getViewName() + " where 1!=1";
    }

    public void createRelation(Dao dao, Entity<?> en) {
        final List<Sql> sqls = new ArrayList<Sql>(5);
        for (LinkField lf : en.visitManyMany(null, null, null)) {
            Sql sql = createRelation(dao, lf);
            if (sql != null)
                sqls.add(sql);
        }
        dao.execute(sqls.toArray(new Sql[sqls.size()]));
    }

    protected Sql createRelation(Dao dao, LinkField lf) {
        ManyManyLinkField mm = (ManyManyLinkField) lf;
        if (dao.exists(mm.getRelationName()))
            return null;
        String sql = "CREATE TABLE " + mm.getRelationName() + "(" + "\n";
        sql += mm.getFromColumnName() + " " + evalFieldType(mm.getHostField()) + "," + "\n";
        sql += mm.getToColumnName() + " " + evalFieldType(mm.getLinkedField()) + "\n";
        sql += ")";
        return Sqls.create(sql);
    }

    public void dropRelation(Dao dao, Entity<?> en) {
        final List<Sql> sqls = new ArrayList<Sql>(5);
        for (LinkField lf : en.visitManyMany(null, null, null)) {
            ManyManyLinkField mm = (ManyManyLinkField) lf;
            if (!dao.exists(mm.getRelationName()))
                continue;
            sqls.add(Sqls.create("DROP TABLE " + mm.getRelationName()));
        }
        dao.execute(sqls.toArray(new Sql[sqls.size()]));
    }

    public String evalFieldType(MappingField mf) {
        if (mf.getCustomDbType() != null)
            return mf.getCustomDbType();
        switch (mf.getColumnType()) {
        case CHAR:
            return "CHAR(" + mf.getWidth() + ")";

        case BOOLEAN:
            return "BOOLEAN";

        case VARCHAR:
            return "VARCHAR(" + mf.getWidth() + ")";

        case TEXT:
            return "TEXT";

        case BINARY:
            return "BLOB";

        case TIMESTAMP:
            return "TIMESTAMP";

        case DATETIME:
            return "DATETIME";

        case DATE:
            return "DATE";
        case TIME:
            return "TIME";

        case INT:
            // 用户自定义了宽度
            if (mf.getWidth() > 0)
                return "INT(" + mf.getWidth() + ")";
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
            return "FLOAT";

        case PSQL_ARRAY:
            return "ARRAY";

        case PSQL_JSON:
        case MYSQL_JSON:
            return "JSON";
        default:
            throw Lang.makeThrow("Unsupport colType '%s' of field '%s' in '%s' ",
                             mf.getColumnType(),
                             mf.getName(),
                             mf.getEntity().getType().getName());
        }
    }

    protected static List<DaoStatement> wrap(String... sqls) {
        List<DaoStatement> sts = new ArrayList<DaoStatement>(sqls.length);
        for (String sql : sqls)
            if (!Strings.isBlank(sql))
                sts.add(Sqls.create(sql));
        return sts;
    }

    protected static List<DaoStatement> wrap(List<String> sqls) {
        List<DaoStatement> sts = new ArrayList<DaoStatement>(sqls.size());
        for (String sql : sqls)
            if (!Strings.isBlank(sql))
                sts.add(Sqls.create(sql));
        return sts;
    }

    protected static String gSQL(String ptn, String table, String field) {
        CharSegment cs = new CharSegment(ptn);
        cs.set("T", table).set("F", field);
        return cs.toString();
    }

    protected String getDefaultValue(MappingField mf) {
        return mf.getDefaultValue(null).replaceAll("@", "@@");
    }

    protected List<Sql> createIndexs(Entity<?> en) {
        List<Sql> sqls = new ArrayList<Sql>();
        for (EntityIndex index : en.getIndexes()) {
            sqls.add(createIndexSql(en, index));
        }
        return sqls;
    }

    public Sql createIndexSql(Entity<?> en, EntityIndex index) {
        StringBuilder sb = new StringBuilder();
        if (index.isUnique())
            sb.append("Create UNIQUE Index ");
        else
            sb.append("Create Index ");
        sb.append(index.getName(en));
        sb.append(" ON ").append(en.getTableName()).append("(");
        for (EntityField field : index.getFields()) {
            if (field instanceof MappingField) {
                MappingField mf = (MappingField) field;
                sb.append(mf.getColumnNameInSql()).append(',');
            } else {
                throw Lang.makeThrow(DaoException.class,
                                     "%s %s is NOT a mapping field, can't use as index field!!",
                                     en.getClass(),
                                     field.getName());
            }
        }
        sb.setCharAt(sb.length() - 1, ')');
        return Sqls.create(sb.toString());
    }

    public void addComment(Dao dao, Entity<?> en) {
        addComment(dao, en, null, null);
    }

    public void addComment(Dao dao, Entity<?> en, String commentTable, String commentColumn) {
        if (!en.hasTableComment() && !en.hasColumnComment()) {
            return;
        }
        List<Sql> sqls = new ArrayList<Sql>();
        // 表注释
        if (en.hasTableComment()) {
            Sql tableCommentSQL = Sqls.create(Strings.isBlank(commentTable) ? DEFAULT_COMMENT_TABLE
                                                                            : commentTable);
            tableCommentSQL.vars().set("table", en.getTableName()).set("tableComment",
                                                                       en.getTableComment());
            sqls.add(tableCommentSQL);
        }
        // 字段注释
        if (en.hasColumnComment()) {
            for (MappingField mf : en.getMappingFields()) {
                if (mf.hasColumnComment() && !mf.isReadonly()) {
                    Sql columnCommentSQL = Sqls.create(Strings.isBlank(commentColumn) ? DEFAULT_COMMENT_COLUMN
                                                                                      : commentColumn);
                    columnCommentSQL.vars()
                                    .set("table", en.getTableName())
                                    .set("column", mf.getColumnName())
                                    .set("columnComment", mf.getColumnComment());
                    sqls.add(columnCommentSQL);
                }
            }
        }
        // 执行创建语句
        dao.execute(sqls.toArray(new Sql[sqls.size()]));
    }

    public void formatQuery(DaoStatement daoStatement) {
        if (daoStatement == null)
            return;
        SqlContext ctx = daoStatement.getContext();
        if (ctx == null || ctx.getPager() == null)
            return;
        if (daoStatement instanceof Pojo)
            formatQuery((Pojo) daoStatement);
        else if (daoStatement instanceof Sql)
            formatQuery((Sql) daoStatement);
        else
            throw Lang.noImplement();
    }

    public abstract void formatQuery(Pojo pojo);

    public void formatQuery(Sql sql) {
        throw Lang.noImplement();
    }

    public Pojo fetchPojoId(Entity<?> en, MappingField idField) {
        String autoSql = "SELECT MAX($field) AS $field FROM $view";
        Pojo autoInfo = new SqlFieldMacro(idField, autoSql);
        autoInfo.setEntity(en);
        return autoInfo;
    }

    public boolean isSupportAutoIncrement() {
        return true;
    }

    public String makePksName(Entity<?> en) {
        String name = en.getType().getAnnotation(PK.class).name();
        if (Strings.isBlank(name)) {
            StringBuilder sb = new StringBuilder();
            for (MappingField mf : en.getPks()) {
                sb.append("_").append(mf.getColumnName());
            }
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
        return name;
    }

    public void addDefaultValue(StringBuilder sb, MappingField mf) {
        if (!mf.hasDefaultValue())
            return;
        String dft = getDefaultValue(mf);
        if (mf.getColumnType() == ColType.VARCHAR
                || mf.getTypeMirror().isStringLike())
            sb.append(" DEFAULT '").append(dft).append('\'');
        else
            sb.append(" DEFAULT ").append(dft);
    }

    public boolean addColumnNeedColumn() {
        return true;
    }

    public boolean supportTimestampDefault() {
        return true;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public String wrapKeywork(String columnName, boolean force) {
        if (force || keywords.contains(columnName.toUpperCase()))
            return "`" + columnName + "`";
        return null;
    }

    public boolean isSupportGeneratedKeys() {
        return true;
    }

    public void checkDataSource(Connection conn) throws SQLException {}

    @Override
    public Sql createAddColumnSql(Entity<?> en, MappingField mf) {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(en.getTableName()).append(" ADD ");
        if (addColumnNeedColumn())
            sb.append("COLUMN ");
        sb.append(mf.getColumnNameInSql()).append(" ").append(evalFieldType(mf));
        if (mf.isUnsigned()) {
            sb.append(" UNSIGNED");
        }
        if (mf.isNotNull()) {
            sb.append(" NOT NULL");
        }
        if (mf.getColumnType() == ColType.TIMESTAMP && supportTimestampDefault()) {
            if (mf.hasDefaultValue()) {
                sb.append(" ").append(mf.getDefaultValue(null).replaceAll("@", "@@"));
            } else {
                if (mf.isNotNull()) {
                    sb.append(" DEFAULT 0");
                } else {
                    sb.append(" NULL DEFAULT NULL");
                }
            }
        } else {
            if (mf.hasDefaultValue())
                addDefaultValue(sb, mf);
        }
        if (mf.hasColumnComment() && canCommentWhenAddIndex()) {
            sb.append(" COMMENT '").append(mf.getColumnComment()).append("'");
        }
        // sb.append(';');
        return Sqls.create(sb.toString());
    }

    public boolean canCommentWhenAddIndex() {
        return false;
    }

    @Override
    public List<String> getIndexNames(Entity<?> en, Connection conn) throws SQLException {
        List<String> names = new ArrayList<String>();
        String showIndexs = "show index from " + en.getTableName();
        PreparedStatement ppstat = conn.prepareStatement(showIndexs);
        ResultSet rest = ppstat.executeQuery();
        while (rest.next()) {
            String index = rest.getString(3);
            names.add(index);
        }
        return names;
    }
}
