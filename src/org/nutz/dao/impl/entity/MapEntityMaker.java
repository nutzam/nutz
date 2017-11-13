package org.nutz.dao.impl.entity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.field.NutMappingField;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Mirror;
import org.nutz.lang.eject.EjectFromMap;
import org.nutz.lang.inject.InjectToMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class MapEntityMaker {

    private static final Log log = Logs.get();

    protected JdbcExpert expert;

    protected DataSource dataSource;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends Map<String, ?>> Entity<T> make(String tableName, T map) {
        final NutEntity<T> en = new NutEntity(map.getClass());
        en.setTableName(tableName);
        en.setViewName(tableName);
        boolean check = false;
        for (Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            // 是实体补充描述吗？
            if (key.startsWith("#")) {
                en.getMetas().put(key.substring(1), entry.getValue().toString());
                continue;
            }
            // 以 "." 开头的字段，不是实体字段
            else if (key.startsWith(".")) {
                continue;
            }

            // 是实体字段
            Object value = entry.getValue();
            Mirror<?> mirror = Mirror.me(value);
            NutMappingField ef = new NutMappingField(en);

            while (true) {
                if (key.startsWith("+")) {
                    ef.setAsAutoIncreasement();
                    if (mirror != null && mirror.isIntLike())
                        ef.setAsId();
                    key = key.substring(1);
                } else if (key.startsWith("!")) {
                    ef.setAsNotNull();
                    key = key.substring(1);
                } else if (key.startsWith("*")) {
                    key = key.substring(1);
                    if (mirror != null && mirror.isIntLike())
                        ef.setAsId();
                    else
                        ef.setAsName();
                } else {
                    break;
                }
            }
            ef.setName(key);
            String columnName = key;
            // 强制大写?
            if (Daos.FORCE_UPPER_COLUMN_NAME) {
                ef.setColumnName(columnName.toUpperCase());
            }
            else {
            	ef.setColumnName(columnName);
            }
            // 强制包裹?
            if (Daos.FORCE_WRAP_COLUMN_NAME) {
                ef.setColumnNameInSql(expert.wrapKeywork(columnName, true));
            }
            else if (Daos.CHECK_COLUMN_NAME_KEYWORD) {
                ef.setColumnNameInSql(expert.wrapKeywork(columnName, false));
            }

            // 类型是啥呢?
            if (map.containsKey("." + key + ".type")) {
                ef.setType((Class) map.get("." + key + ".type"));
            } else {
                ef.setType(null == value ? Object.class : value.getClass());
            }
            // ColType是啥呢?
            if (map.containsKey("." + key + ".coltype")) {
                ef.setColumnType((ColType) map.get("." + key + ".coltype"));
            } else {
                // 猜测一下数据库类型
                Jdbcs.guessEntityFieldColumnType(ef);
            }
            // 适配器类型是啥呢?
            if (map.containsKey("." + key + ".adaptor")) {
                ef.setAdaptor((ValueAdaptor) map.get("." + key + ".adapter"));
            } else {
                ef.setAdaptor(expert.getAdaptor(ef));
            }
            ef.setInjecting(new InjectToMap(key)); // 这里比较纠结,回设的时候应该用什么呢?
            ef.setEjecting(new EjectFromMap(entry.getKey()));

            if (ef.isAutoIncreasement()
                && ef.isId()
                && expert.isSupportAutoIncrement()
                && !expert.isSupportGeneratedKeys()) {
                en.addAfterInsertMacro(expert.fetchPojoId(en, ef));
            }

            en.addMappingField(ef);

            if (mirror != null && !check)
                check = mirror.isEnum();
        }
        en.checkCompositeFields(null);

        // 最后在数据库中验证一下实体各个字段
        if (check) {
            Connection conn = null;
            try {
                try {
                    conn = dataSource.getConnection();
                    expert.setupEntityField(conn, en);
                }
                finally {
                    if (conn != null)
                        conn.close();
                }
            }
            catch (SQLException e) {
                log.debug(e.getMessage(), e);
            }
        }

        // 搞定返回
        return en;
    }

    public void init(DataSource datasource, JdbcExpert expert, EntityHolder holder) {
        this.expert = expert;
        this.dataSource = datasource;
    }

}
