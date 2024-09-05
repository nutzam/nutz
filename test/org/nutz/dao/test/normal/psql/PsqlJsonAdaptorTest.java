package org.nutz.dao.test.normal.psql;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.test.DaoCase;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public class PsqlJsonAdaptorTest extends DaoCase {

    @Override
    protected void before() {
        if (!dao.meta().isPostgresql()) {
            return;
        }
        dao.create(PsqlJsonAdaptorTestBean.class, true);
    }

    @Test
    public void adapotor() {
        if (!dao.meta().isPostgresql()) {
            return;
        }

        PsqlJsonAdaptorTestBean testBean = new PsqlJsonAdaptorTestBean();
        org.nutz.dao.test.normal.psql.StudentResult result = new StudentResult();
        result.setPhysics(new BigDecimal("100"));
        testBean.setNoneAdaptor(result);
        testBean.setJsonAdaptor(result);
        testBean.setJsonCompactAdaptor(result);
        testBean.setJsonTidyAdaptor(result);

        int insertId = dao.insert(testBean).getId();

        org.nutz.dao.entity.Record record = dao.fetch("t_psql_json_adaptor_test_bean", Cnd.where("id","=",insertId));
        // 设置成 jsonb 格式的时候会自动格式化该字段的值
        assertEquals(Json.toJson(result, JsonFormat.tidy()), record.getString("noneAdaptor"));
        assertEquals(Json.toJson(result, JsonFormat.tidy()), record.getString("noneAdaptor"));
        assertEquals(Json.toJson(result, JsonFormat.tidy()), record.getString("jsonAdaptor"));
        assertEquals(Json.toJson(result, JsonFormat.compact()), record.getString("jsonCompactAdaptor"));
        assertEquals(Json.toJson(result, JsonFormat.tidy()), record.getString("jsonTidyAdaptor"));
    }
}
