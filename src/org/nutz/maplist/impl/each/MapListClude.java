package org.nutz.maplist.impl.each;

import java.util.ArrayList;
import java.util.List;

import org.nutz.json.Json;
import org.nutz.json.JsonException;
import org.nutz.json.JsonFilter;
import org.nutz.lang.Streams;
import org.nutz.maplist.MapListEach;
import org.nutz.maplist.MapListRebuild;

/**
 * Json过滤, 
 * <p>
 * 根据模板将原始的JSON进行过滤, 只显示一部分. 不做转换.
 * <p>
 * 包括两种模式, 即, 包含, 排除模式, 默认为排除模式.
 * <p>
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class MapListClude extends MapListEach implements JsonFilter{
    //处理列表
    private List<String> paths = new ArrayList<String>();
    //类型, 取名自exclude(排除), include(包含), false时为排除, true时为包含
    private boolean clude = false;
    private MapListRebuild build = new MapListRebuild();
    
    @SuppressWarnings("unchecked")
    public MapListClude(String path) {
        paths = (List<String>) Json.fromJson(Streams.fileInr(path));
    }
    
    public MapListClude(List<String> paths){
        this.paths = paths;
    }
    
    /**
     * 转换
     * @param obj 目标对象
     * @param model 对应关系
     * @return 
     */
    public Object filter(Object obj) throws JsonException{
        convertObj(obj);
        return build.fetchNewobj();
    }
    
    public void handle(String path, Object item) {
        if(clude){
            if(paths.contains(path)){
                build.put(path, item, arrayIndex);
            } 
        } else {
            if(!paths.contains(path)){
                build.put(path, item, arrayIndex);
            } 
        }
    }

    public void useExcludeModel() {
        this.clude = false;
    }
    public void useIncludeModel() {
        this.clude = true;
    }
}
