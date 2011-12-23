package org.nutz.el.issue125;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

import static org.junit.Assert.*;


public class Issue125Test {
	@Test
	public void test() throws InstantiationException, IllegalAccessException{
		String[] a = new String[]{"a","b"};
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("a", a);
		El exp = new El("util.test(map['a'])");
		Context context = Lang.context();
		context.set("util",StringUtil.class.newInstance());
		context.set("map", map);
		assertEquals("ab", exp.eval(context));
	}
	
	@Test
	public void test2(){
		String[] a = new String[]{"a","b"};
        String[] b = new String[]{"1","2"};
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("a", a);
        map.put("b", b);
        El exp = new El("util.test(map['a'][0],map['b'][0])");  // 预编译结果为一个 El 对象
        Context context = Lang.context();
        context.set("util",new StringUtil());
        context.set("map", map);
        System.out.println(exp.eval(context));
	}
}
