package org.nutz.lang.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;

/**
 * 可支持直接书写多行文本的 Properties 文件
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MultiLineProperties implements Map<String, String> {
    


    public MultiLineProperties(Reader reader) throws IOException {
        this();
        load(reader);
    }

    public MultiLineProperties() {
        maps = new ConcurrentHashMap<String, String>();
    }

    protected Map<String, String> maps;

    /**
     * <b>载入并销毁之前的记录</b>
     * 
     * @param reader
     * @throws IOException
     */
    public synchronized void load(Reader reader) throws IOException {
        load(reader, false);
    }

    public synchronized void load(Reader reader, boolean clear) throws IOException {
        if (clear)
            this.clear();
        BufferedReader tr = null;
        if (reader instanceof BufferedReader)
            tr = (BufferedReader) reader;
        else
            tr = new BufferedReader(reader);
        String s;
        while (null != (s = tr.readLine())) {
            if (Strings.isBlank(s))
                continue;
            if (s.length() > 0 && s.trim().charAt(0) == '#') // 只要第一个非空白字符是#,就认为是注释
                continue;
            int pos;
            char c = '0';
            for (pos = 0; pos < s.length(); pos++) {
                c = s.charAt(pos);
                if (c == '=' || c == ':')
                    break;
            }
            if (c == '=') {
                String name = s.substring(0, pos);
                String value = s.substring(pos + 1);
                if (value.endsWith("\\") && !value.endsWith("\\\\")) {
                    StringBuilder sb = new StringBuilder(value.substring(0, value.length() - 1));
                    while (null != (s = tr.readLine())) {
                        if (Strings.isBlank(s))
                            break;
                        if (s.endsWith("\\") && !s.endsWith("\\\\")) {
                            sb.append(s.substring(0, s.length() - 1));
                        } else {
                            sb.append(s);
                            break;
                        }
                    }
                    value = sb.toString();
                }
                // 对value里面的\\uXXXX进行转义?
                if (value.contains("\\u")) {
                    value = Strings.unicodeDecode(value);
                }
                value = value.replace("\\:", ":").replace("\\=", "=");
                maps.put(Strings.trim(name), value);
            } else if (c == ':') {
                String name = s.substring(0, pos);
                StringBuffer sb = new StringBuffer();
                sb.append(s.substring(pos + 1));
                String ss;
                while (null != (ss = tr.readLine())) {
                    if (ss.length() > 0 && ss.charAt(0) == '#')
                        break;
                    sb.append("\r\n" + ss);
                }
                maps.put(Strings.trim(name), sb.toString());
                if (null == ss)
                    return;
            } else {
                maps.put(Strings.trim(s), "");
            }
        }
    }

    public void clear() {
        maps.clear();
    }

    public boolean containsKey(Object key) {
        return maps.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return maps.containsValue(value);
    }

    public Set<Entry<String, String>> entrySet() {
        return maps.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return maps.equals(o);
    }

    @Override
    public int hashCode() {
        return maps.hashCode();
    }

    public boolean isEmpty() {
        return maps.isEmpty();
    }

    public Set<String> keySet() {
        return maps.keySet();
    }

    public List<String> keys() {
        return new ArrayList<String>(maps.keySet());
    }

    public synchronized String put(String key, String value) {
        return maps.put(key, value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void putAll(Map t) {
        maps.putAll(t);
    }

    public String remove(Object key) {
        return maps.remove(key);
    }

    public int size() {
        return maps.size();
    }

    public Collection<String> values() {
        return maps.values();
    }

    public String get(Object key) {
        return maps.get(key);
    }

    public void print(OutputStream out) throws IOException {
        print(new OutputStreamWriter(out, Encoding.CHARSET_UTF8));
    }

    public void print(Writer writer) throws IOException {
        String NL = System.getProperty("line.separator");
        for (Map.Entry<String, String> en : entrySet()) {
            writer.write(en.getKey());
            String val = en.getValue();
            if (val == null) {
                writer.write("=");
                continue;
            }
            if (val.contains("\n")) {
                writer.write(":=");
                writer.write(val);
                writer.write(NL);
                writer.write("#End " + en.getKey());
            } else {
                writer.write('=');
                writer.write(val);
            }
            writer.write(NL);
        }
        writer.flush();
    }

}
