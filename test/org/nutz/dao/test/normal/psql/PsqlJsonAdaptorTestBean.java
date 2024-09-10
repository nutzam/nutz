package org.nutz.dao.test.normal.psql;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.impl.jdbc.psql.PsqlJsonAdaptor;
import org.nutz.dao.impl.jdbc.psql.PsqlJsonCompactAdaptor;
import org.nutz.dao.impl.jdbc.psql.PsqlJsonTidyAdaptor;

@Table("t_psql_json_adaptor_test_bean")
public class PsqlJsonAdaptorTestBean {

    @Id
    private int id;

    @ColDefine(customType = "jsonb", type = ColType.PSQL_JSON)
    private StudentResult noneAdaptor;

    @ColDefine(customType = "json", type = ColType.PSQL_JSON, adaptor = PsqlJsonAdaptor.class)
    private StudentResult jsonAdaptor;

    @ColDefine(customType = "json", type = ColType.PSQL_JSON, adaptor = PsqlJsonCompactAdaptor.class)
    private StudentResult jsonCompactAdaptor;

    @ColDefine(customType = "json", type = ColType.PSQL_JSON, adaptor = PsqlJsonTidyAdaptor.class)
    private StudentResult jsonTidyAdaptor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public org.nutz.dao.test.normal.psql.StudentResult getNoneAdaptor() {
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
