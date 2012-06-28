package org.nutz.trans;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;

import org.junit.Test;

import org.nutz.dao.DaoException;
import org.nutz.dao.TableName;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Country;
import org.nutz.dao.test.meta.Fighter;
import org.nutz.dao.test.meta.Mission;
import org.nutz.lang.Lang;

public class BatchTransTest extends DaoCase {

    @Override
    protected void after() {}

    @Override
    protected void before() {
        dao.create(Company.class, true);
        dao.create(Master.class, true);
        dao.create(Cat.class, true);
    }

    @Test
    public void try_insert_static_objects_by_many_many() {
        pojos.init();
        final Base b = Base.make("B");
        b.setFighters(new ArrayList<Fighter>());
        TableName.run(1, new Atom() {
            public void run() {
                Trans.exec(new Atom() {
                    public void run() {
                        dao.insert(Country.make("A"));
                        try {
                            dao.insert(Country.make("A"));
                        }
                        catch (DaoException e) {}
                        dao.insert(Country.make("C"));
                        dao.insert(Country.make("D"));
                    }
                });
                assertEquals(3, dao.count(Country.class));
            }
        });

    }

    @Test
    public void try_insert_multiple_dynamic_objects() {
        try {
            pojos.initPlatoon(1);
            TableName.run(1, new Atom() {
                public void run() {
                    Trans.exec(new Atom() {
                        public void run() {
                            dao.insert(Mission.make("D1", "2008-12-21 20:15:26"));
                            try {
                                dao.insert(Mission.make("D1", "2008-9-21 17:12:23"));
                            }
                            catch (DaoException e) {}
                            dao.insert(Mission.make("D2", "2008-10-21 18:13:24"));
                            dao.insert(Mission.make("D3", "2008-11-21 19:14:25"));
                        }
                    });
                    assertEquals(3, dao.count(Mission.class));
                }
            });
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            pojos.dropPlatoon(1);
        }
    }

    @Test
    public void try_insert_multiple_companys() {
        Trans.exec(Connection.TRANSACTION_READ_COMMITTED,new Atom() {
            public void run() {
                dao.insert(Company.make("Google"));
                dao.insert(Company.make("Yahoo"));
                try {
                    dao.insert(Company.make("Yahoo"));
                }
                catch (DaoException e) {}
                dao.insert(Company.make("Microsoft"));
                dao.insert(Company.make("Sun"));
                dao.insert(Company.make("IBM"));
            }
        });
        assertEquals(5, dao.count(Company.class));
    }

}
