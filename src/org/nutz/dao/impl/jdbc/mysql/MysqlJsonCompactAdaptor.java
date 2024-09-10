package org.nutz.dao.impl.jdbc.mysql;

import org.nutz.json.JsonFormat;

/**
 * 数据库中使用 {@code JsonFormat.compact()} 格式来保存JSON类型字段的值。
 * <p />
 * 使用时 {@code ColDefine} 注解的 {@code adaptor} 属性显示声明为 {@code MysqlJsonCompactAdaptor.class}
 * <p />
 * <pre>
 * {@code
 * @ColDefine(customType = "json", type = ColType.MYSQL_JSON, adaptor = MysqlJsonCompactAdaptor.class)
 * private Information info;
 * }
 * </pre>
 */
public class MysqlJsonCompactAdaptor extends MysqlJsonAdaptor {

    public MysqlJsonCompactAdaptor() {
        setJsonFormat(JsonFormat.compact());
    }
}
