package org.nutz.json;

import java.io.IOException;
import java.lang.reflect.Type;

import org.nutz.lang.Mirror;

public interface JsonTypeHandler {

    /**
     * 是否支持 fromJson操作
     */
    boolean supportFromJson(Type type);
    
    /**
     * 是否支持当前对象的toJson操作
     * @param mirror obj对应的Mirrir
     * @param obj 正在等着被转换的对象
     * @param jf JsonFormat实例
     * @return 若支持,接下来会调用toJson
     */
    boolean supportToJson(Mirror<?> mirror, Object obj, JsonFormat jf);
    
    /**
     * 将对象变成json字符串
     * @param mirror currentObj对应的Mirrir
     * @param currentObj 当前正在转换的对象
     * @param r Json渲染器
     * @param jf JsonFormat实例
     * @throws IOException
     */
    void toJson(Mirror<?> mirror, Object currentObj, JsonRender r, JsonFormat jf) throws IOException;
    
    Object fromJson(Object data, Type type) throws Exception;
    
    /**
     * 是否需要进行循环依赖检测
     */
    boolean shallCheckMemo();
}
