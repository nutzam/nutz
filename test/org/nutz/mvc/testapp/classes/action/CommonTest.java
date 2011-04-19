package org.nutz.mvc.testapp.classes.action;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.testapp.BaseWebappTest;
import org.nutz.mvc.testapp.classes.bean.UserT;

@InjectName
@IocBean
@At("/common")
@Ok("raw")
public class CommonTest extends BaseWebappTest {

	//最最基本的测试
	@At("/pathArgs/*")
	public String test_base_pathargs(String name){
		return name;
	}
	
	//基本测试1
	@At("/pathArgs2/*")
	public String test_base_pathargs2(String name,
									  int id,
									  long pid,
									  short fid,
									  double xid,
									  float yid,
									  boolean z,
									  char p){
		return name + id + pid + fid + (int)xid + (int)yid + z + p;
	}
	
	//含?和*
	@At("/pathArgs3/?/blog/*")
	public String test_base_pathargs3(String type,long id){
		return type + "&" +id;
	}
	
	//与Parms混用
	@At("/pathArgs4/*")
	public String test_base_pathargs4(String key,@Param("..")UserT userT){
		return key+"&"+userT.getName();
	}
	

	//与Parms混用
	@At("/pathArgs5/*")
	public String test_base_pathargs5(String key,
            @Param("::user.")UserT user1,
            @Param("::user2.")UserT user2){
		return key+"&"+user1.getName()+"&"+user2.getName();
	}
	
	//Parms混用
	@At("/param")
	public String test_param(@Param("id") long id){
		return ""+id;
	}
	
	//Parms混用
	@At("/path")
	@Ok(">>:/${key}.jsp")
	public void test_req_param(){
	}
	
	//Test EL
	@At("/path2")
	@Ok("->:/${key.length() == 1 ? 'base' : 'false'}.jsp")
	public void test_req_param2(){
	}
}
