package org.nutz.dao.test.normal.mysql;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.impl.jdbc.mysql.MysqlJsonAdaptor;
import org.nutz.dao.impl.jdbc.mysql.MysqlJsonCompactAdaptor;
import org.nutz.dao.impl.jdbc.mysql.MysqlJsonTidyAdaptor;

@Table("t_mysql_json_adaptor_test_bean")
public class MysqlJsonAdaptorTestBean {

    @Id
    private int id;

    @ColDefine(customType = "json", type = ColType.MYSQL_JSON)
    private StudentResult noneAdaptor;

    @ColDefine(customType = "json", type = ColType.MYSQL_JSON, adaptor = MysqlJsonAdaptor.class)
    private StudentResult jsonAdaptor;

    @ColDefine(customType = "json", type = ColType.MYSQL_JSON, adaptor = MysqlJsonCompactAdaptor.class)
    private StudentResult jsonCompactAdaptor;

    @ColDefine(customType = "json", type = ColType.MYSQL_JSON, adaptor = MysqlJsonTidyAdaptor.class)
    private StudentResult jsonTidyAdaptor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StudentResult getNoneAdaptor() {
        return noneAdaptor;
    }

    public void setNoneAdaptor(StudentResult noneAdaptor) {
        this.noneAdaptor = noneAdaptor;
    }

    public StudentResult getJsonAdaptor() {
        return jsonAdaptor;
    }

    public void setJsonAdaptor(StudentResult jsonAdaptor) {
        this.jsonAdaptor = jsonAdaptor;
    }

    public StudentResult getJsonCompactAdaptor() {
        return jsonCompactAdaptor;
    }

    public void setJsonCompactAdaptor(StudentResult jsonCompactAdaptor) {
        this.jsonCompactAdaptor = jsonCompactAdaptor;
    }

    public StudentResult getJsonTidyAdaptor() {
        return jsonTidyAdaptor;
    }

    public void setJsonTidyAdaptor(StudentResult jsonTidyAdaptor) {
        this.jsonTidyAdaptor = jsonTidyAdaptor;
    }
}
