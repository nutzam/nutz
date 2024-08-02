package org.nutz.mock.servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class MockRequestDispatcher implements RequestDispatcher {

    public MockRequestDispatcher(String[] target, String dest) {
        target[0] = dest;
    }

    @Override
    public void forward(ServletRequest arg0, ServletResponse arg1) throws ServletException,
            IOException {}

    @Override
    public void include(ServletRequest arg0, ServletResponse arg1) throws ServletException,
            IOException {}

}
