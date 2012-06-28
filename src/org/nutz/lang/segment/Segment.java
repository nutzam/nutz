package org.nutz.lang.segment;

import java.io.Reader;
import java.util.List;
import java.util.Set;

import org.nutz.lang.util.Context;

/**
 * 字符串片段。你可以通过这个接口的函数，为片段中的占位符设值。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Segment {

    Segment setAll(Object v);

    /**
     * 根据对象设置每个插入点的值。
     * 
     * @param obj
     *            可以是 POJO 或者 Map
     * @return Segment
     */
    Segment setBy(Object obj);

    Segment set(String key, Object v);

    Segment add(String key, Object v);

    void clearAll();

    Segment born();

    Segment clone();

    boolean contains(String key);

    Set<String> keys();
    
    int keyCount();
    
    boolean hasKey();

    List<Object> values();
    
    Segment valueOf(String str);

    void parse(Reader reader);

    CharSequence render();

    CharSequence render(Context context);

    Context getContext();

    void fillNulls(Context context);

    String getOrginalString();

    List<SegmentNode> getNodes();
}
