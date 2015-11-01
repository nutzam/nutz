package org.nutz.dao.test.normal;

import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;

@Table("t_bean_uuid")
public class BeanWithUUID {

    @Name
    @Prev(els=@EL("uuid()"))
    private String name;
    
    private int age;
    
    private boolean locked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
