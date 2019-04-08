package org.nutz.dao.test.meta.nutzcn;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("t_abc_pet")
public class AbcPet {
    
    @Id
    private long id;
    
    private String name;
    
    private long userId;
    
    @One
    private AbcUser user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public AbcUser getUser() {
        return user;
    }

    public void setUser(AbcUser user) {
        this.user = user;
    }
}