package org.nutz.dao.impl.entity.macro;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.jdbc.NutPojo;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.SqlType;
import org.nutz.el.El;
import org.nutz.lang.util.Context;

public class ElFieldMacro extends NutPojo {

    private El bin;

    private MappingField entityField;

    public SqlType getSqlType() {
        return SqlType.RUN;
    }

    public ElFieldMacro(MappingField field, String str) {
        this.entityField = field;
        this.bin = new El(str);
    }

    private ElFieldMacro() {}

    public void onAfter(Connection conn, ResultSet rs) throws SQLException {
        Context context = entityField.getEntity().wrapAsContext(getOperatingObject());
        Object value = bin.eval(context);
        entityField.setValue(getOperatingObject(), value);
    }

    @Override
    public Pojo duplicate() {
        ElFieldMacro re = new ElFieldMacro();
        re.bin = bin;
        re.entityField = entityField;
        return re;
    }

}
