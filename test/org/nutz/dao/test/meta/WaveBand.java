package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;

@Table("dao_waveband")
public class WaveBand {

    public static WaveBand make(String name, double value) {
        WaveBand wb = new WaveBand();
        wb.name = name;
        wb.value = value;
        return wb;
    }

    @Column
    @Name
    private String name;

    @Column
    private double value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
