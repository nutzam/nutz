package org.nutz.mvc.impl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.lang.Lang;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.MultiLineProperties;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.MessageLoader;
import org.nutz.mvc.Mvcs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public class NutMessageLoader implements MessageLoader {

    private static final Log log = Logs.get();

    public Map<String, Map<String, Object>> load(String refer) {
        Map<String, Map<String, Object>> re = new HashMap<String, Map<String, Object>>();
        List<NutResource> allnrs = Scans.me().scan(refer, "^.+[.]properties$");
        if (log.isDebugEnabled())
            log.debugf("Load Messages in %s resource : [%s]", allnrs.size(), allnrs);
        // 求取路径的最大长度
        int max = 0;
        for (NutResource nr : allnrs) {
            String[] nms = nr.getName().split("[\\\\/]");
            max = Math.max(max, nms.length);
        }

        // 根据第二级目录，编制列表
        Map<String, List<NutResource>> map = new HashMap<String, List<NutResource>>();
        for (NutResource nr : allnrs) {
            String langType;
            String resName = nr.getName();
            if (resName.contains("/"))
                langType = resName.substring(0, resName.indexOf('/'));
            else if (resName.contains("\\"))
                langType = resName.substring(0, resName.indexOf('\\'));
            else
                langType = Mvcs.DEFAULT_MSGS;
            // 按语言类型编制
            List<NutResource> list = map.get(langType);
            if (null == list) {
                list = new ArrayList<NutResource>(10);
                map.put(langType, list);
            }
            list.add(nr);
        }
        // 根据语言的分类，依次构建字符串 Map
        try {
            for (Entry<String, List<NutResource>> entry : map.entrySet()) {
                List<NutResource> nrs = entry.getValue();
                String langType = entry.getKey();
                // 循环读取该语言的文件夹
                for (NutResource nr : nrs) {
                    // 读取多行属性
                    MultiLineProperties p = new MultiLineProperties();
                    Reader r = nr.getReader();
                    p.load(r);
                    r.close();

                    // 获取当前语言的 Map
                    Map<String, Object> msgs = re.get(langType);
                    if (null == msgs) {
                        msgs = new NutMessageMap();
                        re.put(langType, msgs);
                    }

                    // 将本地化字符串增加到当前语言
                    for (String key : p.keySet()) {
                        String str = p.get(key);
                        Segment seg = (new CharSegment()).valueOf(str);
                        if (seg.keys().isEmpty())
                            msgs.put(key, str);
                        else
                            msgs.put(key, seg);
                    }

                } // ~ 内部循环结束
            }
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
        // 看看有没有默认的,没有的话,取第一个为默认
        // TODO 这段代码应该在 1.b.46 之后的某一个版本删掉
        if (!re.containsKey(Mvcs.DEFAULT_MSGS)) {
            if (re.size() > 0) {
                String first_lang = re.keySet().iterator().next();
                re.put(Mvcs.DEFAULT_MSGS, re.get(first_lang));
            }
        }

        if (log.isDebugEnabled())
            log.debugf("Message Loaded, size = %s", re.size());

        // 返回结果
        return re;
    }

}
