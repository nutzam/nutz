package org.nutz.dao.impl.jdbc.psql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

/**
 * 为 PostgreSQL 数据库封装对 Json 类型的支持
 * <p/>
 * 数据库里面的 Json 类型的值自动为 String 类型。
 * <p/>
 * 注意，必要的时候需要给 POJO 添加<b>带一个参数的静态工厂方法</b>或者<b>带一个参数的构造函数</b>，<br>
 * 显示的使用 java.sql.ResultSet 来创建该 POJO，不然会出现无法映射的错误。
 * <p/>
 * <pre>
 * public class Pet {
 *
 *     public static Pet getInstance(ResultSet rs) throws SQLException {
 *         // 需要把所有字段从 ResultSet 取出，不然该属性无法映射
 *         Pet pet = new Pet();
 *         pet.setId(rs.getInt("id"));
 *         pet.setData(NutMap.WRAP(rs.getString("data")));
 *         return pet;
 *     }
 *
 *     &#64;Id
 *     private int id;
 *
 *     &#64;ColDefine(customType = "json", type = ColType.PSQL_JSON)
 *     private NutMap data;
 *
 *     // ... 省略后面代码，包括字段声明以及 getter 和 setter
 * }
 *
 * public class Jone {
 *
 *     public Jone(ResultSet rs) throws SQLException {
 *         // 需要把所有字段从 ResultSet 取出，不然该属性无法映射
 *         this.id = rs.getInt("id");
 *         this.info = Json.fromJson(Information.class, rs.getString("info"));
 *     }
 *
 *     &#64;Id
 *     private int id;
 *
 *     &#64;ColDefine(customType = "json", type = ColType.PSQL_JSON)
 *     private Information info;
 *
 *     // ... 省略后面代码，包括字段声明以及 getter 和 setter
 * }
 * </pre>
 *
 */
public class PsqlJsonAdaptor implements ValueAdaptor {

    public Object get(ResultSet rs, String colName) throws SQLException {
        return rs.getObject(colName);
    }

    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            stat.setNull(index, Types.NULL);
        } else {
            stat.setObject(index, Json.toJson(obj, JsonFormat.tidy()), Types.OTHER);
        }
    }
}
