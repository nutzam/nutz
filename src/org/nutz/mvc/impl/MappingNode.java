package org.nutz.mvc.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;

public class MappingNode<T> {

    private T obj;

    private T asterisk;

    private MappingNode<T> quesmark;

    private Map<String, MappingNode<T>> map;

    public MappingNode() {
        map = new HashMap<String, MappingNode<T>>();
    }

    private void add(Iterator<String> it, T obj) {
        // 还有路径
        if (it.hasNext()) {
            String key = it.next().toLowerCase();
            // '*'
            if ("*".equals(key)) {
                if (it.hasNext()) {
                    throw Lang.makeThrow(    "char '*' should be the last item in a Path '../*/%s/..'",
                                            it.next());
                }
                asterisk = obj;
            }
            // '?'
            else if ("?".equals(key)) {
                if (quesmark == null) //也许这个节点之前就已经有值呢
                    quesmark = new MappingNode<T>();
                quesmark.add(it, obj);
            }
            // 其它节点，加入 map
            else {
                MappingNode<T> node = map.get(key);
                if (null == node) {
                    node = new MappingNode<T>();
                    map.put(key, node);
                }
                node.add(it, obj);
            }

        }
        // 没有路径了
        else {
            this.obj = obj;
        }
    }

    private T get(ActionContext ac, Iterator<String> it) {
        // 路径已经没有内容了，看看本节点是否有一个对象
        if (!it.hasNext()) {
            return obj == null ? asterisk : obj;
        }

        String key = it.next();
        // 先在 map 里寻找，
        MappingNode<T> node = map.get(key.toLowerCase());
        if (null != node)
            return node.get(ac, it);

        // 如果没有看看是否有 '?' 的匹配
        if (quesmark != null) {
            ac.getPathArgs().add(key);
            return quesmark.get(ac, it);
        }

        // 还没有则看看是否有 '*' 的匹配
        if (null != asterisk) {
            List<String> pathArgs = ac.getPathArgs();
            pathArgs.add(key);
            while (it.hasNext())
                pathArgs.add(it.next());
            return asterisk;
        }

        return null;
    }

    /**
     * 增加一个映射,将 obj 映射到 path 上,或 path 上的[?,*] 
     */
    public void add(String path, T obj) {
        try {
            add(Lang.list(Strings.splitIgnoreBlank(path, "/")).iterator(), obj);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e, "Wrong Url path format '%s'", path);
        }
    }

    public T get(ActionContext ac, String path) {
        ac.setPath(path);
        ac.setPathArgs(new LinkedList<String>());
        return get(ac, Lang.list(Strings.splitIgnoreBlank(path, "/")).iterator());
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
