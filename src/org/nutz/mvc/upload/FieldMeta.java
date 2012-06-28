package org.nutz.mvc.upload;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Strings;

public class FieldMeta {

    FieldMeta(String s) {
        map = new HashMap<String, String>();
        String[] ss = Strings.splitIgnoreBlank(s, "[\n;]");
        for (String pair : ss) {
            String name = pair.split("[:=]")[0];
            String value = pair.replaceAll("^[^=:]*[=:]", "");
            map.put(Strings.trim(name), formatValue(value));
        }
    }

    private static String formatValue(String s) {
        s = Strings.trim(s);
        if (null != s && s.length() > 2 && s.charAt(0) == '"')
            return s.substring(1, s.length() - 1);
        if ("\"\"".equals(s))
            return "";
        return s;
    }

    Map<String, String> map;

    public String getContentType() {
        return map.get("Content-Type");
    }

    public String getName() {
        return map.get("name");
    }

    public String getFileLocalPath() {
        return map.get("filename");
    }

    public String getFileLocalName() {
        return (new File(getFileLocalPath().replace('\\', '/'))).getName();
    }

    public String getFileExtension() {
        String name = getFileLocalPath();
        int pos = name.lastIndexOf('.');
        if (pos >= 0)
            return name.substring(pos);
        return "";
    }

    public String getContentDisposition() {
        return map.get("Content-Disposition");
    }

    public boolean isFile() {
        return null != getFileLocalPath();
    }
}
