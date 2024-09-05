package org.nutz.dao.impl.jdbc.mysql;

import org.nutz.json.JsonFormat;

/**
 * 数据库中使用 {@code JsonFormat.tidy()} 格式来保存JSON类型字段的值。
 * <p />
 * 使用时 {@code ColDefine} 注解的 {@code adaptor} 属性显示声明为 {@code MysqlJsonTidyAdaptor.class}
 * <p />
 * <pre>
 * {@code
 * @ColDefine(customType = "json", type = ColType.MYSQL_JSON, adaptor = MysqlJsonTidyAdaptor.class)
 * private Information info;
 * }
 * </pre>
 */
public class MysqlJsonTidyAdaptor extends MysqlJsonAdaptor {

    public MysqlJsonTidyAdaptor() {
        setJsonFormat(JsonFormat.tidy());
    }
}
