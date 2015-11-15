package org.nutz.lang.reflect;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.DaoSupport;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.meta.Pet;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.born.Borning;

public class FastClassFactoryTest extends Assert {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testInvokeObjectMethodObjectArray() throws InvocationTargetException {
        DefaultClassDefiner.DEBUG_DIR = "/nutz_fastclass/";
        FastClass fc = FastClassFactory.get(Pet.class);
        //net.sf.cglib.reflect.FastClass fc2 = net.sf.cglib.reflect.FastClass.create(Pet.class);
        Mirror<Pet> mirror = Mirror.me(Pet.class);
        Borning<Pet> mb = mirror.getBorning();
        for (int i = 0; i < 10000; i++) {
            fc.born();
        }
        for (int i = 0; i < 10000; i++) {
            new Pet();
        }
        for (int i = 0; i < 10000; i++) {
            mb.born();
        }
//        for (int i = 0; i < 10000; i++) {
//            fc2.newInstance();
//        }
        System.gc();
        Lang.quiteSleep(1000);
        System.gc();
        
        Pet pet = null;
        Stopwatch sw = Stopwatch.begin();
        for (int i = 0; i < 1000000; i++) {
            pet = new Pet();
        }
        
        sw.stop();
        System.out.println("Native New    :"+sw);
        System.gc();
        Lang.quiteSleep(1000);
        System.gc();
        

        sw = Stopwatch.begin();
        for (int i = 0; i < 1000000; i++) {
            pet = (Pet) fc.born();
        }

        sw.stop();
        System.out.println("FastClass born:"+sw);
        System.gc();
        Lang.quiteSleep(1000);
        System.gc();
        

        sw = Stopwatch.begin();
        for (int i = 0; i < 1000000; i++) {
            pet = mb.born();
        }

        sw.stop();
        System.out.println("mirror born   :"+sw);
        System.gc();
        Lang.quiteSleep(1000);
        System.gc();
        
        sw = Stopwatch.begin();
        for (int i = 0; i < 1000000; i++) {
            new Object();
        }

        sw.stop();
        System.out.println("NULL          :"+sw);
        System.gc();
        Lang.quiteSleep(1000);
        System.gc();
        

//        sw = Stopwatch.begin();
//        for (int i = 0; i < 1000000; i++) {
//            pet = (Pet) fc2.newInstance(new Class[]{String.class}, new Object[]{"wendal"});
//        }
//        
//        sw.stop();
//        System.out.println("cglib born   :"+sw);
//        Lang.quiteSleep(1000);
//        System.gc();
        
        System.out.println(pet.hashCode());
    }
    
    @Test
    public void test_fastclass_for_datasource() {
        FastClassFactory.get(Dao.class);
        FastClassFactory.get(BasicDataSource.class);
        FastClass fc = FastClassFactory.get(DaoSupport.class);
        fc = FastClassFactory.get(NutDao.class);
        fc.invoke(new NutDao(), "execute", new Class[]{Sql.class}, new Object[]{null});
    }

    public static void main(String[] args) throws Exception {
//       ASMifier.main(new String[]{"target/classes/org/nutz/lang/reflect/SimpleFastClass.class"});
    }
}
