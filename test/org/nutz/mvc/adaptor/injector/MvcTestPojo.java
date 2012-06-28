package org.nutz.mvc.adaptor.injector;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MvcTestPojo {

    public String[] names;

    public int num;

    public String str;

    public Long longValue;

    public Date date;
    
    public List<String> books;
    
    public List<MvcTestPojo> lists;
    
    public Set<MvcTestPojo> sets;
    
    public Map<String, MvcTestPojo> maps;
    
    public MvcTestPojo[] arrays;
}
