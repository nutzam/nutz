package org.nutz.lang.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

/**
 * 解析命令参数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class CmdParams {

    private static final Pattern PARAM_KEY = Pattern.compile("^-([a-zA-Z_].*)$");

    public String[] vals;

    NutMap map;

    /**
     * @see #parse(String[], String, String)
     */
    public static CmdParams parse(String[] args, String bools) {
        if (null == bools)
            return parse(args, null, null);

        if (bools.startsWith("^"))
            return parse(args, null, bools);

        return parse(args, bools, null);
    }

    /**
     * 解析传入的参数表
     * 
     * <pre>
     * 如果参数以 "-" 开头，则所谓名值对的键。
     * 如果后面接着一个 "-" 开头的参数，则认为当前项目是布尔
     * 当然，如果给入的参数 boolChars 或者 boolRegex 匹配上了这个参数，也认为是布尔
     * </pre>
     * 
     * @param args
     *            参数表
     * 
     * @param boolChars
     *            指明一个键的哪个字符是布尔值。 一个键如果全部内容都是布尔值，则分别记录。否则认为是一个普通键 <br>
     *            你可以直接给一个正则表达式来匹配 boolChar，但是你的正则表达式必须得有 group(1) 表示内容
     * 
     * @param boolRegex
     *            用一个正则表达式来描述哪些键（参数的整体）为布尔值
     * 
     * @return 参数表
     */
    public static CmdParams parse(String[] args, String boolChars, String boolRegex) {
        CmdParams params = new CmdParams();
        List<String> list = new ArrayList<String>(args.length);
        params.map = new NutMap();
        if (args.length > 0) {

            // 预编译 boolRegex
            Pattern bool_key = null;
            if (!Strings.isBlank(boolRegex)) {
                bool_key = Pattern.compile(boolRegex);
            }

            // 预编译 boolChars，如果匹配这个正则表达式的参数，将被认为是一个布尔参数
            // 支持 -bish 这样的组合形式
            Pattern bool_char = null;
            if (!Strings.isBlank(boolChars)) {
                bool_char = Pattern.compile("^-([" + boolChars + "]+)$");
            }

            // 参数表 ...
            int i = 0;
            Matcher m;
            for (; i < args.length; i++) {
                String s = args[i];
                // boolChars
                // 是否是布尔值表
                if (null != bool_char) {
                    m = bool_char.matcher(s);
                    if (m.find()) {
                        char[] cs = m.group(m.groupCount()).toCharArray();
                        for (char c : cs) {
                            params.map.put("" + c, true);
                        }
                        continue;
                    }
                }

                // 键值
                m = PARAM_KEY.matcher(s);
                if (m.find()) {
                    String key = m.group(m.groupCount());
                    // 键就是布尔值
                    if (null != bool_key && bool_key.matcher(key).matches()) {
                        params.map.put(key, true);
                    }
                    // 木有后面的值了，那么作为 boolean
                    else if (i >= args.length - 1) {
                        params.map.put(key, true);
                        break;
                    }
                    // 如果有值 ...
                    else {
                        s = args[i + 1];
                        if (s.matches("^-[a-zA-Z_].*$")) {
                            params.map.put(key, true);
                            continue;
                        }
                        params.map.put(key, s);
                        // 跳过下一个值
                        i++;
                    }
                }
                // 嗯，是普通值 ...
                else {
                    list.add(s);
                }
            }
        }
        params.vals = list.toArray(new String[list.size()]);
        return params;
    }

    protected CmdParams() {}

    public String val(int index) {
        int i = index >= 0 ? index : vals.length + index;
        if (i < 0 || i >= vals.length)
            return null;
        return this.vals[i];
    }

    public String val_check(int index) {
        String v = val(index);
        if (null == v) {
            throw Er.create("e.cmd.lack.param.vals", index);
        }
        return v;
    }

    public boolean is(String key) {
        return map.getBoolean(key, false);
    }

    public boolean is(String key, boolean dft) {
        return map.getBoolean(key, dft);
    }

    public void setv(String key, Object val) {
        map.setv(key, val);
    }

    public boolean has(String key) {
        return map.has(key);
    }

    public boolean hasString(String key) {
        String val = this.get(key);
        return !Strings.isBlank(val) && !"true".equals(val);
    }

    public float getFloat(String key) {
        return map.getFloat(key, Float.NaN);
    }

    public float getFloat(String key, float dft) {
        return map.getFloat(key, dft);
    }

    public int getInt(String key) {
        return map.getInt(key, -1);
    }

    public int getInt(String key, int dft) {
        return map.getInt(key, dft);
    }

    public long getLong(String key) {
        return map.getLong(key, -1);
    }

    public long getLong(String key, long dft) {
        return map.getLong(key, dft);
    }

    public double getDouble(String key) {
        return map.getDouble(key, Double.NaN);
    }

    public double getDouble(String key, double dft) {
        return map.getDouble(key, dft);
    }

    public String get(String key) {
        return map.getString(key);
    }

    public String get(String key, String dft) {
        return map.getString(key, dft);
    }

    public String getString(String key) {
        return this.getString(key, "");
    }

    public String getString(String key, String dft) {
        Object val = map.get(key);
        if (null == val || val instanceof Boolean)
            return dft;
        return val.toString();
    }

    public String wrap(String key, String fmt) {
        return wrap(key, fmt, "");
    }

    public String wrap(String key, String fmt, String dft) {
        String val = this.get(key);
        if (Strings.isBlank(val)) {
            return dft;
        }
        return String.format(fmt, val);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> classOfEnum) {
        return map.getEnum(key, classOfEnum);
    }

    public <T> T getAs(String key, Class<T> classOfT) {
        return map.getAs(key, classOfT);
    }

    public <T> T getAs(String key, Class<T> classOfT, T dft) {
        return map.getAs(key, classOfT, dft);
    }

    public NutMap getMap(String key) {
        return getMap(key, null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public NutMap getMap(String key, NutMap dft) {
        Object val = map.get(key);
        if (null == val)
            return null;

        if (val instanceof Map)
            return NutMap.WRAP((Map) val);

        return Lang.map(val.toString());
    }

    public <T> List<T> getList(String key, Class<T> eleType) {
        return map.getList(key, eleType);
    }

    public String check(String key) {
        String v = get(key);
        if (Strings.isBlank(v)) {
            throw Er.create("e.cmd.lack.param", key);
        }
        return v;
    }

    public int checkInt(String key) {
        String v = get(key);
        if (Strings.isBlank(v)) {
            throw Er.create("e.cmd.lack.param.int", key);
        }
        return Integer.valueOf(v);
    }

    public long checkLong(String key) {
        String v = get(key);
        if (Strings.isBlank(v)) {
            throw Er.create("e.cmd.lack.param.long", key);
        }
        return Long.valueOf(v);
    }

    public float checkFloat(String key) {
        String v = get(key);
        if (Strings.isBlank(v)) {
            throw Er.create("e.cmd.lack.param.float", key);
        }
        return Float.valueOf(v);
    }

    public double checkDouble(String key) {
        String v = get(key);
        if (Strings.isBlank(v)) {
            throw Er.create("e.cmd.lack.param.double", key);
        }
        return Double.valueOf(key);
    }

    public NutMap map() {
        return map;
    }
    
    static class Er {
        public static RuntimeException create(String msg, Object key) {
            return new RuntimeException(String.format("%s : key=%s", msg, key));
        }
    }
}
