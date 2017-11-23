package org.nutz.json;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutType;

/**
 * @author kerbores@gmail.com
 *
 */
public class GenericTypeTest {

    public static class A<T> {

        T data;

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

    }

    public static class B {
        int id;
        String name;
        Date birth;
        public B() {
            // TODO Auto-generated constructor stub
        }
        /**
         * @param id
         * @param name
         * @param birth
         */
        public B(int id, String name, Date birth) {
            super();
            this.id = id;
            this.name = name;
            this.birth = birth;
        }
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
        public Date getBirth() {
            return birth;
        }
        public void setBirth(Date birth) {
            this.birth = birth;
        }
        
        
    }

    @Test
    public void test() {
        A a = new A();
        a.setData(new B(1, "test", Times.now()));
        String s = Json.toJson(a);
        A<B> a1 = (A<B>) Json.fromJson(new NutType(A.class, B.class), s);
        assertEquals(B.class, a1.getData().getClass());
    }

}
