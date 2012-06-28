package org.nutz.lang.socket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SocketActionTable {

    private boolean[] nots;

    private SocketAction[] aary;

    private Pattern[] ptns;

    private Map<String, SocketAction> map;

    SocketActionTable(Map<String, SocketAction> actions) {
        List<String> rl = new ArrayList<String>(actions.size());
        List<SocketAction> al = new ArrayList<SocketAction>(actions.size());
        map = new HashMap<String, SocketAction>();
        // 建立正则式列表
        for (String key : actions.keySet()) {
            // key 为空 ，相当于全部匹配
            if (key == null) {
                rl.add("$:.*");
                al.add(actions.get(key));
            }
            // 通过一个正则表达式匹配动作
            else if (key.startsWith("$:")) {
                rl.add(key.substring(2));
                al.add(actions.get(key));
            }
            // 精确匹配动作
            else {
                map.put(key, actions.get(key));
            }
        }

        // 处理正则式列表中的 "!"
        nots = new boolean[rl.size()];
        Arrays.fill(nots, false);
        aary = new SocketAction[nots.length];
        ptns = new Pattern[nots.length];
        for (int i = 0; i < nots.length; i++) {
            aary[i] = al.get(i);
            if (rl.get(i).startsWith("!")) {
                nots[i] = true;
                ptns[i] = Pattern.compile(rl.get(i).substring(1));
            } else {
                ptns[i] = Pattern.compile(rl.get(i));
            }
        }
    }

    /**
     * 根据输入的行，得到一个动作执行对象
     * 
     * @param line
     *            输入的行
     * @return 动作执对象，null 表示没有动作可以匹配这一行
     */
    public SocketAction get(String line) {
        // 是否有精确匹配
        SocketAction sa = map.get(line);
        if (null != sa)
            return sa;

        // 用正则式匹配
        for (int i = 0; i < nots.length; i++) {
            if (ptns[i].matcher(line).find() && !nots[i]) {
                return aary[i];
            } else if (nots[i]) {
                return aary[i];
            }
        }

        // 啥都没有 -_-!
        return null;

    }

}
