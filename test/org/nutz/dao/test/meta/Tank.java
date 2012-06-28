package org.nutz.dao.test.meta;

import java.util.Map;

import org.nutz.dao.entity.annotation.*;

@Table("dao_d_tank_${id}")
public class Tank {

    public static Tank make(String code) {
        Tank t = new Tank();
        t.code = code;
        return t;
    }

    @Column
    @Id
    private int id;

    @Column
    @Name
    private String code;

    @Column
    private String motorName;

    @Column
    private int weight;

    @One(target = Soldier.class, field = "motorName")
    private Soldier motorman;

    @ManyMany(target = Soldier.class, relation = "dao_d_m_soldier_tank_${id}", from = "tid", to = "sname", key = "name")
    private Map<String, Soldier> members;

    public Tank addMember(Soldier s) {
        members.put(s.getName(), s);
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMotorName() {
        return motorName;
    }

    public void setMotorName(String motorName) {
        this.motorName = motorName;
    }

    public Soldier getMotorman() {
        return motorman;
    }

    public void setMotorman(Soldier motorman) {
        this.motorman = motorman;
    }

    public Map<String, Soldier> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Soldier> members) {
        this.members = members;
    }

}
