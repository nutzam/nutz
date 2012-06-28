package org.nutz.service.pojo;

import org.nutz.dao.entity.annotation.*;

@Table("srv_profile")
public class Profile {

    public Profile() {
        super();
    }

    /**
     * @param id
     * @param city
     */
    public Profile(int id, String city) {
        this.id = id;
        this.city = city;
    }

    @Column
    @Id(auto = false)
    public int id;

    @Column
    public String city;

}
