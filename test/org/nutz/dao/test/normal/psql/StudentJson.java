package org.nutz.dao.test.normal.psql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

@Table("t_psql_student_json")
public class StudentJson extends Student {

    public StudentJson() {}

    public StudentJson(ResultSet rs) throws SQLException {
        this.setId(rs.getInt("id"));
        this.data = NutMap.WRAP(rs.getString("data"));
        this.studentResult = Json.fromJson(StudentResult.class, rs.getString("studentResult"));
    }

    @ColDefine(customType = "json", type = ColType.PSQL_JSON)
    private NutMap data;

    @ColDefine(customType = "json", type = ColType.PSQL_JSON)
    private StudentResult studentResult;

    public NutMap getData() {
        return data;
    }

    public void setData(NutMap data) {
        this.data = data;
    }

    public StudentResult getStudentResult() {
        return studentResult;
    }

    public void setStudentResult(StudentResult studentResult) {
        this.studentResult = studentResult;
    }
}
