package org.nutz.dao.texp;

import org.nutz.dao.entity.annotation.*;

@Table("t_worker")
public class Worker {

    @Column("wid")
    @Id
    public int id;

    @Column("wname")
    @Name
    public String name;

    @Column("ct")
    public String city;

    @Column
    public short age;

    @Column("days")
    public int workingDay;

}
