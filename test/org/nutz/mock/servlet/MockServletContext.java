package org.nutz.mock.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;

public class MockServletContext extends MockServletObject implements ServletContext {

    private static final Log log = Logs.get();

    @Override
    public int getMajorVersion() {
        return 2;
    }

    @Override
    public String getMimeType(String arg0) {
        throw Lang.noImplement();
    }

    @Override
    public int getMinorVersion() {
        return 5;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String arg0) {
        throw Lang.noImplement();
    }

    @Override
    public String getRealPath(String path) {
        if (path.startsWith("/WEB-INF/lib/")) {
            return new File(path.substring("/WEB-INF/lib/".length())).getAbsolutePath();
        }
        if (path.startsWith("/WEB-INF/classes/")) {
            return new File(path.substring("/WEB-INF/classes/".length())).getAbsolutePath();
        }
        if (path.startsWith("/")) {
            return new File("." + path).getAbsolutePath();
        }
        return new File(path).getAbsolutePath();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String arg0) {
        throw Lang.noImplement();
    }

    @Override
    public URL getResource(String name) throws MalformedURLException {
        return getClass().getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return getClass().getResourceAsStream(name);
    }

    @Override
    public Set<String> getResourcePaths(String name) {
        try {
            HashSet<String> hashSet = new HashSet<String>();
            Enumeration<URL> enumeration;
            enumeration = getClass().getClassLoader().getResources(name);
            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();
                hashSet.add(url.toString());
            }
            return hashSet;
        }
        catch (IOException e) {
            log.info("IOException", e);
            return null;
        }
    }

    @Override
    public String getServerInfo() {
        return "NutServer TestCase 1.0";
    }

    public Servlet getServlet(String name) throws ServletException {
        throw Lang.noImplement();
    }

    private String servletContextName;

    @Override
    public String getServletContextName() {
        return servletContextName;
    }

    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    public Enumeration<String> getServletNames() {
        throw Lang.noImplement();
    }

    public Enumeration<Servlet> getServlets() {
        throw Lang.noImplement();
    }

    @Override
    public void log(String arg0) {
        log.info(arg0);
    }

    public void log(Exception arg0, String arg1) {
        log.info(arg1, arg0);
    }

    @Override
    public void log(String arg0, Throwable arg1) {
        log.info(arg0, arg1);
    }

    protected Map<String, Object> attributeMap = new HashMap<String, Object>();

    @Override
    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Vector<String>(attributeMap.keySet()).elements();
    }

    @Override
    public ServletContext getContext(String arg0) {
        throw Lang.noImplement();
    }

    @Override
    public String getContextPath() {
        return "";
    }

    // =====================================================3.0

    @Override
    public int getEffectiveMajorVersion() {
        throw Lang.noImplement();
    }

    @Override
    public int getEffectiveMinorVersion() {
        throw Lang.noImplement();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        throw Lang.noImplement();
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz)
            throws ServletException {
        throw Lang.noImplement();
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz)
            throws ServletException {
        throw Lang.noImplement();
    }

    @Override
    public void addListener(String className) {
        throw Lang.noImplement();
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        throw Lang.noImplement();
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        throw Lang.noImplement();
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz)
            throws ServletException {
        throw Lang.noImplement();
    }

    @Override
    public ClassLoader getClassLoader() {
        throw Lang.noImplement();
    }

    @Override
    public void declareRoles(String... roleNames) {
        throw Lang.noImplement();
    }

    @Override
    public Dynamic addServlet(String servletName, String className) {

        return null;
    }

    @Override
    public Dynamic addServlet(String servletName, Servlet servlet) {

        return null;
    }

    @Override
    public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {

        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {

        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {

        return null;
    }

    @Override
    public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {

        return null;
    }

    @Override
    public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {

        return null;
    }

    @Override
    public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName,
                                                                Class<? extends Filter> filterClass) {

        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {

        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {

        return null;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {

        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {}

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {

        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {

        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {

        return null;
    }

    @Override
    public String getVirtualServerName() {

        return null;
    }

    /**
     * @param servletName
     * @param jspFile
     * @return
     * @see jakarta.servlet.ServletContext#addJspFile(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Dynamic addJspFile(String servletName, String jspFile) {
        return null;
    }

    /**
     * @return
     * @see jakarta.servlet.ServletContext#getSessionTimeout()
     */
    @Override
    public int getSessionTimeout() {
        return 0;
    }

    /**
     * @param sessionTimeout
     * @see jakarta.servlet.ServletContext#setSessionTimeout(int)
     */
    @Override
    public void setSessionTimeout(int sessionTimeout) {

    }

    /**
     * @return
     * @see jakarta.servlet.ServletContext#getRequestCharacterEncoding()
     */
    @Override
    public String getRequestCharacterEncoding() {
        return null;
    }

    /**
     * @param encoding
     * @see jakarta.servlet.ServletContext#setRequestCharacterEncoding(java.lang.String)
     */
    @Override
    public void setRequestCharacterEncoding(String encoding) {

    }

    /**
     * @return
     * @see jakarta.servlet.ServletContext#getResponseCharacterEncoding()
     */
    @Override
    public String getResponseCharacterEncoding() {
        return null;
    }

    /**
     * @param encoding
     * @see jakarta.servlet.ServletContext#setResponseCharacterEncoding(java.lang.String)
     */
    @Override
    public void setResponseCharacterEncoding(String encoding) {

    }

}
