package org.nutz.dao.test.meta.issue1163;

import java.util.ArrayList;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("t_1163_master")
public class Issue1163Master {

    @Id
    private int id;
    @Name
    private String name;
    
    private int gpet_id;
    
    @Many(field="master_id")
    private ArrayList<Issue1163Pet> pets;
    
    @One(field="gpet_id")
    private Issue1163Pet gpet;
    
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
    public ArrayList<Issue1163Pet> getPets() {
        return pets;
    }
    public void setPets(ArrayList<Issue1163Pet> pets) {
        this.pets = pets;
    }
    public int getGpet_id() {
        return gpet_id;
    }
    public void setGpet_id(int gpet_id) {
        this.gpet_id = gpet_id;
    }
    public Issue1163Pet getGpet() {
        return gpet;
    }
    public void setGpet(Issue1163Pet gpet) {
        this.gpet = gpet;
    }
    
    

}
