package org.nutz.dao.test.meta;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nutz.dao.entity.annotation.*;

@Table("dao_base")
public class Base {

    public static Base make(String name) {
        Base b = new Base();
        b.setName(name);
        return b;
    }

    @Column
    @Name
    private String name;

    @Column("cid")
    private int countryId;

    @Column("lvl")
    private int level;

    @One(target = Country.class, field = "countryId")
    private Country country;

    @ManyMany(target = Fighter.class, relation = "dao_m_base_fighter", from = "bname", to = "fid")
    private List<Fighter> fighters;

    @Many(target = WaveBand.class, field = "")
    private List<WaveBand> wavebands;

    @Many(target = Platoon.class, field = "baseName", key = "name")
    private Map<String, Platoon> platoons;

    public List<WaveBand> getWavebands() {
        return wavebands;
    }

    public void setWavebands(List<WaveBand> wavebands) {
        this.wavebands = wavebands;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Map<String, Platoon> getPlatoons() {
        return platoons;
    }

    public void addPlatoon(Platoon p) {
        platoons.put(p.getName(), p);
    }

    public void setPlatoons(Map<String, Platoon> platoons) {
        this.platoons = platoons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Fighter> getFighters() {
        return fighters;
    }

    public void setFighters(List<Fighter> fighters) {
        this.fighters = fighters;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public int countFighter(Fighter.TYPE type) {
        int re = 0;
        for (Iterator<Fighter> it = fighters.iterator(); it.hasNext();)
            if (it.next().getType() == type)
                re++;
        return re;
    }
}
