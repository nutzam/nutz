package org.nutz.dao.test.entity;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.PK;

@PK("name")
public class TO2 {

    @Id
    public int id;

    public String name;

}
