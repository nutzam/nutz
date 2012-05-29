package org.nutz.maplist.impl.each;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonException;
import org.nutz.json.JsonFilter;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.maplist.MapListEach;
import org.nutz.maplist.MapListRebuild;

/**
 * Json转换.
 * <p> 将一种JSON结构转换成另外一种JSON结构.例:
 * <pre>
 *  {
 *      "age":"123",
 *      "name":"juqkai"
 *  }
 *  转换成:
 *  {
 *      "年龄":"123",
 *      "姓名":"juqkai"
 *  }
 * </pre>
 * <p>要进行这样的转换需要预先配置一个对应关系的配置, 具体的配置关系说明如下:
 * <ul>
 * <li>使用原JSON一样的结构
 * <li>有数组的, 只写第一个元素的结构
 * <li>原结构中的值, 以字符串或字符串数组做为目标结构的对应关系
 * <li>对应关系可以为数组
 * <li>有数组的, 目标结构以key[].abc来代替数组
 * <li>原结构数组层次强制限定一致, 目标结构中'[]'的索引按原结构中出现先后顺序进行匹配.
 * <li>如果原结果不存在, 那默认为0
 * <li>未在模板中申明的不做转换
 * </ul>
 * <p> 例:
 * <pre>
 * 例1:
 *  {
 *      "age":"user.年龄",
 *      "name":["user.name", "user.姓名"]
 *  }
 * 例2(原json:[{"name":"nutz"},{"name":"juqkai"}]):
 * [{
 *      "name":"[].姓名"
 * }]
 * 例3:
 * {
 *      users:[
 *          {
 *              "name":["people[].name", "users[].name"],
 *              "age":"users[].name"
 *          }
 *      ]
 * }
 * </pre>
 * @author juqkai(juqkai@gmail.com)
 */
public class MapListConvert extends MapListEach implements JsonFilter{
    //关系
    private Map<String, List<String>> relation = new HashMap<String, List<String>>();
    
    private MapListRebuild structure = new MapListRebuild();
    
    /**
     * 
     * @param path 模板文件路径
     */
    public MapListConvert(String path){
        Object obj = Json.fromJson(Streams.fileInr(path));
        loadRelation(obj, "");
    }
    /**
     * 
     * @param reader 模板流
     */
    public MapListConvert(Reader reader){
        Object obj = Json.fromJson(reader);
        loadRelation(obj, "");
    }
    /**
     * 
     * @param obj 模板的Map, List结构
     */
    public MapListConvert(Object obj){
        loadRelation(obj, "");
    }
    
    /**
     * 转换
     * @param obj 目标对象
     * @param model 对应关系
     * @return 
     */
    public Object filter(Object obj) throws JsonException{
        convertObj(obj);
        return structure.fetchNewobj();
    }

    /**
     * 重建新对象
     * @param path
     * @param object
     */
    public void handle(String path, Object object) {
        if(relation.containsKey(path)){
            List<String> dests = relation.get(path);
            for(String dest : dests){
                if(dest.equals("")){
                    structure.put(path, object, arrayIndex);
                    continue;
                } 
                structure.put(dest, object, arrayIndex);
            }
        }
    }
    
    /**
     * 解析配置信息
     * @param obj
     * @param path
     */
    private void loadRelation(Object obj, String path) {
        if(obj instanceof Map){
            loadMapRelation((Map<?, ?>) obj, path);
        } else if(obj instanceof List){
            loadListRelation((List<?>) obj, path);
        } else {
            throw new RuntimeException("无法识别的类型!");
        }
    }
    /**
     * 解析List配置信息
     * @param obj
     * @param path
     */
    @SuppressWarnings("unchecked")
    private void loadListRelation(List<?> obj, String path) {
        if(obj.size() <= 0){
            return;
        }
        if(obj.get(0) instanceof String){
            relation.put(path, (List<String>) obj);
            return;
        }
        loadRelation(obj.get(0), path + "[]");
    }
    /**
     * 解析MAP配置信息
     * @param obj
     * @param path
     */
    private void loadMapRelation(Map<?, ?> obj, String path) {
        for(Object key : obj.keySet()){
            Object val = obj.get(key);
            if(val instanceof String){
                relation.put(path + space(path) + key.toString(), Lang.list(val.toString()));
                continue;
            }
            loadRelation(obj.get(key), path + space(path) + key.toString());
        }
    }
    
    private String space(String path){
        return path == "" ? "" : ".";
    }
    
}
