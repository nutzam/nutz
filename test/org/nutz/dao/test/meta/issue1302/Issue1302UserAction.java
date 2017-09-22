package org.nutz.dao.test.meta.issue1302;

import java.util.HashMap;
import java.util.Map;

public enum Issue1302UserAction {

    /** 未知动作，用于旧代码兼容新新行为使用 */
    UNKNOWN("未知动作", 0),

    VIEW("查看", 100),

    COLLECT("收藏", 200),

    DELIVER("投递", 300);

    private static final Map<Integer, Issue1302UserAction> USER_ACTION_MAP = new HashMap<Integer, Issue1302UserAction>();

    static {
        for (Issue1302UserAction userAction : values()) {
            USER_ACTION_MAP.put(userAction.value(), userAction);
        }
    }

    /** 用户行为描述 */
    private String desc;

    /** 用户行为编码以及优先级 */
    private int priority;

    Issue1302UserAction(String desc, int priority) {
        this.desc = desc;
        this.priority = priority;
    }

    /**
     * 根据用户行为编码，获取用户行为
     * <p />
     * 为了兼容Nutz dao枚举类型转换，该方法必须命名为fromInt
     *
     * @param priority 编码
     * @return 用户行为
     */
    public static Issue1302UserAction fromInt(int priority) {
        Issue1302UserAction action = USER_ACTION_MAP.get(priority);
        return action != null ? action : UNKNOWN;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 获取用户行为编码
     * <p />
     * 为了兼容Nutz dao枚举类型转换，该方法必须命名为value
     *
     * @return 用户行为编码
     */
    public int value() {
        return priority;
    }
}
