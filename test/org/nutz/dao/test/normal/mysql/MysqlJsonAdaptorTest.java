package org.nutz.dao.test.normal.mysql;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.test.DaoCase;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public class MysqlJsonAdaptorTest extends DaoCase {

    @Override
    protected void before() {
        if (!dao.meta().isMySql()) {
            return;
        }
        dao.create(MysqlJsonAdaptorTestBean.class, true);
    }

    @Test
    public void adapotor() {
        if (!dao.meta().isMySql()) {
            return;
        }

        MysqlJsonAdaptorTestBean testBean = new MysqlJsonAdaptorTestBean();
        StudentResult result = new StudentResult();
        result.setPhysics(new BigDecimal("100"));
        testBean.setNoneAdaptor(result);
        testBean.setJsonAdaptor(result);
        testBean.setJsonCompactAdaptor(result);
        testBean.setJsonTidyAdaptor(result);

        int insertId = dao.insert(testBean).getId();

        org.nutz.dao.entity.Record record = dao.fetch("t_mysql_json_adaptor_test_bean", Cnd.where("id","=",insertId));
        // mysql 在保存 json 格式字段的时候会自动格式化该字段的值
        // mariadb 的话就没问题
        assertEquals(Json.toJson(result, JsonFormat.tidy()), record.getString("noneAdaptor"));
        assertEquals(Json.toJson(result, JsonFormat.tidy()), record.getString("noneAdaptor"));
        assertEquals(Json.toJson(result, JsonFormat.tidy()), record.getString("jsonAdaptor"));
        assertEquals(Json.toJson(result, JsonFormat.compact()), record.getString("jsonCompactAdaptor"));
        assertEquals(Json.toJson(result, JsonFormat.tidy()), record.getString("jsonTidyAdaptor"));
    }
}
