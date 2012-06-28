package org.nutz.ioc.aop.config.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 根据Json配置文件判断需要拦截哪些方法
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class JsonAopConfigration extends AbstractAopConfigration {

    public void setItemList(List<List<String>> itemList) {
        List<AopConfigrationItem> aopItemList = new ArrayList<AopConfigrationItem>();
        for (List<String> list : itemList) {
            AopConfigrationItem item = new AopConfigrationItem();
            item.setClassName(list.get(0));
            item.setMethodName(list.get(1));
            item.setInterceptor(list.get(2));
            if (list.size() == 4)
                item.setSingleton(Boolean.parseBoolean(list.get(3)));
            aopItemList.add(item);
        }
        super.setAopItemList(aopItemList);
    }

}
