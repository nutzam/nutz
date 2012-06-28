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

    public void forward(ServletRequest arg0, ServletResponse arg1) throws ServletException,
            IOException {}

    public void include(ServletRequest arg0, ServletResponse arg1) throws ServletException,
            IOException {}

}
