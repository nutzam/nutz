package org.nutz.mvc.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Strings;

public class FieldMeta {

    public FieldMeta(String s) {
        map = new HashMap<String, String>();
        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean needPairEnd = false;
        for (Character c : s.toCharArray()) {
            if (needPairEnd) {
                if (c == '"') {
                    needPairEnd = false;
                    continue;
                } else {
                    sb.append(c);
                    continue;
                }
            }
            if (c == '"') {
                needPairEnd = true;
                continue;
            }
            if (c ==  ';' || c == '\n') {
                if (sb.length() > 0) {
                    list.add(sb.toString().trim());
                }
                sb.setLength(0);
                continue;
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString().trim());
        }
        for (String pair : list) {
            if (pair.isEmpty())
                continue;
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
