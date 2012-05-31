package org.nutz.maplist.impl;

import java.util.Collection;
import java.util.Map;

/**
 * MapList结构访问
 * @author juqkai(juqkai@gmail.com)
 */
public class MaplistCell {
    /**
     * 访问MAP, List结构的数据, 通过 uers[2].name 这种形式.
     */
    public static Object cell(Object obj, String path){
        String paths[] = path.split("\\.");
        if(paths == null || paths.length == 0){
            paths = new String []{path};
        }
        return new MaplistCell().cell(obj, paths, 0);
    }
    
    private Object cell(Object obj, String[] paths, int index){
        if(index >= paths.length){
            return obj;
        }
        if(obj instanceof Map){
            return cellMap(obj, paths, index);
        }else if(obj instanceof Collection){
            if(!canList(paths[index])){
                try{
                    return cellList(obj, paths, index, Integer.parseInt(paths[index]));
                } catch(NumberFormatException e){
                    try{
                        index++;
                        return cellList(obj, paths, index, Integer.parseInt(paths[index]));
                    }catch(NumberFormatException ex){
                        throw new RuntimeException("List路径错误, 请使用list[1]或list.1格式");
                    }
                }
//                return cell(obj, paths, index + 1);
            }
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
            if(index + 1 >= paths.length){
                return o;
            }
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
        return cellList(obj, paths, index, fetchListIndex(paths[index]));
    }
    /**
     * 访问集合
     * @param obj
     * @param paths
     * @param index
     * @return
     */
    private Object cellList(Object obj, String[] paths, int index, int arrayIndex){
        if(!(obj instanceof Collection)){
            throw new RuntimeException("提取数据所使用的路径有问题.请检查[" + paths[index] + "]结点!");
        }
        Collection<?> col = (Collection<?>) obj;
        Object o = col.toArray()[arrayIndex];
        return cell(o, paths, index + 1);
    }
    /**
     * 得到key
     * @param path
     * @return
     */
    private String fetchKey(String path){
        if(!canList(path)){
            return path;
        }
        return path.substring(0, path.indexOf('['));
    }
    private boolean canList(String path){
        return path.indexOf('[') >= 0;
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
