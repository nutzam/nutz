package controllers;

import org.nutz.dao.Dao;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

public class TestController {

	public static boolean SCOFFOLDING = true;
	@At("/helloAt")
	public void hello(){}
	@Ok("jsp:views.test1")
	public void test1(String arg){}
	public void test3(){}
	private Dao dao;
	public void setDao(Dao dao){
		this.dao = dao;
	}
}
