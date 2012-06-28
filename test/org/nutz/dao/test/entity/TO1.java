package org.nutz.dao.test.entity;

import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.PK;

@PK("id")
public class TO1 {

    public int id;

    @Name
    public String name;

}
