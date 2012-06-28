package org.nutz.trans;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.dao.test.DaoCase;
import org.nutz.lang.Lang;
import org.nutz.service.IdEntityService;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

public class TransactionTest extends DaoCase {

    private IdEntityService<Cat> catService;
    private IdEntityService<Company> comService;
    private IdEntityService<Master> masterService;

    protected void before() {
        dao.create(Company.class, true);
        dao.create(Master.class, true);
        dao.create(Cat.class, true);
        catService = new IdEntityService<Cat>(dao) {};
        comService = new IdEntityService<Company>(dao) {};
        masterService = new IdEntityService<Master>(dao) {};
    }

    protected void after() {}

    private Cat insert(final Cat cat, final Bomb bomb) {
        // //System.out.printf("\n>> insert cat: %d\n",
        // bomb.times);
        Trans.exec(new Atom() {
            public void run() {
                dao.insert(cat);
                cat.setMaster(insert(cat.getMaster(), bomb));
                bomb.bong();
            }
        });
        // //System.out.printf("<< insert cat: %d\n",
        // bomb.times);
        return cat;
    }

    private Company insert(final Company com, final Bomb bomb) {
        // //System.out.printf("\n>> insert company: %d\n",
        // bomb.times);
        Trans.exec(new Atom() {
            public void run() {
                dao.insert(com);
                bomb.bong();
            }
        });
        // //System.out.printf("<< insert company: %d\n",
        // bomb.times);
        return com;
    }

    private Master insert(final Master master, final Bomb bomb) {
        // //System.out.printf("\n>> insert master: %d\n",
        // bomb.times);
        Trans.exec(new Atom() {
            public void run() {
                dao.insert(master);
                master.setCom(insert(master.getCom(), bomb));
                bomb.bong();
            }
        });
        // //System.out.printf("<< insert master: %d\n",
        // bomb.times);
        return master;
    }

    private static class Bomb {

        private int times;

        Bomb(int times) {
            this.times = times;
        }

        Bomb bong() throws RuntimeException {
            if (--times == 0) {
                // //System.out.printf("Bong!!! bomb-- %d => %d\n",
                // times + 1,
                // times);
                throw new RuntimeException("Bong!!!");
            }
            // //System.out.printf("bomb-- %d => %d\n",
            // times + 1, times);
            return this;
        }
    }

    @Test
    public void testNestedCommit() {
        Cat cat = Cat.create("XiaoBai", Master.create("zzh", Company.create("Dtri")));
        insert(cat, new Bomb(-1));
        assertEquals(1, dao.count(Cat.class));
        assertEquals(1, dao.count(Master.class));
        assertEquals(1, dao.count(Company.class));
        assertNull(Trans.get());
        assertEquals(0, Trans.count.get().intValue());
    }

    @Test
    public void testNestedRollback() {
        Cat cat = Cat.create("XiaoBai", Master.create("zzh", Company.create("Dtri")));
        try {
            insert(cat, new Bomb(2));
            fail();
        }
        catch (Exception e) {
            assertEquals(0, dao.count(Company.class));
            assertEquals(0, dao.count(Master.class));
            assertEquals(0, dao.count(Cat.class));
            assertNull(Trans.get());
        }
    }

    class CheckCheck extends Thread {

        private Object lock = new Object();
        private Object tellor;
        private boolean standby;

        public boolean isStandby() {
            return standby;
        }

        public void run() {
            // System.out.println("\nI am checker");
            try {
                synchronized (lock) {
                    standby = true;
                    // System.out.println("\nchecker: I am standby and wait for another thread...");
                    lock.wait();
                    // System.out.println("\nchecker: I will do some check");
                }
                if (0 != dao.count(Company.class, null)
                    || 0 != dao.count(Master.class, null)
                    || 0 != dao.count(Cat.class, null)) {
                    throw new RuntimeException("Find change in another thread");
                }
            }
            catch (InterruptedException e) {
                throw Lang.wrapThrow(e);
            }
            finally {
                synchronized (tellor) {
                    // System.out.println("\nchecker: I will notify tellor!");
                    tellor.notifyAll();
                    // System.out.println("\nchecker: I done for notify tellor");
                }
            }
        }
    }

    class AnotherThread extends Thread {
        private Object checker;
        private Object tellor;
        public Object waiter;

        public void run() {
            Trans.exec(new Atom() {
                public void run() {
                    // System.out.println("\nI am another");
                    Company com = Company.create("dtri");
                    Master m = Master.create("zzh", com);
                    Cat c1 = Cat.create("XiaoBai", m);
                    Cat c2 = Cat.create("Tony", m);

                    comService.dao().insert(com);
                    assertEquals(1, dao.count(Company.class, null));
                    m.setComId(com.getId());
                    masterService.dao().insert(m);
                    assertEquals(1, dao.count(Master.class, null));
                    c1.setMasterId(m.getId());
                    c2.setMasterId(m.getId());
                    catService.dao().insert(c1);
                    catService.dao().insert(c2);
                    assertEquals(2, dao.count(Cat.class, null));
                    synchronized (checker) {
                        // System.out.println("\nanother: finished to myjob and I will tell checker...");
                        checker.notifyAll();
                        // System.out.println("\nanother: I done for notify checker");
                    }
                    try {
                        synchronized (tellor) {
                            // System.out.println("\nanother: I will wait 100 ms");
                            tellor.wait(100);
                            // System.out.println("\nanother: I am wake up");
                        }
                    }
                    catch (InterruptedException e) {
                        throw Lang.wrapThrow(e);
                    }
                }
            });
            // System.out.println("\nanother: I will tell waiter");
            synchronized (waiter) {
                waiter.notifyAll();
            }
            // System.out.println("\nanother: done for tell waiter");
        }

    }

    @Test
    public void testRollback() {
        // In transaction
        try {
            Trans.exec(new Atom() {
                public void run() {
                    Company com = Company.create("dtri");
                    Master m = Master.create("zzh", com);
                    Cat c1 = Cat.create("XiaoBai", m);
                    Cat c2 = Cat.create("Tony", m);

                    comService.dao().insert(com);
                    assertEquals(1, dao.count(Company.class, null));
                    m.setComId(com.getId());
                    masterService.dao().insert(m);
                    assertEquals(1, dao.count(Master.class, null));
                    c1.setMasterId(m.getId());
                    c2.setMasterId(m.getId());
                    catService.dao().insert(c1);
                    catService.dao().insert(c2);
                    assertEquals(2, dao.count(Cat.class, null));
                    throw new RuntimeException("Stop!!!");
                }
            });
            fail();
        }
        catch (Exception e) {
            assertEquals(0, dao.count(Company.class, null));
            assertEquals(0, dao.count(Master.class, null));
            assertEquals(0, dao.count(Cat.class, null));
            assertEquals(0, Trans.count.get().intValue());
            assertNull(Trans.get());
        }
    }
}
