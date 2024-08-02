package org.nutz.mock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mock.servlet.MockHttpSession;
import org.nutz.mock.servlet.MockServletConfig;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mock.servlet.multipart.MultipartInputStream;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 一些方面的静态方法
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Mock {

    public static class servlet {
        public static MockServletContext context() {
            return new MockServletContext();
        }

        public static MockServletConfig config(String s) {
            return new MockServletConfig(context(), s);
        }

        public static MockHttpServletRequest request() {
            return new MockHttpServletRequest();
        }

        public static MockHttpServletRequest fullRequest() {
            MockHttpServletRequest req = request();
            req.setSession(session(context()));
            return req;
        }

        public static MockHttpSession session(MockServletContext context) {
            return new MockHttpSession(context);
        }

        public static ServletInputStream ins(final InputStream ins) {
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return ins.read();
                }

                @Override
                public int available() throws IOException {
                    return super.available();
                }

                @Override
                public void close() throws IOException {
                    ins.close();
                }

                @Override
                public synchronized void mark(int readlimit) {
                    ins.mark(readlimit);
                }

                @Override
                public boolean markSupported() {
                    return ins.markSupported();
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    return ins.read(b, off, len);
                }

                @Override
                public int read(byte[] b) throws IOException {
                    return ins.read(b);
                }

                @Override
                public synchronized void reset() throws IOException {
                    ins.reset();
                }

                @Override
                public long skip(long n) throws IOException {
                    return ins.skip(n);
                }

                @Override
                public boolean isFinished() {

                    return false;
                }

                @Override
                public boolean isReady() {

                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {}
            };
        }

        public static ServletInputStream ins(String path) {
            return ins(Streams.fileIn(path));
        }

        public static MultipartInputStream insmulti(String charset, String boundary) {
            return new MultipartInputStream(charset, boundary);
        }

        public static MultipartInputStream insmulti(String charset) {
            return insmulti(charset,
                            "------NutzMockHTTPBoundary@"
                                     + Long.toHexString(System.currentTimeMillis()));
        }

        public static MultipartInputStream insmulti(String charset, File... files) {
            MultipartInputStream ins = insmulti(charset);
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    ins.append("F" + i, files[i]);
                }
            }
            return ins;
        }
    }

    public static final InvocationHandler EmtryInvocationHandler = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args)
                throws Throwable {
            throw Lang.noImplement();
        };
    };

    public static final HttpServletRequest EmtryHttpServletRequest = (HttpServletRequest) Proxy.newProxyInstance(Mock.class.getClassLoader(),
                                                                                                                 new Class[]{HttpServletRequest.class},
                                                                                                                 EmtryInvocationHandler);

    public static final HttpServletResponse EmtryHttpServletResponse = (HttpServletResponse) Proxy.newProxyInstance(Mock.class.getClassLoader(),
                                                                                                                    new Class[]{HttpServletResponse.class},
                                                                                                                    EmtryInvocationHandler);

}
