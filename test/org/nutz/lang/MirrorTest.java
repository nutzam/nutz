package org.nutz.lang;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.nutz.NutzEnum;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.test.meta.Pet;
import org.nutz.lang.born.Borning;
import org.nutz.lang.meta.Email;
import org.nutz.lang.meta.Issue392Bean;
import org.nutz.lang.reflect.ObjA;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.Callback3;

public class MirrorTest {

    public static class MTGSF {
        public static final int F_AGE = 9;
        public static final String F_NAME = "ABC";

        public static int X;
        public static String Y;
        public static int Z;

        String m_a;

        String m_b;
    }

    @Test
    public void test_getFields() {
        Field[] fs = Mirror.me(MTGSF.class).getFields();
        assertEquals(2, fs.length);
        for (Field f : fs)
            assertTrue(f.getName().startsWith("m_"));

        fs = Mirror.me(MTGSF.class).getStaticField(true);
        assertEquals(3, fs.length);
        for (Field f : fs) {
            assertFalse(f.getName().startsWith("m_"));
            assertFalse(f.getName().startsWith("F_"));
        }

        fs = Mirror.me(MTGSF.class).getStaticField(false);
        assertEquals(5, fs.length);
        for (Field f : fs) {
            assertFalse(f.getName().startsWith("m_"));
        }
    }

    public static class TDMFGADM {
        public String toString() {
            return super.toString();
        }

        public Object get() {
            return "";
        }
    }

    public static class TDMFGADMII extends TDMFGADM {
        public String toString() {
            return super.toString();
        }

        @Override
        public String get() {
            return super.get().toString();
        }

    }

    @Test
    public void test_duplicate_method_for_getAllDeclareMethods() {
        Method[] ms = Mirror.me(TDMFGADMII.class).getAllDeclaredMethodsWithoutTop();
        assertEquals(2, ms.length);
    }

    abstract class A<T> {}

    abstract class B<X, Y> {}

    @Test
    public void testOneParam() {
        A<String> a = new A<String>() {};
        assertEquals(String.class, Mirror.getTypeParams(a.getClass())[0]);

    }

    @Test
    public void test_invoke_map() {
        Map<?, ?> map = Lang.map("{x:10,y:50,txt:'Hello'}");
        Integer v = (Integer) Mirror.me(map).invoke(map, "get", "x");
        assertEquals(10, v.intValue());
    }

    @Test
    public void testTwoParam() {
        B<Integer, String> b = new B<Integer, String>() {};
        assertEquals(Integer.class, Mirror.getTypeParams(b.getClass())[0]);
        assertEquals(String.class, Mirror.getTypeParams(b.getClass())[1]);
    }

    @Test
    public void testWrapper() {
        assertTrue(Mirror.me(Integer.class).isWrapperOf(int.class));
        assertFalse(Mirror.me(Integer.class).isWrapperOf(float.class));
        assertTrue(Mirror.me(Float.class).isWrapperOf(float.class));
    }

    @Test
    public void testCanCastToDirectly() {
        assertTrue(Mirror.me(Integer.class).canCastToDirectly(int.class));
        assertTrue(Mirror.me(int.class).canCastToDirectly(Integer.class));
        assertTrue(Mirror.me(String.class).canCastToDirectly(CharSequence.class));
        assertTrue(Mirror.me(String.class).canCastToDirectly(String.class));
        assertTrue(Mirror.me(Boolean.class).canCastToDirectly(boolean.class));
        assertTrue(Mirror.me(boolean.class).canCastToDirectly(Boolean.class));
        assertTrue(Mirror.me(int.class).canCastToDirectly(short.class));

        assertFalse(Mirror.me(int.class).canCastToDirectly(Short.class));
        assertFalse(Mirror.me(CharSequence.class).canCastToDirectly(String.class));
        assertFalse(Mirror.me(String.class).canCastToDirectly(StringBuilder.class));
        assertFalse(Mirror.me(String.class).canCastToDirectly(StringBuilder.class));
        assertFalse(Mirror.me(boolean.class).canCastToDirectly(float.class));
        assertFalse(Mirror.me(boolean.class).canCastToDirectly(short.class));

        assertTrue(Mirror.me(Character.class).canCastToDirectly(char.class));
        assertTrue(Mirror.me(Character.class).canCastToDirectly(Character.class));
        assertTrue(Mirror.me(char.class).canCastToDirectly(Character.class));
    }

    @Test
    public void testGetWrpperClass() {
        assertEquals(Boolean.class, Mirror.me(Boolean.class).getWrapperClass());
        assertEquals(Boolean.class, Mirror.me(boolean.class).getWrapperClass());
        assertEquals(Integer.class, Mirror.me(Integer.class).getWrapperClass());
        assertEquals(Integer.class, Mirror.me(int.class).getWrapperClass());
        assertEquals(Float.class, Mirror.me(Float.class).getWrapperClass());
        assertEquals(Float.class, Mirror.me(float.class).getWrapperClass());
        assertEquals(Long.class, Mirror.me(Long.class).getWrapperClass());
        assertEquals(Long.class, Mirror.me(long.class).getWrapperClass());
        assertEquals(Double.class, Mirror.me(Double.class).getWrapperClass());
        assertEquals(Double.class, Mirror.me(double.class).getWrapperClass());
        assertEquals(Byte.class, Mirror.me(Byte.class).getWrapperClass());
        assertEquals(Byte.class, Mirror.me(byte.class).getWrapperClass());
        assertEquals(Short.class, Mirror.me(Short.class).getWrapperClass());
        assertEquals(Short.class, Mirror.me(short.class).getWrapperClass());
        assertEquals(Character.class, Mirror.me(Character.class).getWrapperClass());
        assertEquals(Character.class, Mirror.me(char.class).getWrapperClass());
    }

    @Test
    public void testExtractBoolean() {
        assertEquals(Boolean.class, Mirror.me(boolean.class).extractTypes()[0]);
    }

    @Test
    public void testExtractEnum() {
        assertEquals(NutzEnum.class, Mirror.me(NutzEnum.Dao.getClass()).extractTypes()[0]);
        assertEquals(Enum.class, Mirror.me(NutzEnum.Dao.getClass()).extractTypes()[1]);
    }

    @Test
    public void testExtractChar() {
        Class<?>[] types = Mirror.me(char.class).extractTypes();
        assertEquals(2, types.length);
        assertEquals(Character.class, types[0]);
    }

    @Test
    public void testExtractInt() {
        Class<?>[] types = Mirror.me(int.class).extractTypes();
        assertEquals(3, types.length);
        assertEquals(Integer.class, types[0]);
        assertEquals(Number.class, types[1]);
    }

    @Test
    public void testExtractString() {
        Class<?>[] types = Mirror.me(String.class).extractTypes();
        assertEquals(3, types.length);
        assertEquals(String.class, types[0]);
        assertEquals(CharSequence.class, types[1]);
    }

    public static class F {
        @Id
        String id;
    }

    public static class SubF extends F {
        @Name
        String id;
    }

    public static class FF {

        public FF(String myId) {
            fid = myId;
        }

        public FF(F f, String myId) {
            fid = f.id + myId;
        }

        String fid;
    }

    @Test
    public void test_get_fields() {
        Field[] fields = Mirror.me(SubF.class).getFields();
        assertEquals(1, fields.length);
        assertNotNull(fields[0].getAnnotation(Name.class));
    }

    @Test
    public void testBorn_innerClassNested() {
        F f = new F();
        f.id = "haha";
        FF ff = Mirror.me(FF.class).born(f, "!!!");
        assertEquals("haha!!!", ff.fid);
    }

    @Test
    public void testBorn_innerClassDefaultNested() {
        FF ff = Mirror.me(FF.class).born("!!!");
        assertEquals("!!!", ff.fid);
    }

    public static class DS {
        public DS(int id, String... values) {
            this.id = id;
            this.values = values;
        }

        private int id;
        private String[] values;
    }

    @Test
    public void testBornByStaticDynamiceArgs() {
        DS ds = Mirror.me(DS.class).born(23, new String[]{"TT", "FF"});
        assertEquals(23, ds.id);
        assertEquals("FF", ds.values[1]);
    }

    @Test
    public void testBornByStaticNullDynamiceArgs() {
        DS ds = Mirror.me(DS.class).born(23);
        assertEquals(23, ds.id);
        assertEquals(0, ds.values.length);
    }

    public static class DD {
        public DD(int id, String... values) {
            this.id = id;
            this.values = values;
        }

        private int id;
        private String[] values;
    }

    @Test
    public void testBornByInnerDynamiceArgs() {
        DD ds = Mirror.me(DD.class).born(23, new String[]{"TT", "FF"});
        assertEquals(23, ds.id);
        assertEquals("FF", ds.values[1]);
    }

    @Test
    public void testBornByInnerNullDynamiceArgs() {
        DD ds = Mirror.me(DD.class).born(23);
        assertEquals(23, ds.id);
        assertEquals(0, ds.values.length);
    }

    @Test
    public void testBornByInnerOuterDynamiceArgs() {
        DD ds = Mirror.me(DD.class).born(23);
        assertEquals(23, ds.id);
        assertEquals(0, ds.values.length);
    }

    @Test
    public void testBornByParent() {
        NullPointerException e = new NullPointerException();
        RuntimeException e2 = Mirror.me(RuntimeException.class).born(e);
        assertTrue(e2.getCause() == e);
    }

    @Test
    public void testBornByStatic() {
        Calendar c = Mirror.me(Calendar.class).born();
        assertNotNull(c);
        Integer ii = Mirror.me(Integer.class).born(34);
        assertTrue(34 == ii);
    }

    public static class DDD {
        public String[] args;
        public String[] x_args;

        public DDD(String... args) {
            this.args = args;
        }

        public void x(String... args) {
            this.x_args = args;
        }
    }

    @Test
    public void testBornByDynamicArgs() throws Exception {
        DDD d = Mirror.me(DDD.class).born((Object) Lang.array("abc", "bcd"));
        assertEquals(2, d.args.length);
        assertEquals("abc", d.args[0]);
        assertEquals("bcd", d.args[1]);
    }

    @Test
    public void testInvokeByDynamicArgs() throws Exception {
        DDD d = Mirror.me(DDD.class).born((Object) Lang.array("abc", "bcd"));
        Mirror.me(DDD.class).invoke(d, "x", (Object[]) Lang.array("F", "Z"));
        assertEquals(2, d.x_args.length);
        assertEquals("F", d.x_args[0]);
        assertEquals("Z", d.x_args[1]);
    }

    @Test
    public void testBornByDynamicArgsNull() throws Exception {
        DDD d = Mirror.me(DDD.class).born();
        assertEquals(0, d.args.length);
    }

    @Test
    public void testBornByDynamicArgsObjectArray() throws Exception {
        Object[] args = new Object[2];
        args[0] = "A";
        args[1] = "B";
        DDD d = Mirror.me(DDD.class).born(args);
        assertEquals(2, d.args.length);
        assertEquals("A", d.args[0]);
        assertEquals("B", d.args[1]);
        d = Mirror.me(DDD.class).born("A", "B");
        assertEquals(2, d.args.length);
        assertEquals("A", d.args[0]);
        assertEquals("B", d.args[1]);
    }

    @Test
    public void testBornEmail() {
        Email email = Mirror.me(Email.class).born("a@b.com");
        assertEquals("a", email.getAccount());
        assertEquals("b.com", email.getHost());

        email = Mirror.me(Email.class).born();
        email.setAccount("a");
        email.setHost("b.com");
        assertEquals("a", email.getAccount());
        assertEquals("b.com", email.getHost());
    }

    public static int testStaticMethod(long l) {
        return (int) (l * 10);
    }

    @Test
    public void testInvokeStatic() {
        int re = (Integer) Mirror.me(this.getClass()).invoke(null, "testStaticMethod", 45L);
        assertEquals(450, re);
    }

    public boolean testMethod(String s) {
        return Boolean.valueOf(s);
    }

    @Test
    public void testInvoke() {
        boolean re = (Boolean) Mirror.me(this.getClass()).invoke(this, "testMethod", "true");
        assertTrue(re);
    }

    public static int invokeByInt(int abc) {
        return abc * 10;
    }

    @Test
    public void testInvokeByWrapper() {
        int re = (Integer) Mirror.me(this.getClass()).invoke(this, "invokeByInt", 23);
        assertEquals(230, re);
    }

    public static class SV {
        private int id;
        private char cc;

        private Boolean ok;

        private Character cobj;

        private Integer intobj;

    }

    @Test
    public void test_setValue() {
        SV sv = new SV();
        Mirror.me(SV.class).setValue(sv, "id", 200);
        Mirror.me(SV.class).setValue(sv, "cc", 'T');
        assertEquals(200, sv.id);
        assertEquals('T', sv.cc);
        Mirror.me(SV.class).setValue(sv, "id", null);
        Mirror.me(SV.class).setValue(sv, "cc", null);
        assertEquals(0, sv.id);
        assertEquals(0, (int) sv.cc);

        ClassC c = new ClassC();
        Mirror.me(ClassC.class).setValue(c, "id", 1);
        assertEquals(1, c.getId());
    }

    public static class ClassC {

        private int x;

        public void setId(int id) {
            this.x = id;
        }

        public int getId() {
            return x;
        }
    }

    @Test
    public void test_setValue_Boolean_and_Character() {
        SV sv = new SV();
        sv.ok = true;
        sv.cobj = Character.valueOf('F');
        sv.intobj = 30;
        Mirror.me(SV.class).setValue(sv, "ok", null);
        Mirror.me(SV.class).setValue(sv, "cobj", null);
        Mirror.me(SV.class).setValue(sv, "intobj", null);
        assertNull(sv.ok);
        assertNull(sv.cobj);
        assertNull(sv.intobj);
    }

    @Test(expected = RuntimeException.class)
    public void set_null_value_by_invoking() {
        ObjA base = new ObjA("abc");
        Mirror<ObjA> mirror = Mirror.me(ObjA.class);
        mirror.invoke(base, "setName", (Object) null);
        assertNull(base.getName());
        base.setName("FYZ");
        mirror.invoke(base, "setName", (Object[]) null);
    }

    class Abcc {

        Map<String, Mirror<?>> map;

        List<Abcc> list;

        String name;

        Map<?, String> map2;

        List<?> list2;

    }

    @Test
    public void test_getGenericTypes() throws Exception {
        Field f = Abcc.class.getDeclaredField("map");
        Class<?>[] types = Mirror.getGenericTypes(f);
        assertEquals(2, types.length);
        assertEquals(String.class, types[0]);
        assertEquals(Mirror.class, types[1]);

        f = Abcc.class.getDeclaredField("map2");
        types = Mirror.getGenericTypes(f);
        assertEquals(2, types.length);
        assertEquals(Object.class, types[0]);
        assertEquals(String.class, types[1]);

        f = Abcc.class.getDeclaredField("list");
        types = Mirror.getGenericTypes(f);
        assertEquals(1, types.length);
        assertEquals(Abcc.class, types[0]);

        f = Abcc.class.getDeclaredField("list2");
        types = Mirror.getGenericTypes(f);
        assertEquals(1, types.length);
        assertEquals(Object.class, types[0]);

        f = Abcc.class.getDeclaredField("name");
        types = Mirror.getGenericTypes(f);
        assertEquals(0, types.length);
    }

    public static class TBOC {
        DB db;

        public TBOC(DB db) {
            this.db = db;
        }
    }

    @Test
    public void test_borning_of_constractor() {
        Borning<TBOC> b = Mirror.me(TBOC.class).getBorning("H2");
        TBOC tb = b.born(Lang.array("H2"));
        assertEquals(DB.H2, tb.db);
    }

    public static class TBOM {
        DB db;

        public static TBOM create(DB db) {
            TBOM re = new TBOM();
            re.db = db;
            return re;
        }
    }

    @Test
    public void test_borning_of_method() {
        Borning<TBOM> b = Mirror.me(TBOM.class).getBorning("H2");
        TBOM tb = b.born(Lang.array("H2"));
        assertEquals(DB.H2, tb.db);
    }

    @Test
    public void test_packageClass() {
        PClass p = new PClass();
        Mirror<PClass> mirror = Mirror.me(p);
        mirror.setValue(p, "longField", null);
        mirror.setValue(p, "doubleField", null);
        mirror.setValue(p, "floatField", null);
        mirror.setValue(p, "integerField", null);
        mirror.setValue(p, "shortField", null);
        mirror.setValue(p, "byteField", null);
        mirror.setValue(p, "characterField", null);
        mirror.setValue(p, "booleanField", null);
        mirror.setValue(p, "bigDecimal", null);
        mirror.setValue(p, "bigInteger", null);

        mirror.setValue(p, "longField2", null);
        mirror.setValue(p, "doubleField2", null);
        mirror.setValue(p, "floatField2", null);
        mirror.setValue(p, "integerField2", null);
        mirror.setValue(p, "shortField2", null);
        mirror.setValue(p, "byteField2", null);
        mirror.setValue(p, "characterField2", null);
        mirror.setValue(p, "booleanField2", null);
    }

    public static class PClass {
        public Long longField;
        public Double doubleField;
        public Float floatField;
        public Integer integerField;
        public Short shortField;
        public Byte byteField;
        public Character characterField;
        public Boolean booleanField;
        public BigDecimal bigDecimal;
        public BigInteger bigInteger;

        public long longField2;
        public double doubleField2;
        public float floatField2;
        public int integerField2;
        public short shortField2;
        public byte byteField2;
        public char characterField2;
        public boolean booleanField2;
    }

    @Test
    public void test_evalGetterSetter() throws NoSuchMethodException {
        Mirror<Pet> mirror = Mirror.me(Pet.class);
        Mirror.evalGetterSetter(mirror.getGetter("name"), new Callback3<String, Method, Method>() {
            public void invoke(String field, Method getter, Method setter) {
                assertNotNull(getter);
                assertNotNull(setter);
                assertNotNull(field);
            }

        }, new Callback<Method>() {
            public void invoke(Method obj) {}
        });
    }
    
    @Test
    public void testIssue309(){
        assertEquals("jk", Mirror.me(String.class).invoke(String.class, "valueOf", "jk"));
    }
    
    @Test
    public void testIssue392() {
    	assertEquals(0, Mirror.me(Issue392Bean.class).born(new byte[]{}).getLen());
    	assertEquals(6, Mirror.me(Issue392Bean.class).born(new byte[]{1,2,3,4,5,6}).getLen());
    }
}
