package org.nutz.dao.test.normal;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.entity.annotation.*;

@Table("t_killer")
public class Killer {

    public Killer() {
        killeds = new ArrayList<Resident>(5);
    }

    public Killer(String name) {
        this();
        this.name = name;
    }

    @Column("killerid")
    @Id
    private int id;

    @Column
    @Name
    private String name;

    @Column("killed")
    @Prev(@SQL("SELECT COUNT(*) FROM t_killer_re WHERE killer=@name"))
    private int killedCount;

    @Column("last")
    @Prev(@SQL("SELECT name FROM t_resident WHERE id IN (SELECT rid FROM t_killer_re WHERE killer=@name) ORDER BY name DESC"))
    private String lastKillName;

    @ManyMany(target = Resident.class, relation = "t_killer_re", from = "killer:name", to = "rid")
    private List<Resident> killeds;

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

    public int getKilledCount() {
        return killedCount;
    }

    public void setKilledCount(int killedCount) {
        this.killedCount = killedCount;
    }

    public String getLastKillName() {
        return lastKillName;
    }

    public void setLastKillName(String lastDogName) {
        this.lastKillName = lastDogName;
    }

    public List<Resident> getKilleds() {
        return killeds;
    }

    public void setKilleds(List<Resident> killeds) {
        this.killeds = killeds;
    }

    public void kill(Resident re) {
        killeds.add(re);
    }

}
