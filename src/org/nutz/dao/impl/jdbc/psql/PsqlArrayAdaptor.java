package org.nutz.dao.impl.jdbc.psql;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.jdbc.ValueAdaptor;

/**
 * 为 PostgreSQL 数据库封装对数组类型的支持
 * <p/>
 * 注意，需要给 POJO 添加<b>带一个参数的静态工厂方法</b>或者<b>带一个参数的构造函数</b>，<br>
 * 显示的使用 java.sql.ResultSet 来创建该 POJO，不然会出现无法映射的错误。
 * <p/>
 * <code>
public class Pet {

    public static Pet getInstance(ResultSet rs) throws SQLException {
        Pet pet = new Pet();
        pet.setId(rs.getInt("id"));
        pet.setPayByQuarter((Integer[]) rs.getArray("pay_by_quarter").getArray());
        return pet;
    }

    &#64;Column("pay_by_quarter")
    &#64;ColDefine(customType = "integer[]", type = ColType.PSQL_ARRAY)
    private Integer[] payByQuarter;

    // ... 省略后面代码，包括字段声明以及 getter 和 setter
}

public class Jone {

    public static Jone(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.schedule = (String[]) rs.getArray("schedule").getArray();
    }

    &#64;ColDefine(customType = "varchar[]", type = ColType.PSQL_ARRAY)
    private String[] schedule;

    // ... 省略后面代码，包括字段声明以及 getter 和 setter
}
</code>
 * 
 */
public class PsqlArrayAdaptor implements ValueAdaptor {

    private String customDbType;

    public PsqlArrayAdaptor(String customDbType) {
        this.customDbType = customDbType;
    }

    
    public Object get(ResultSet rs, String colName) throws SQLException {
        return rs.getObject(colName);
    }

    
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            stat.setNull(index, Types.NULL);
        } else {
            String typeName = customDbType.substring(0, customDbType.length() - 2);
            Array array = stat.getConnection().createArrayOf(typeName, (Object[]) obj);
            stat.setObject(index, array, Types.ARRAY);
        }
    }
}
