<%@page import="org.nutz.mvc.util.ScoffoldViewUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String viewName = (String)request.getAttribute("scoffold_viewName");
	Object obj = request.getAttribute("obj");
	//ScoffoldViewUtil.process(viewName,out);
	out.print(ScoffoldViewUtil.process(viewName, obj));
%>