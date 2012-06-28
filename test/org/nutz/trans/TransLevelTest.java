package org.nutz.trans;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.Test;
import org.nutz.Nutzs;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.test.DaoCase;
import org.nutz.service.IdEntityService;

/**
 * @author amosleaf(amosleaf@gmail.com)
 */
public class TransLevelTest extends DaoCase {

    private static IdEntityService<Company> comService;

    static class QueryCompany_ReadCommitted extends DaoCase implements Callable<String> {

        private int id;

        QueryCompany_ReadCommitted(int id) {
            this.id = id;
        }

        public String call() throws Exception {
            ResultAtom<String> ra = null;
            Trans.exec(Connection.TRANSACTION_READ_COMMITTED, ra = new ResultAtom<String>() {
                public void run() {
                    setResult(comService.dao().fetch(Company.class, id).getName());
                }
            });
            return ra.getResult();
        }
    }

    /**
     * Add flag " finished ", we should not wait the thread by fix time, This
     * flat help to make each test function more faster.
     * 
     * @author zozoh(zozohtnt@gmail.com)
     * 
     */
    static class RepeatableRead extends DaoCase implements Runnable {

        RepeatableRead(int id) {
            this.id = id;
        }

        private int id;
        boolean finished;

        public void run() {
            try {
                Trans.exec(Connection.TRANSACTION_READ_COMMITTED, new Atom() {
                    public void run() {
                        Company c = new Company();
                        c.setId(id);
                        c.setName("update");
                        comService.dao().update(c);
                    }
                });
            }
            catch (Exception e) {}
            finally {
                finished = true;
            }
        }
    }

    static abstract class ResultAtom<T> implements Atom {

        private T result;

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }
    }

    @Override
    protected void before() {
        dao.create(Company.class, true);
        comService = new IdEntityService<Company>(dao) {};
        Company c = Company.create("com1");
        comService.dao().insert(c);
        c = Company.create("com2");
        comService.dao().insert(c);
        c = Company.create("com3");
        comService.dao().insert(c);
    }

    @Override
    protected void after() {
        super.after();
    }

    private static Company duplicate(Company old) {
        Company c = new Company();
        c.setId(old.getId());
        c.setName(old.getName());
        return c;
    }

    @Test
    public void testTransLevel() {
        final int[] ls = new int[5];
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                ls[0] = conn.getTransactionIsolation();
            }
        });
        Trans.exec(Connection.TRANSACTION_SERIALIZABLE, new Atom() {
            public void run() {
                dao.run(new ConnCallback() {
                    public void invoke(Connection conn) throws Exception {
                        ls[1] = conn.getTransactionIsolation();
                    }
                });
            }
        });
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                ls[2] = conn.getTransactionIsolation();
            }
        });
        Trans.exec(Connection.TRANSACTION_READ_COMMITTED, new Atom() {
            public void run() {
                dao.run(new ConnCallback() {
                    public void invoke(Connection conn) throws Exception {
                        ls[3] = conn.getTransactionIsolation();
                    }
                });
            }
        });
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                ls[4] = conn.getTransactionIsolation();
            }
        });
        assertEquals(Connection.TRANSACTION_SERIALIZABLE, ls[1]);
        assertEquals(ls[0], ls[2]);
        assertEquals(Connection.TRANSACTION_READ_COMMITTED, ls[3]);
        assertEquals(ls[0], ls[4]);
    }

    @Test
    public void testReadCommitted() {
        // SqlServer/hsql 在这个测试中，两个线程会相互等待 ...
        if (dao.meta().isSqlServer() || dao.meta().isHsql()) {
            Nutzs.notSupport(dao.meta());
        }
        // H2 会在抛异常：Timeout trying to lock table "TRANS_COMPANY";
        else if (dao.meta().isH2()) {
            Nutzs.notSupport(dao.meta());
        } else {
            final ExecutorService es = Executors.newCachedThreadPool();
            final Company c = dao.fetch(Company.class, dao.getMaxId(Company.class));
            Trans.exec(Connection.TRANSACTION_READ_COMMITTED, new Atom() {
                public void run() {
                    Company c1 = duplicate(c);
                    c1.setName("update");
                    comService.dao().update(c1);
                    try {
                        String theName = es.submit(new QueryCompany_ReadCommitted(c.getId())).get();
                        assertEquals(c.getName(), theName);
                    }
                    catch (Exception e) {
                        Assert.assertTrue(false);
                    }
                    c1.setName(c.getName());
                    comService.dao().update(c1);
                }
            });
            es.shutdown();
            assertEquals(c.getName(), comService.fetch(c.getId()).getName());
        }
    }

    // cancel the this @Test, in OpenJDK, it will go into an endless looping
    public void testRepeatableRead() {
        // SqlServer 在这个测试中，两个线程会相互等待 ...
        if (dao.meta().isSqlServer()) {
            Nutzs.notSupport(dao.meta());
        } else {
            final ExecutorService es = Executors.newCachedThreadPool();
            final Company c = dao.fetch(Company.class, dao.getMaxId(Company.class));
            Trans.exec(Connection.TRANSACTION_READ_COMMITTED, new Atom() {
                public void run() {
                    assertEquals(c.getName(), comService.fetch(c.getId()).getName());
                    RepeatableRead task = new RepeatableRead(c.getId());
                    es.submit(task);
                    // spinning to wait the task thread finish its job
                    while (!task.finished) {}
                    // Then test the result
                    assertEquals("update", comService.fetch(c.getId()).getName());
                }
            });
            es.shutdown();
        }
    }

    @Test
    public void testSerializable() {
        final Company c = dao.fetch(Company.class, dao.getMaxId(Company.class));
        Trans.exec(Connection.TRANSACTION_SERIALIZABLE, new Atom() {
            public void run() {
                Company c1 = duplicate(c);
                c1.setName("xyz");
                dao.update(c1);
            }
        });
        Company c2 = dao.fetch(Company.class, c.getId());
        assertEquals("xyz", c2.getName());
    }

    // cancel the this @Test, in OpenJDK, it will go into an endless looping
    public void test_serializable_in_2_thread() {
        // MySql 会导致两个线程互相锁。估计是 InnoDB 只是到表级锁的原因
        // 所以，这个测试不测试 MySql
        if (dao.meta().isMySql()) {
            Nutzs.notSupport(dao.meta());
        }
        // H2 不支持这个事务级别
        else if (dao.meta().isH2()) {
            Nutzs.notSupport(dao.meta());
        }
        // Oracle, 会导致 java.sql.SQLException: ORA-08177: 无法连续访问此事务处理
        else if (dao.meta().isOracle()) {
            Nutzs.notSupport(dao.meta());
        }
        // SqlServer 在这个测试中，两个线程会相互等待 ...
        else if (dao.meta().isSqlServer()) {
            Nutzs.notSupport(dao.meta());
        }
        // 在 Postgresql 下，工作良好
        else {
            final ExecutorService es = Executors.newCachedThreadPool();
            final Company c = dao.fetch(Company.class, dao.getMaxId(Company.class));
            Trans.exec(Connection.TRANSACTION_SERIALIZABLE, new Atom() {
                public void run() {
                    assertEquals(c.getName(), comService.fetch(c.getId()).getName());
                    RepeatableRead task = new RepeatableRead(c.getId());
                    es.submit(task);
                    // spinning to wait the task thread finish its job
                    while (!task.finished) {}
                    // Then test the result, The data didn't change
                    assertEquals(c.getName(), comService.fetch(c.getId()).getName());
                }
            });
            // Output of the trans, fetch again, the data changed
            Company c2 = dao.fetch(Company.class, c.getId());
            assertEquals("update", c2.getName());
        }
    }
}
