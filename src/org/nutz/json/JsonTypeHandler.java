package org.nutz.json;

import java.io.IOException;

import org.nutz.lang.Mirror;

public abstract class JsonTypeHandler {

    /**
     * 是否支持 fromJson操作
     * @param mirror TODO
     * @param obj TODO
     */
    public boolean supportFromJson(Mirror<?> mirror, Object obj) {
        return false;
    }
    
    /**
     * 是否支持当前对象的toJson操作
     * @param mirror obj对应的Mirrir
     * @param obj 正在等着被转换的对象
     * @param jf JsonFormat实例
     * @return 若支持,接下来会调用toJson
     */
    public boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf) {
        return false;
    }
    
    /**
     * 将对象变成json字符串
     * @param mirror currentObj对应的Mirrir
     * @param currentObj 当前正在转换的对象
     * @param r Json渲染器
     * @param jf JsonFormat实例
     * @throws IOException
     */
    public void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException {
        
    }
    
    public Object fromJson(Object obj, Mirror<?> mirror) throws Exception {
        return null;
    };
    
    /**
     * 是否需要进行循环依赖检测
     */
    public boolean shallCheckMemo() {
        return false;
    }
}
