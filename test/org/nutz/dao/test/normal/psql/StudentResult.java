package org.nutz.dao.test.normal.psql;

import java.math.BigDecimal;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;

public class StudentResult {

    private String mathematics;

    private BigDecimal physics;

    private int foreignLanguage;

    public String getMathematics() {
        return mathematics;
    }

    public void setMathematics(String mathematics) {
        this.mathematics = mathematics;
    }

    public BigDecimal getPhysics() {
        return physics;
    }

    public void setPhysics(BigDecimal physics) {
        this.physics = physics;
    }

    public int getForeignLanguage() {
        return foreignLanguage;
    }

    public void setForeignLanguage(int foreignLanguage) {
        this.foreignLanguage = foreignLanguage;
    }

    public boolean equals(StudentResult otherResult) {
        return Lang.equals(Json.toJson(this, JsonFormat.tidy()),
                           Json.toJson(otherResult, JsonFormat.tidy()));
    }
}
