package org.nutz.aop.interceptor.ioc;

import java.io.StringReader;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.meta.IocObject;

public class TransIocLoader implements IocLoader {

    protected JsonLoader proxy;
    
    public TransIocLoader() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("txNONE:            {type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [0]},\n");
        sb.append("txREAD_UNCOMMITTED:{type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [1]},\n");
        sb.append("txREAD_COMMITTED:  {type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [2]},\n");
        sb.append("txREPEATABLE_READ: {type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [4]},\n");
        sb.append("txSERIALIZABLE:    {type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [8]},");
        sb.setCharAt(sb.length() - 1, '}');
        proxy = new JsonLoader(new StringReader(sb.toString()));
    }
    
    public String[] getName() {
        return proxy.getName();
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        return proxy.load(loading, name);
    }

    public boolean has(String name) {
        return proxy.has(name);
    }
}
