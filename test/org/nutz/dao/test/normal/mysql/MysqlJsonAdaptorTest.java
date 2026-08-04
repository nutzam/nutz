package org.nutz.dao.test.normal.mysql;

import java.math.BigDecimal;

import org.json.JSONException;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.test.DaoCase;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.skyscreamer.jsonassert.JSONAssert;

public class MysqlJsonAdaptorTest extends DaoCase {

    @Override
    protected void before() {
        if (!dao.meta().isMySql()) {
            return;
        }
        dao.create(MysqlJsonAdaptorTestBean.class, true);
    }

    @Test
    public void adapotor() throws JSONException {
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
        // MySQL 8 还会对JSON列做规范化(键排序/空格), 所以这里按JSON语义比较(宽松模式,忽略键顺序)
        String expected = Json.toJson(result, JsonFormat.compact());
        JSONAssert.assertEquals(expected, record.getString("noneAdaptor"), false);
        JSONAssert.assertEquals(expected, record.getString("jsonAdaptor"), false);
        JSONAssert.assertEquals(expected, record.getString("jsonCompactAdaptor"), false);
        JSONAssert.assertEquals(expected, record.getString("jsonTidyAdaptor"), false);
    }
}
