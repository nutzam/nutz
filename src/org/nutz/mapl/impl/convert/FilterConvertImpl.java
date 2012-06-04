package org.nutz.mapl.impl.convert;

import java.util.ArrayList;
import java.util.List;

import org.nutz.json.Json;
import org.nutz.lang.Streams;
import org.nutz.mapl.MaplConvert;
import org.nutz.mapl.impl.MaplEach;
import org.nutz.mapl.impl.MaplRebuild;

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
public class FilterConvertImpl extends MaplEach implements MaplConvert{
    //处理列表
    private List<String> paths = new ArrayList<String>();
    //类型, 取名自exclude(排除), include(包含), false时为排除, true时为包含
    private boolean clude = false;
    private MaplRebuild build = new MaplRebuild();
    
    @SuppressWarnings("unchecked")
    public FilterConvertImpl(String path) {
        paths = (List<String>) Json.fromJson(Streams.fileInr(path));
    }
    
    public FilterConvertImpl(List<String> paths){
        this.paths = paths;
    }
    
    /**
     * 转换
     * @param obj 目标对象
     * @param model 对应关系
     * @return 
     */
    public Object convert(Object obj){
        each(obj);
        return build.fetchNewobj();
    }
    
    protected void DLR(String path, Object item) {
        if(clude){
            if(paths.contains(path)){
                build.put(path, item, arrayIndex);
            } 
        }
    }

    protected void LRD(String path, Object item) {
        if(clude){
           return;
        }
        for(String p : paths){
            if(!p.startsWith(path) && !path.startsWith(p)){
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
