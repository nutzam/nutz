package org.nutz.lang.segment;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;

public class CharSegment implements Segment, Cloneable {

    public CharSegment() {}

    public CharSegment(String str) {
        valueOf(str);
    }

    @SuppressWarnings("unchecked")
    public Segment add(String key, Object v) {
        if (!context.has(key)) {
            context.set(key, v);
            return this;
        }
        Object val = context.get(key);
        if (val == null) {
            context.set(key, v);
        } else if (val instanceof Collection<?>) {
            ((Collection<Object>) val).add(v);
        } else {
            List<Object> objSet = new LinkedList<Object>();
            objSet.add(val);
            objSet.add(v);
            context.set(key, objSet);
        }
        return this;
    }

    public void clearAll() {
        context.clear();
    }

    public boolean contains(String key) {
        return keys.containsKey(key);
    }

    public Segment born() {
        return new CharSegment(this.getOrginalString());
    }

    private String orgString;

    public String getOrginalString() {
        return orgString;
    }

    public Segment clone() {
        CharSegment cs = new CharSegment();
        cs.parse(Lang.inr(orgString));
        cs.context = this.context.clone();
        return cs;
    }

    public Set<String> keys() {
        return this.keys.keySet();
    }

    public int keyCount() {
        return this.keys.size();
    }

    public boolean hasKey() {
        return !this.keys.isEmpty();
    }

    public List<Object> values() {
        List<Object> re = new ArrayList<Object>(nodes.size());
        for (SegmentNode node : nodes) {
            if (node.isKey())
                re.add(context.get(node.getValue()));
            else
                re.add(node.getValue());
        }
        return re;
    }

    public Segment setAll(Object v) {
        for (String key : keys())
            context.set(key, v);
        return this;
    }

    public Segment setBy(Object obj) {
        Iterator<String> it = keys().iterator();
        Class<?> klass = obj.getClass();
        Mirror<?> mirror = Mirror.me(klass);
        // Primitive Type: set it to all PlugPoints
        if (mirror.isStringLike() || mirror.isBoolean() || mirror.isNumber() || mirror.isChar()) {
            this.setAll(obj);
        }
        // Map: set by key
        else if (mirror.isOf(Map.class)) {
            Map<?, ?> map = (Map<?, ?>) obj;
            while (it.hasNext()) {
                String key = it.next();
                try {
                    this.set(key, map.get(key));
                }
                catch (Exception e) {
                    this.set(key, "");
                }
            }
        }
        // POJO: set by field
        else {
            while (it.hasNext()) {
                String key = it.next();
                try {
                    this.set(key, mirror.getValue(obj, key));
                }
                catch (Exception e) {
                    this.set(key, "");
                }
            }
        }
        return this;
    }

    public Segment set(String key, Object v) {
        context.set(key, v);
        return this;
    }

    public List<SegmentNode> getNodes() {
        return nodes;
    }

    private Context context;

    private List<SegmentNode> nodes;

    private NutMap keys;

    public void parse(Reader reader) {
        nodes = new LinkedList<SegmentNode>();
        context = Lang.context();
        keys = new NutMap();
        StringBuilder org = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        int b;
        try {
            while (-1 != (b = reader.read())) {
                org.append((char) b);
                switch (b) {
                case '$':
                    b = reader.read();
                    org.append((char) b);
                    // Escape
                    if (b == '$') {
                        sb.append((char) b);
                    }
                    // In Plug Point
                    else if (b == '{') {
                        // Save before
                        if (sb.length() > 0) {
                            nodes.add(SegmentNode.val(sb.toString()));
                            sb = new StringBuilder();
                        }
                        // Search the end
                        while (-1 != (b = reader.read())) {
                            org.append((char) b);
                            if (b == '}')
                                break;
                            sb.append((char) b);
                        }
                        if (b != '}')
                            throw Lang.makeThrow("Error format around '%s'", sb);
                        // Create Key
                        String key = sb.toString();
                        nodes.add(SegmentNode.key(key));
                        keys.put(key, null);
                        sb = new StringBuilder();
                    }
                    // Normal
                    else {
                        sb.append('$').append((char) b);
                    }
                    break;
                default:
                    sb.append((char) b);
                }
            }
            if (sb.length() > 0)
                nodes.add(SegmentNode.val(sb.toString()));
            // Store the Oraginal Value
            orgString = org.toString();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public Segment valueOf(String str) {
        parse(new StringReader(str));
        return this;
    }

    public CharSequence render() {
        return render(context);
    }

    public CharSequence render(Context context) {
        StringBuilder sb = new StringBuilder();
        for (SegmentNode node : nodes) {
            Object val = node.isKey() ? context.get(node.getValue()) : node.getValue();
            if (null == val)
                continue;
            if (val instanceof Collection<?>) {
                for (Object obj : (Collection<?>) val) {
                    sb.append(obj);
                }
            } else {
                sb.append(val);
            }
        }
        return sb;
    }

    public Context getContext() {
        return context;
    }

    public void fillNulls(Context context) {
        for (String key : keys.keySet()) {
            Object val = context.get(key);
            if (null == val)
                context.set(key, "${" + key + "}");
        }
    }

    public String toString() {
        return render().toString();
    }

}
