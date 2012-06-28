package org.nutz.resource;

import java.io.IOException;

public class JarEntryInfo {

    public String getEntryName() {
        return entryName;
    }

    public String getJarPath() {
        return jarPath;
    }

    public JarEntryInfo setEntryName(String entryName) {
        this.entryName = entryName;
        return this;
    }

    public JarEntryInfo setJarPath(String jarPath) {
        this.jarPath = jarPath;
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s!/%s", jarPath, entryName);
    }

    private String entryName;

    private String jarPath;

    public JarEntryInfo() {}

    /**
     * 从一个包含 jar 的全路径里，截取 jar 文件的路径，以及 Entry的path。
     * <p>
     * Mac / Linux / Windows jar 文件的路径的分隔符号略有不同
     * <ul>
     * <li><b>Windows</b> - file:\D:\a\b\c\xyz.jar!\m\n\T.class
     * </ul>
     * 
     * @param path
     *            文件全路径
     * 
     * @throws IOException
     */
    public JarEntryInfo(String path) {
        path = path.replace('\\', '/');
        int posL = path.indexOf("file:");
        posL = posL < 0 ? 0 : posL + "file:".length();
        int posR = path.indexOf(".jar!") + ".jar!".length();
        this.jarPath = path.substring(posL, posR - 1);
        this.entryName = path.substring(posR + 1);
    }

    public JarEntryInfo(String jarPath, String entryName) {
        this.jarPath = jarPath;
        this.entryName = entryName;
    }

}
