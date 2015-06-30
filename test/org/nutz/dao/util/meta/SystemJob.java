package org.nutz.dao.util.meta;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.EL;

@Table("t_daoup_job")
public class SystemJob {

    @Id
    private int id;
    
    @Prev(els={@EL("uuid()")})
    @Column("nm")
    @Name
    private String name;
    
    @Column("u_id")
    private int userId;
    
    @Column("st")
    private int state;
    
    @One(target=SystemUser.class, field="userId")
    private SystemUser user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public SystemUser getUser() {
        return user;
    }

    public void setUser(SystemUser user) {
        this.user = user;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    
}
