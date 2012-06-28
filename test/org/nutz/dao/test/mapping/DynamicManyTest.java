package org.nutz.dao.test.mapping;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.nutz.dao.TableName;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Gun;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.meta.Soldier;
import org.nutz.dao.test.meta.Tank;
import org.nutz.trans.Atom;

public class DynamicManyTest extends DaoCase {

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
    public void fetch_null_field_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Platoon p = dao.fetchLinks(dao.fetch(Platoon.class), "tanks|soliders");
                assertEquals(2, p.getTanks().size());
                assertEquals(5, p.getSoliders().size());
            }
        });
    }

    @Test
    public void delete_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Soldier s = dao.fetchLinks(dao.fetch(Soldier.class, "ZZH"), "guns");
                assertEquals(3, s.getGuns().length);
                dao.deleteLinks(s, "guns");
                assertEquals(8, dao.count(Gun.class));
            }
        });
    }

    @Test
    public void delete_null_field_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Platoon p = dao.fetchLinks(dao.fetch(Platoon.class), "tanks|soliders");
                dao.deleteLinks(p, "tanks|soliders");
                assertEquals(1, dao.count(Platoon.class));
                assertEquals(0, dao.count(Soldier.class));
                assertEquals(0, dao.count(Tank.class));
            }
        });
    }

    @Test
    public void delete_links_partly() {
        TableName.run(platoon, new Atom() {
            public void run() {
                TableName.run(platoon, new Atom() {
                    public void run() {
                        Soldier s = dao.fetchLinks(dao.fetch(Soldier.class, "ZZH"), "guns");
                        s.getGuns()[1] = null;
                        dao.deleteLinks(s, "guns");
                        assertEquals(9, dao.count(Gun.class));
                    }
                });
            }
        });
    }

    @Test
    public void delete_null_field_links_partly() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Platoon p = dao.fetchLinks(dao.fetch(Platoon.class), "tanks|soliders");
                p.getTanks().remove("M1-A1");
                p.getSoliders().remove(0);
                dao.deleteLinks(p, "tanks|soliders");
                assertEquals(1, dao.count(Platoon.class));
                assertEquals(1, dao.count(Soldier.class));
                assertEquals(1, dao.count(Tank.class));
                assertEquals(6, dao.count("dao_d_m_soldier_tank_" + platoon.getId()));
            }
        });
    }

    @Test
    public void delete_with() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Soldier s = dao.fetchLinks(dao.fetch(Soldier.class, "ZZH"), "guns");
                dao.deleteWith(s, "guns");
                assertEquals(4, dao.count(Soldier.class));
                assertEquals(8, dao.count(Gun.class));
            }
        });
    }

    @Test
    public void delete_with_partly() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Soldier s = dao.fetchLinks(dao.fetch(Soldier.class, "ZZH"), "guns");
                s.getGuns()[1] = null;
                dao.deleteWith(s, "guns");
                assertEquals(4, dao.count(Soldier.class));
                assertEquals(9, dao.count(Gun.class));
            }
        });
    }

    @Test
    public void clear_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Soldier s = dao.fetch(Soldier.class, "ZZH");
                dao.clearLinks(s, "guns");
                assertEquals(5, dao.count(Soldier.class));
                assertEquals(8, dao.count(Gun.class));
            }
        });
    }

    @Test
    public void clear_null_field_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Platoon p = dao.fetch(Platoon.class);
                dao.clearLinks(p, "tanks|soliders");
                assertEquals(1, dao.count(Platoon.class));
                /*
                 * null field, so relative object will be clear
                 */
                assertEquals(0, dao.count(Soldier.class));
                assertEquals(0, dao.count(Tank.class));
            }
        });
    }

    @Test
    public void update_links() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Soldier s = dao.fetchLinks(dao.fetch(Soldier.class, "ZZH"), "guns");
                s.setAge(25);
                s.getGuns()[0].setType(Gun.TYPE.AK47);
                s.getGuns()[1].setType(Gun.TYPE.AK47);
                s.getGuns()[2].setType(Gun.TYPE.AK47);
                dao.updateLinks(s, "guns");
                s = dao.fetchLinks(dao.fetch(Soldier.class, "ZZH"), "guns");
                assertEquals(0, s.getAge());
                assertEquals(3, s.getGuns().length);
                for (Gun gun : s.getGuns()) {
                    assertEquals(Gun.TYPE.AK47, gun.getType());
                }
            }
        });
    }

    @Test
    public void update_with() {
        TableName.run(platoon, new Atom() {
            public void run() {
                Soldier s = dao.fetchLinks(dao.fetch(Soldier.class, "ZZH"), "guns");
                s.setAge(25);
                s.getGuns()[0].setType(Gun.TYPE.AK47);
                s.getGuns()[1].setType(Gun.TYPE.AK47);
                s.getGuns()[2].setType(Gun.TYPE.AK47);
                dao.updateWith(s, "guns");
                s = dao.fetchLinks(dao.fetch(Soldier.class, "ZZH"), "guns");
                assertEquals(25, s.getAge());
                assertEquals(3, s.getGuns().length);
                for (Gun gun : s.getGuns()) {
                    assertEquals(Gun.TYPE.AK47, gun.getType());
                }
            }
        });
    }

}
