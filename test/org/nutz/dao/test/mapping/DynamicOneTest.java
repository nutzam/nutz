package org.nutz.dao.test.mapping;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.nutz.dao.TableName;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.meta.Soldier;
import org.nutz.dao.test.meta.Tank;
import org.nutz.trans.Atom;

public class DynamicOneTest extends DaoCase {

    private Platoon platoon;

    @Override
    protected void before() {
        pojos.init();
        platoon = pojos.create4Platoon(Base.make("blue"), "seals");
    }

    @Override
    protected void after() {
        Platoon p = dao.fetch(Platoon.class, "seals");
        pojos.dropPlatoon(p.getId());
    }

    @Test
    public void fetch_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Platoon p = dao.fetchLinks(dao.fetch(Platoon.class), "leader");
                assertEquals("ZZH", p.getLeaderName());
                assertEquals("ZZH", p.getLeader().getName());
            }
        });
    }

    @Test
    public void delete_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Tank t = dao.fetchLinks(dao.fetch(Tank.class, "M1-A1"), "motorman");
                dao.deleteLinks(t, "motorman");
                assertEquals(4, dao.count(Soldier.class));
                assertEquals(2, dao.count(Tank.class));
            }
        });
    }

    @Test
    public void delete_with() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Tank t = dao.fetchLinks(dao.fetch(Tank.class, "M1-A1"), "motorman");
                dao.deleteWith(t, "motorman");
                assertEquals(4, dao.count(Soldier.class));
                assertEquals(1, dao.count(Tank.class));
            }
        });
    }

    @Test
    public void clear_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Tank t = dao.fetch(Tank.class, "M1-A1");
                dao.clearLinks(t, "motorman");
                assertEquals(4, dao.count(Soldier.class));
                assertEquals(2, dao.count(Tank.class));
            }
        });
    }

    @Test
    public void update_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Tank t = dao.fetchLinks(dao.fetch(Tank.class, "M1-A1"), "motorman");
                t.getMotorman().setAge(32);
                dao.updateLinks(t, "motorman");
                Soldier s = dao.fetch(Soldier.class, t.getMotorName());
                assertEquals(32, s.getAge());
            }
        });
    }

    @Test
    public void update_with() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Tank t = dao.fetchLinks(dao.fetch(Tank.class, "M1-A1"), "motorman");
                t.getMotorman().setAge(32);
                t.setWeight(50);
                dao.updateWith(t, "motorman");
                Soldier s = dao.fetch(Soldier.class, t.getMotorName());
                assertEquals(32, s.getAge());
                t = dao.fetch(Tank.class, t.getId());
                assertEquals(50, t.getWeight());
            }
        });
    }

}
