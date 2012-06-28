package org.nutz.dao.test.normal;

import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.test.meta.Pet;

public class Pet2 extends Pet {

    @Column("alias")
    @Prev(@SQL("SELECT 'dog.$name'"))
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public Pet2 setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

}
