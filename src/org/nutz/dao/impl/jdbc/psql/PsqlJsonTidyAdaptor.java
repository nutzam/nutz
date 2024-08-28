package org.nutz.dao.impl.jdbc.psql;

import org.nutz.json.JsonFormat;

/**
 * 数据库中使用 {@code JsonFormat.tidy()} 格式来保存JSON类型字段的值。
 * <p />
 * 使用时 {@code ColDefine} 注解的 {@code adaptor} 属性显示声明为 {@code PsqlJsonTidyAdaptor.class}
 * <p />
 * <pre>
 * {@code
 * @ColDefine(customType = "json", type = ColType.PSQL_JSON, adaptor = PsqlJsonTidyAdaptor.class)
 * private Information info;
 * }
 * </pre>
 */
public class PsqlJsonTidyAdaptor extends PsqlJsonAdaptor {

    public PsqlJsonTidyAdaptor() {
        setJsonFormat(JsonFormat.tidy());
    }
}
