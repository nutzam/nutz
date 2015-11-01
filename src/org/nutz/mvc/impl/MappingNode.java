package org.nutz.mvc.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;

public class MappingNode<T> {

    private T obj;

    private T asterisk; // 匹配 *

    private T remain; // 匹配 **

    private MappingNode<T> quesmark; // 匹配 ?

    private Map<String, MappingNode<T>> map; // 匹配精确的值

    public MappingNode() {
        map = new HashMap<String, MappingNode<T>>();
    }

    private void add(T obj, String[] ss, int off) {
        // 还有路径
        if (off < ss.length) {
            String key = ss[off].toLowerCase();
            off++;
            // '*'
            if ("*".equals(key)) {
                if (off < ss.length) {
                    throw Lang.makeThrow("char '*' should be the last item"
                                                 + " in a Path '../**/%s'",
                                         Lang.concat(off, ss.length - off, "/", ss));
                }
                asterisk = obj;
            }
            // '**'
            else if ("**".equals(key)) {
                if (off < ss.length) {
                    throw Lang.makeThrow("'**' should be the last item" + " in a Path '../**/%s'",
                                         Lang.concat(off, ss.length - off, "/", ss));
                }
                remain = obj;
            }
            // '?'
            else if ("?".equals(key)) {
                if (quesmark == null) // 也许这个节点之前就已经有值呢
                    quesmark = new MappingNode<T>();
                quesmark.add(obj, ss, off);
            }
            // 其它节点，加入 map
            else {
                MappingNode<T> node = map.get(key);
                if (null == node) {
                    node = new MappingNode<T>();
                    map.put(key, node);
                }
                node.add(obj, ss, off);
            }

        }
        // 没有路径了
        else {
            this.obj = obj;
        }
    }

    private T get(ActionContext ac, String[] ss, int off) {
        // 路径已经没有内容了，看看本节点是否有一个对象
        if (off >= ss.length) {
            return obj == null ? (asterisk == null ? remain : asterisk) : obj;
        }

        String key = ss[off];
        // 先在 map 里寻找，
        MappingNode<T> node = map.get(key.toLowerCase());
        if (null != node) {
            // 在子节点中查找
            T t = node.get(ac, ss, off + 1);
            if (t != null)
                return t;
            // 找不到的时候, 继续在当前节点找泛匹配(?或者*)
        }

        // 如果没有看看是否有 '?' 的匹配
        if (quesmark != null) {
            ac.getPathArgs().add(key);
            T t = quesmark.get(ac, ss, off + 1);
            if (t != null)
                return t;
            ac.getPathArgs().remove(ac.getPathArgs().size() - 1);
        }

        // 还没有则看看是否有 '*' 的匹配
        if (null != asterisk) {
            List<String> pathArgs = ac.getPathArgs();
            while (off < ss.length)
                pathArgs.add(ss[off++]);
            return asterisk;
        }

        // 最后看看是不是有 '**' 匹配
        if (null != remain) {
            String ph = Lang.concat(off, ss.length - off, "/", ss).toString();
            if (!Strings.isBlank(ac.getSuffix())) {
                HttpServletRequest req = ac.getRequest();
                String url = Strings.sBlank(req.getPathInfo(), req.getServletPath());
                // 看看有没有必要补一个 "/"
                if (url.endsWith("/." + ac.getSuffix())) {
                    ph += "/";
                }
                ph += "." + ac.getSuffix();
            }
            ac.getPathArgs().add(ph);
            return remain;
        }

        return null;
    }

    /**
     * 增加一个映射,将 obj 映射到 path 上,或 path 上的[?,*]
     */
    public void add(String path, T obj) {
        try {
            String[] ss = Strings.splitIgnoreBlank(path, "/");
            add(obj, ss, 0);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e, "Wrong Url path format '%s'", path);
        }
    }

    public T get(ActionContext ac, String path) {
        return get(ac, path, null);
    }

    public T get(ActionContext ac, String path, String suffix) {
        ac.setPath(path);
        ac.setPathArgs(new LinkedList<String>());
        String[] ss = Strings.splitIgnoreBlank(path, "/");
        return get(ac, ss, 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTo(sb, 0);
        return sb.toString();
    }

    private void appendTo(StringBuilder sb, int indent) {
        String prefix = Strings.dup("   ", indent);
        sb.append(prefix).append('<').append(Strings.sNull(obj, "null")).append('>');

        prefix = "\n   " + prefix;
        if (null != asterisk) {
            sb.append(prefix).append(" * : ").append(asterisk.toString());
        }
        if (null != quesmark) {
            sb.append(prefix).append(" ? : ");
            quesmark.appendTo(sb, indent + 1);
        }
        for (String key : map.keySet()) {
            sb.append(prefix).append(" '" + key + "' : ");
            map.get(key).appendTo(sb, indent + 1);
        }
    }

}
