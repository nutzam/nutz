package org.nutz.aop.asm.test;

import java.io.IOException;

import javax.swing.JFrame;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.log.Log;

public class Aop1 {

    private String name;

    public Aop1(String name) {
        this.name = name;
    }

    Aop1() {}

    protected Aop1(Integer uu) {}

    /**
     * 无参数,无返回
     */
    public void nonArgsVoid() throws Throwable {
        System.out.println("My - " + name + " >> nonArgsVoid");
    }

    protected void voidZ() {}

    /**
     * 有一个参数,无返回
     */
    public void argsVoid(String x) {
        System.out.println("My - " + name + " >> argsVoid");
    }

    /**
     * 有多个参数,无返回
     */
    public void mixObjectsVoid(String x, Object obj, Integer i, JFrame f) {
        System.out.println("My - " + name + " >> mixObjectsVoid");
    }

    /**
     * 有多个参数,无返回
     */
    public void mixArgsVoid(String x, Object obj, int yy, char xp, long... z) {
        System.out.println("My - "
                            + name
                            + " >> mixArgsVoid"
                            + " 我的参数"
                            + Castors.me().castToString(new Object[]{x, obj, yy, xp, z}));
//        new Throwable().printStackTrace();
    }

    /**
     * 有多个参数,无返回
     */
    public void mixArgsVoid2(    String x,
                                Object obj,
                                int yy,
                                char xp,
                                long bb,
                                boolean ser,
                                char xzzz,
                                String ppp,
                                StringBuffer sb,
                                Log log,
                                long... z) throws Throwable {
        System.out.println("My - " + name + " >> mixArgsVoid2");
    }

    public void x() {
        throw Lang.noImplement();
    }

    // public Object mixArgsVoid3(String x) {
    // return mixArgsVoid4(x);
    // }
    //    
    public Object mixArgsVoid4(String x) throws Throwable {
        return x;
    }

    public String returnString() throws Throwable {
        return "Wendal-X";
    }

    public long returnLong() {
        return 1L;
    }

    public int returnInt() {
        return 1908;
    }

    public short returnShort() {
        return (short) 34;
    }

    public char returnChar() {
        return 'w';
    }

    public byte returnByte() {
        return (byte) 90;
    }

    public boolean returnBoolean() {
        return false;
    }

    public float returnFloat() {
        return 1.1f;
    }

    public double returnDouble() {
        return 1.1D;
    }

    public Log getLog(StringBuilder builder) {
        return null;
    }

    public void throwError() throws Throwable {
        throw new Error("我是被主动抛出的异常!!!");
    }

    public void throwException() throws Throwable {
        throw new Exception("我是被主动抛出的异常!!!");
    }

    public Object[] returnObjectArray() {
        return null;
    }

    public long[] returnLongArray() {
        return null;
    }

    public int[] returnIntArray() {
        return null;
    }

    public short[] returnShortArray() {
        return null;
    }

    public char[] returnCharArray() {
        return null;
    }

    public byte[] returnByteArray() {
        return null;
    }

    public boolean[] returnBooleanArray() {
        return null;
    }

    public float[] returnFloatArray() {
        return null;
    }

    public double[] returnDoubleArray() {
        return null;
    }

    public Aop1(Object x, Object y) throws IOException {

    }

    public Runnable getRunnable() {
        return new Thread();
    }

    public Enum<?> getEnum() {
        return null;
    }

    public void getXXX(Enum<?> enumZ) {

    }
}
