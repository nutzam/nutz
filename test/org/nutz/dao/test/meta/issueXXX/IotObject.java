package org.nutz.dao.test.meta.issueXXX;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_iot_object")
public class IotObject {

    @Id
    private int id;

    @ColDefine(type=ColType.INT)
    protected IotProductStatus stat;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IotProductStatus getStat() {
        return stat;
    }

    public void setStat(IotProductStatus stat) {
        this.stat = stat;
    }
}
