package org.nutz.dao.test.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import org.nutz.dao.Dao;
import org.nutz.dao.TableName;
import org.nutz.service.*;
import org.nutz.trans.Atom;

public class Pojos extends Service {

    private Dao dao;

    public Pojos(Dao dao) {
        super(dao);
        this.dao = dao;
    }

    public void initPet() {
        dao.create(Pet.class, true);
    }

    public void init() {
        synchronized (Pojos.class) {
            dao.create(Country.class, true);
            dao.create(Platoon.class, true);
            dao.create(Base.class, true);
            dao.create(Fighter.class, true);
            dao.create(WaveBand.class, true);
        }
    }

    public Platoon create4Platoon(Base base, String name) {
        final Platoon p = dao().insert(Platoon.make(base, name));
        int id = p.getId();
        initPlatoon(id);
        TableName.run(id, new Atom() {
            public void run() {
                Soldier mick = Soldier.make("Mick");
                Soldier zzh = Soldier.make("ZZH");
                Soldier peter = Soldier.make("Peter");
                Soldier sm = Soldier.make("Super Man");
                Soldier bush = Soldier.make("George.W.Bush");
                p.setTanks(new HashMap<String, Tank>());

                Tank m1a1 = p.addTank(Tank.make("M1-A1"));
                m1a1.setMotorName(bush.getName());
                

                Tank t92 = p.addTank(Tank.make("T92"));
                t92.setMotorName(zzh.getName());

                p.setLeaderName(zzh.getName());
                p.setLeader(zzh);

                p.setSoliders(new LinkedList<Soldier>());
                p.getSoliders().add(mick);
                p.getSoliders().add(peter);
                p.getSoliders().add(sm);
                p.getSoliders().add(bush);

                dao.update(p);
                dao.insertLinks(p, "leader|soliders|tanks");

                m1a1 = dao.fetch(Tank.class, "M1-A1");
                m1a1.setMembers(new HashMap<String, Soldier>());
                m1a1.addMember(bush).addMember(sm).addMember(zzh);
                
                t92 = dao.fetch(Tank.class, "T92");
                t92.setMembers(new HashMap<String, Soldier>());
                t92.addMember(zzh).addMember(mick).addMember(peter);

                dao.insertRelation(m1a1, "members");
                dao.insertRelation(t92, "members");
                dao.insertLinks(Gun.assign(mick, Gun.TYPE.AK47, Gun.TYPE.P228), "guns");
                dao.insertLinks(Gun.assign(zzh, Gun.TYPE.AWP, Gun.TYPE.MP5, Gun.TYPE.P228), "guns");
                dao.insertLinks(Gun.assign(peter, Gun.TYPE.M16, Gun.TYPE.UMP_45), "guns");
                dao.insertLinks(Gun.assign(sm, Gun.TYPE.M16, Gun.TYPE.UMP_45), "guns");
                dao.insertLinks(Gun.assign(bush, Gun.TYPE.M60, Gun.TYPE.P228), "guns");
            }
        });
        return p;
    }

    public void initPlatoon(int id) {
        TableName.set(id);
        dao.create(Soldier.class, true);
        dao.create(Tank.class, true);
        dao.create(Gun.class, true);
        dao.create(Mission.class, true);
        TableName.clear();
    }

    public void dropPlatoon(int id) {
        TableName.set(id);
        dao.drop(Soldier.class);
        dao.drop(Tank.class);
        dao.drop(Gun.class);
        dao.drop(Mission.class);
        TableName.clear();
    }

    public void initData() {
        init();
        dao.insert(WaveBand.make("FM_A", 107.9));
        dao.insert(WaveBand.make("FM_B", 104.83));
        dao.insert(WaveBand.make("DQ_99", 109.99));
        dao.insert(WaveBand.make("X", 101.21));

        Base b = Base.make("red");
        b.setCountry(Country.make("China"));
        b.setFighters(new ArrayList<Fighter>(6));
        b.getFighters().add(Fighter.make(Fighter.TYPE.SU_35));
        b.getFighters().add(Fighter.make(Fighter.TYPE.SU_27));
        b.getFighters().add(Fighter.make(Fighter.TYPE.SU_27));
        b.getFighters().add(Fighter.make(Fighter.TYPE.SU_31));
        b.getFighters().add(Fighter.make(Fighter.TYPE.SU_31));
        b.getFighters().add(Fighter.make(Fighter.TYPE.SU_31));
        b.setPlatoons(new HashMap<String, Platoon>());
        b.addPlatoon(Platoon.make(b, "C"));
        b.addPlatoon(Platoon.make(b, "ES"));
        b.addPlatoon(Platoon.make(b, "DT"));
        dao.insertWith(b, "country|fighters|platoons");

        b = Base.make("blue");
        b.setCountry(Country.make("US"));
        b.setFighters(new LinkedList<Fighter>());
        b.getFighters().add(Fighter.make(Fighter.TYPE.F117A));
        b.getFighters().add(Fighter.make(Fighter.TYPE.F22));
        b.getFighters().add(Fighter.make(Fighter.TYPE.F15));
        b.getFighters().add(Fighter.make(Fighter.TYPE.F15));
        b.getFighters().add(Fighter.make(Fighter.TYPE.F16));
        b.getFighters().add(Fighter.make(Fighter.TYPE.F16));
        b.getFighters().add(Fighter.make(Fighter.TYPE.F16));
        b.setPlatoons(new HashMap<String, Platoon>());
        b.addPlatoon(Platoon.make(b, "SF"));
        b.addPlatoon(Platoon.make(b, "DF"));
        b.addPlatoon(Platoon.make(b, "seals"));
        dao.insertLinks(b, "country");
        dao.insert(b);
        dao.insertLinks(b, "fighters|platoons");
    }

}
