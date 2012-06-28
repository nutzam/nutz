package org.nutz.dao.test.meta;

import org.nutz.dao.entity.annotation.*;

@Table("bean_with_default")
public class BeanWithDefault {

    @Id
    private int id;

    @Name
    private String name;

    @Column("alias")
    @Default("--")
    private String alias;

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
