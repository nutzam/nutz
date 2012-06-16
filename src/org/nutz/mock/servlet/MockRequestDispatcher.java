package org.nutz.mock.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MockRequestDispatcher implements RequestDispatcher {

	public MockRequestDispatcher(String[] target, String dest) {
		target[0] = dest;
	}

	public void forward(ServletRequest req, ServletResponse resp) throws ServletException,
			IOException {}

	public void include(ServletRequest req, ServletResponse resp) throws ServletException,
			IOException {}

}
