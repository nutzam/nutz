package org.nutz.dao.impl.jdbc.h2;

import org.nutz.dao.DB;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.entity.macro.SqlFieldMacro;
import org.nutz.dao.impl.jdbc.psql.PsqlJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.sql.Pojo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class H2JdbcExpert extends PsqlJdbcExpert {

    public H2JdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    public String getDatabaseType() {
        return DB.H2.name();
    }

    public Pojo fetchPojoId(Entity<?> en, MappingField idField) {
        String autoSql = "SELECT IDENTITY() as $field from $view";
        Pojo autoInfo = new SqlFieldMacro(idField, autoSql);
        autoInfo.setEntity(en);
        return autoInfo;
    }

    @Override
    public List<String> getIndexNames(Entity<?> en, Connection conn) throws SQLException {
        List<String> names = new ArrayList<String>();
        // 仅返回非自动生成的索引,排除主键/唯一约束背后的系统索引
        String showIndexs = "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.INDEXES "
                            + "WHERE TABLE_NAME = ? AND IS_GENERATED = FALSE";
        PreparedStatement preparedStatement = conn.prepareStatement(showIndexs);
        preparedStatement.setString(1, en.getTableName().toUpperCase());
        ResultSet rest = preparedStatement.executeQuery();
        while (rest.next()) {
            String index = rest.getString("INDEX_NAME");
            names.add(index);
        }
        return names;
    }

    public void checkDataSource(Connection conn) throws SQLException {
    }
}
