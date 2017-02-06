package org.nutz.lang;

/**
 * 判断当前系统的类型
 * 
 * @author pw
 */
public abstract class OS {

    private static String sysProp(String property) {
        try {
            return System.getProperty(property);
        }
        catch (SecurityException ex) {
            System.err.println("SecurityException on try find system property '" + property + "'");
            return "";
        }
    }

    public static final String OS_NAME = sysProp("os.name").toLowerCase();
    public static final String JAVA_SPECIFICATION_VERSION = sysProp("java.specification.version");
    public static final String JAVA_VERSION = sysProp("java.version");

    // ------------------------- 系统判断

    public static boolean isLinux() {
        return OS_NAME.indexOf("linux") >= 0;
    }

    public static boolean isMacOS() {
        return OS_NAME.indexOf("mac") >= 0;
    }

    public static boolean isWindows() {
        return OS_NAME.indexOf("windows") >= 0;
    }

    public static boolean isAndroid() {
        try {
            Class.forName("android.Manifest");
            return true;
        }
        catch (Throwable e) {}
        return false;
    }

    // ------------------------- java版本

    private static boolean isJavaVersion(String versionPrefix) {
        return JAVA_SPECIFICATION_VERSION.startsWith(versionPrefix);
    }

    public static boolean isJava1_1 = isJavaVersion("1.1");
    public static boolean isJava1_2 = isJavaVersion("1.2");
    public static boolean isJava1_3 = isJavaVersion("1.3");
    public static boolean isJava1_4 = isJavaVersion("1.4");
    public static boolean isJava1_5 = isJavaVersion("1.5");
    public static boolean isJava1_6 = isJavaVersion("1.6");
    public static boolean isJava1_7 = isJavaVersion("1.7");
    public static boolean isJava1_8 = isJavaVersion("1.8");
    public static boolean isJava1_9 = isJavaVersion("9");

    public static void main(String[] args) {
        System.out.println("os.name: " + OS.OS_NAME);
        System.out.println("java.specification.version: " + OS.JAVA_SPECIFICATION_VERSION);
        System.out.println("java.version: " + OS.JAVA_VERSION);

        System.out.println("Linux: " + OS.isLinux());
        System.out.println("MacOS: " + OS.isMacOS());
        System.out.println("Windows: " + OS.isWindows());
        System.out.println("Android: " + OS.isAndroid());

        System.out.println("java1.1: " + OS.isJava1_1);
        System.out.println("java1.2: " + OS.isJava1_2);
        System.out.println("java1.3: " + OS.isJava1_3);
        System.out.println("java1.4: " + OS.isJava1_4);
        System.out.println("java1.5: " + OS.isJava1_5);
        System.out.println("java1.6: " + OS.isJava1_6);
        System.out.println("java1.7: " + OS.isJava1_7);
        System.out.println("java1.8: " + OS.isJava1_8);
        System.out.println("java1.9: " + OS.isJava1_9);
    }
}
