package org.nutz.maplist;

import java.util.Collection;
import java.util.Map;

/**
 * MapList结构访问
 * @author juqkai(juqkai@gmail.com)
 */
public class MapListCell {
    /**
     * 访问MAP, List结构的数据, 通过 uers[2].name 这种形式.
     */
    public static Object cell(Object obj, String path){
        String paths[] = path.split("\\.");
        if(paths == null || paths.length == 0){
            paths = new String []{path};
        }
        return new MapListCell().cell(obj, paths, 0);
    }
    
    private Object cell(Object obj, String[] paths, int index){
        if(index >= paths.length){
            return obj;
        }
        if(obj instanceof Map){
            return cellMap(obj, paths, index);
        }else if(obj instanceof Collection){
            return cellList(obj, paths, index);
        }
        throw new RuntimeException(obj.getClass() + "类型无法识别! 只支持Map, List结构!");
    }
    /**
     * 访问map
     * @param obj
     * @param paths
     * @param index
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object cellMap(Object obj, String[] paths, int index){
        if(!(obj instanceof Map)){
            throw new RuntimeException("提取数据所使用的路径有问题.请检查[" + paths[index] + "]结点!");
        }
        Object o = ((Map<String, Object>)obj).get(fetchKey(paths[index]));
        if(o instanceof Collection){
            return cell(o, paths, index);
        }
        return cell(o, paths, index + 1);
    }
    /**
     * 访问集合
     * @param obj
     * @param paths
     * @param index
     * @return
     */
    private Object cellList(Object obj, String[] paths, int index){
        if(!(obj instanceof Collection)){
            throw new RuntimeException("提取数据所使用的路径有问题.请检查[" + paths[index] + "]结点!");
        }
        int i = fetchListIndex(paths[index]);
        Collection<?> col = (Collection<?>) obj;
        Object o = col.toArray()[i];
        return cell(o, paths, index + 1);
    }
    /**
     * 得到key
     * @param path
     * @return
     */
    private String fetchKey(String path){
        if(path.indexOf('[') <= 0){
            return path;
        }
        return path.substring(0, path.indexOf('['));
    }
    /**
     * 得到集合的index
     * @param path
     * @return
     */
    private int fetchListIndex(String path){
        if(path.indexOf('[') < 0){
            throw new RuntimeException(path + "没有索引!");
        }
        String index = path.substring(path.indexOf('[') + 1, path.indexOf(']'));
        return Integer.parseInt(index);
    }
}
