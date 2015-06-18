package org.nutz.lang.reflect;

import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.test.meta.Pet;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Stopwatch;

public class FastClassFactoryTest extends Assert {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testInvokeObjectMethodObjectArray() throws InvocationTargetException {
        FastClass fc = FastClassFactory.get(Pet.class);
        //net.sf.cglib.reflect.FastClass fc2 = net.sf.cglib.reflect.FastClass.create(Pet.class);
        Mirror<Pet> mirror = Mirror.me(Pet.class);
        for (int i = 0; i < 10000; i++) {
            fc.born();
        }
        for (int i = 0; i < 10000; i++) {
            new Pet();
        }
        for (int i = 0; i < 10000; i++) {
            mirror.born();
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
            pet = mirror.born();
        }

        sw.stop();
        System.out.println("mirror born   :"+sw);
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
        
        System.out.println(pet);
    }

}
