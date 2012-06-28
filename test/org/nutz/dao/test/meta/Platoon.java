package org.nutz.dao.test.meta;

import java.util.List;
import java.util.Map;

import org.nutz.dao.entity.annotation.*;

@Table("dao_platoon")
public class Platoon {

    public static Platoon make(Base base, String name) {
        Platoon p = new Platoon();
        p.name = name;
        p.setBase(base);
        p.setBaseName(base.getName());
        return p;
    }

    @Column
    @Id
    private int id;

    @Column
    @Name(casesensitive = false)
    private String name;

    @Column("base")
    private String baseName;

    @One(target = Base.class, field = "baseName")
    private Base base;

    @Column("leader")
    private String leaderName;

    @One(target = Soldier.class, field = "leaderName")
    private Soldier leader;

    @Many(target = Soldier.class, field = "")
    private List<Soldier> soliders;

    @Many(target = Tank.class, field = "", key = "code")
    private Map<String, Tank> tanks;

    public Tank addTank(Tank tank) {
        tanks.put(tank.getCode(), tank);
        return tank;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

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

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public Soldier getLeader() {
        return leader;
    }

    public void setLeader(Soldier leader) {
        this.leader = leader;
    }

    public List<Soldier> getSoliders() {
        return soliders;
    }

    public void setSoliders(List<Soldier> soliders) {
        this.soliders = soliders;
    }

    public Map<String, Tank> getTanks() {
        return tanks;
    }

    public void setTanks(Map<String, Tank> tanks) {
        this.tanks = tanks;
    }

}
