package org.nutz.dao.impl.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.nutz.dao.DaoException;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.LinkVisitor;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.sql.pojo.NoParamsPItem;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.sql.PItem;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.PojoMaker;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

/**
 * Nut Pojo制造商。
 */
public class NutPojoMaker implements PojoMaker {

    private JdbcExpert expert;

    public NutPojoMaker(JdbcExpert expert) {
        this.expert = expert;
    }

    @Override
    public Pojo makePojo(SqlType type) {
        return expert.createPojo(type);
    }

    @Override
    public Pojo makeInsert(final Entity<?> en) {
        Pojo pojo = Pojos.pojo(expert, en, SqlType.INSERT);
        pojo.setEntity(en);
        pojo.append(Pojos.Items.entityTableName());
        pojo.append(Pojos.Items.insertFields());
        pojo.append(Pojos.Items.insertValues());
        if (expert.isSupportAutoIncrement()) {
            MappingField mf = en.getIdField();
            if (mf != null && mf.isAutoIncreasement()) {
                if (expert.isSupportGeneratedKeys()) {
                    pojo.setAfter(new GeneratedKeys());
                    pojo.getContext().attr("RETURN_GENERATED_KEYS", true);
                }
            }
        }
        return pojo;
    }

    @Override
    public Pojo makeUpdate(Entity<?> en, Object refer) {
        Pojo pojo = Pojos.pojo(expert, en, SqlType.UPDATE);
        pojo.setEntity(en);
        pojo.append(Pojos.Items.entityTableName());
        pojo.append(Pojos.Items.updateFields(refer));
        return pojo;
    }

    @Override
    public Pojo makeQuery(Entity<?> en) {
        Pojo pojo = Pojos.pojo(expert, en, SqlType.SELECT);
        pojo.setEntity(en);
        pojo.append(Pojos.Items.queryEntityFields());
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.entityViewName());
        return pojo;
    }

    @Override
    public Pojo makeQuery(String tableName) {
        return makeQuery(tableName, "*");
    }

    @Override
    public Pojo makeQuery(String tableName, String fields) {
        String[] ss = tableName.split(":");
        // String idFieldName = ss.length > 1 ? ss[1] : "*";//按id字段来统计,比较快
        Pojo pojo = makePojo(SqlType.SELECT);
        // pojo.append(Pojos.Items.wrap(idFieldName));//与org.nutz.dao.test.normal.QueryTest.query_records_pager()冲突
        pojo.append(Pojos.Items.wrap(fields));
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.wrap(ss[0]));
        return pojo;
    }

    @Override
    public Pojo makeDelete(Entity<?> en) {
        Pojo pojo = Pojos.pojo(expert, en, SqlType.DELETE);
        pojo.setEntity(en);
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.entityTableName());
        return pojo;
    }

    @Override
    public Pojo makeDelete(String tableName) {
        Pojo pojo = makePojo(SqlType.DELETE);
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.wrap(tableName));
        return pojo;
    }

    @Override
    public Pojo makeFunc(String tableName, String funcName, String colName) {
        Pojo pojo = makePojo(SqlType.SELECT);
        pojo.append(Pojos.Items.wrapf("%s(%s) FROM %s", funcName, colName, tableName));
        return pojo;
    }

    static class GeneratedKeys implements PojoCallback {

        @Override
        public Object invoke(Connection conn, ResultSet rs, final Pojo pojo, Statement stmt)
                throws SQLException {
            final ResultSet _rs = stmt.getGeneratedKeys();
            Object obj = pojo.getOperatingObject();
            if (obj instanceof Map) {
                obj = Arrays.asList(obj);
            }
            Lang.each(obj, new Each<Object>() {
                @Override
                public void invoke(int index, Object ele, int length)
                        throws ExitLoop, ContinueLoop, LoopException {
                    try {
                        if (!_rs.next()) {
                            throw new ExitLoop();
                        }
                        Object key = _rs.getObject(1);
                        pojo.getEntity().getIdField().setValue(ele, key);
                    }
                    catch (SQLException e) {
                        throw new DaoException(e);
                    }
                }
            });
            return pojo.getOperatingObject();
        }
    }

    /**
     * 按联接进行查询
     * @param en
     * @param regex
     * @return
     */
    @Override
    public Pojo makeQueryByJoin(final Entity<?> en, String regex) {
        final Pojo pojo = Pojos.pojo(expert, en, SqlType.SELECT);
        pojo.setEntity(en);
        pojo.append(new QueryJoinFeilds(en, true, en.getTableName()));
        en.visitOne(null, regex, new LinkVisitor() {
            @Override
            public void visit(Object obj, LinkField lnk) {
                pojo.append(Pojos.Items.wrap(","));
                pojo.append(new QueryJoinFeilds(lnk.getLinkedEntity(), false, lnk.getName()));
            }
        });
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.entityViewName());
        en.visitOne(null, regex, new LinkVisitor() {
            @Override
            public void visit(Object obj, LinkField lnk) {
                PItem item = expert.formatLeftJoinLink(obj, lnk, en);
                if (item != null)
                	pojo.append(item);
            }
        });
        return pojo;
    }

    @Override
    public Pojo makeCountByJoin(final Entity<?> en, String regex) {
        final Pojo pojo = Pojos.pojo(expert, en, SqlType.SELECT);
        pojo.setEntity(en);
        pojo.append(Pojos.Items.wrap("count(1)"));
        pojo.append(Pojos.Items.wrap("FROM"));
        pojo.append(Pojos.Items.entityViewName());
        en.visitOne(null, regex, new LinkVisitor() {
            @Override
            public void visit(Object obj, LinkField lnk) {
                PItem item = expert.formatLeftJoinLink(obj, lnk, en);
                if (item != null)
                	pojo.append(item);
            }
        });
        return pojo;
    }
    
    protected static class QueryJoinFeilds extends NoParamsPItem {

        private static final long serialVersionUID = 1L;
        protected Entity<?> en;
        protected boolean main;
        protected String tableName;

        public QueryJoinFeilds(Entity<?> en, boolean main, String tableName) {
            this.en = en;
            this.main = main;
            this.tableName = tableName;
        }

        @Override
        public void joinSql(Entity<?> en, StringBuilder sb) {
            en = this.en;
            FieldMatcher fm = getFieldMatcher();
            List<MappingField> efs = _en(en).getMappingFields();

            int old = sb.length();

            for (MappingField ef : efs) {
                if (fm == null || fm.match(ef.getName())) {
                    sb.append(tableName)
                      .append(".")
                      .append(ef.getColumnNameInSql())
                      .append(" as ");
                    if (!main) {
                        sb.append(tableName).append("_z_");
                    }
                    sb.append(ef.getColumnNameInSql()).append(',');
                }
            }

            if (sb.length() == old) {
                throw Lang.makeThrow("No columns be queryed: '%s'", _en(en));
            }

            sb.setCharAt(sb.length() - 1, ' ');
        }

    }

    /**
     * 设置安全的表名
     * @param tableName
     * @return
     */
    protected String safeTableName(String tableName) {
        if (!Daos.CHECK_COLUMN_NAME_KEYWORD) {
            //return tableName;
        }
        String str = expert.wrapKeyword(tableName, Daos.FORCE_WRAP_COLUMN_NAME);
        return str == null ? tableName : str;
    }
}
