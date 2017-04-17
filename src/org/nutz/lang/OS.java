package org.nutz.lang;

import java.awt.GraphicsEnvironment;

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

    static {
        refreshFonts();
    }

    // ------------------------- 系统字体
    private static String[] _fonts;

    private static void refreshFonts() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        _fonts = environment.getAvailableFontFamilyNames(); // 获得系统字体
    }

    /**
     * 返回字体列表
     * 
     * @return 字体列表
     */
    public static String[] fonts() {
        return _fonts.clone();
    }

    /**
     * 重新加载，返回字体列表
     * 
     * @return 字体列表
     */
    public static String[] fontsRefresh() {
        refreshFonts();
        return fonts();
    }

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

}
