package org.nutz.aop.interceptor.ioc;

import java.io.StringReader;

import org.nutz.ioc.loader.json.JsonLoader;

public class TransIocLoader extends JsonLoader {
    
    public TransIocLoader() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("txNONE:            {type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [0]},\n");
        sb.append("txREAD_UNCOMMITTED:{type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [1]},\n");
        sb.append("txREAD_COMMITTED:  {type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [2]},\n");
        sb.append("txREPEATABLE_READ: {type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [4]},\n");
        sb.append("txSERIALIZABLE:    {type : 'org.nutz.aop.interceptor.TransactionInterceptor',args : [8]},");
        sb.setCharAt(sb.length() - 1, '}');
        loadFromReader(new StringReader(sb.toString()));
    }
}
