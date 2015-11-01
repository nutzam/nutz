package org.nutz.dao.test.meta.issue901;

import java.math.BigDecimal;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table(value = "t_x_place")
public class XPlace {
    @Id
    private int id;
    @ColDefine(width=255, type=ColType.VARCHAR)
    private BigDecimal lng = new BigDecimal(0);
    @ColDefine(width=255, type=ColType.VARCHAR)
    private BigDecimal lat = new BigDecimal(0);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }
}