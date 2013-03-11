package org.nutz.resource;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;

import org.nutz.Nutz;
import org.nutz.castor.Castors;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.ClassTools;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.impl.ErrorResourceLocation;
import org.nutz.resource.impl.FileResource;
import org.nutz.resource.impl.ResourceLocation;

/**
 * 资源扫描的帮助函数集
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Scans {

    /**
     * 在Web环境中使用Nutz的任何功能,都应该先调用这个方法,以初始化资源扫描器
     * <p/>
     * 调用一次就可以了
     */
    @SuppressWarnings("unchecked")
    public Scans init(ServletContext sc) {
        // 获取classes文件夹的路径
        String classesPath = sc.getRealPath("/WEB-INF/classes/");
        if (classesPath != null) {
            locations.add(ResourceLocation.file(new File(classesPath)));
        } else {
            if (log.isWarnEnabled())
                log.warn("/WEB-INF/classes/ NOT found?!");
        }

        // 获取lib文件夹中的全部jar
        Set<String> jars = sc.getResourcePaths("/WEB-INF/lib/");
        if (jars != null) // 这个文件夹不一定存在,尤其是Maven的WebApp项目
            for (String path : jars) {
                if (!path.toLowerCase().endsWith(".jar"))
                    continue;
                locations.add(ResourceLocation.jar(sc.getRealPath(path)));
            }
        else {
            if (log.isWarnEnabled())
                log.warn("/WEB-INF/lib/ NOT found?!");
        }
        if (log.isDebugEnabled())
            log.debug("Locations for Scans:\n" + locations);
        return this;
    }

    public List<NutResource> loadResource(String regex, String... paths) {
        List<NutResource> list = new LinkedList<NutResource>();
        for (String path : paths) {
            list.addAll(scan(path, regex));
        }
        // 如果找不到?
        if (list.size() < 1 && paths.length > 0)
            throw Lang.makeThrow(    RuntimeException.class,
                                    "folder or file like '%s' no found in %s",
                                    regex,
                                    Castors.me().castToString(paths));
        return new ArrayList<NutResource>((new HashSet<NutResource>(list)));
    }

    public void registerLocation(Class<?> klass) {
        if (klass == null)
            return;
        try {
            registerLocation(klass.getProtectionDomain().getCodeSource().getLocation());
        }
        catch (Throwable e) { // Android上会死
            String classFile = klass.getName().replace('.', '/') + ".class";
            URL url = ClassTools.getClassLoader().getResource(classFile);
            if (url != null) { // 基本上不可能为null
                String str = url.toString();
                try {
                    str = URLDecoder.decode(str, Encoding.UTF8);
                }
                catch (UnsupportedEncodingException e1) {
                    throw Lang.impossible();
                }
                str = str.substring(0, str.length() - classFile.length());
                try {
                    registerLocation(new URL(str));
                }
                catch (Throwable e2) {
                    if (log.isInfoEnabled())
                        log.info("Fail to registerLocation --> " + str, e);
                }
            }
        }
    }

    public void registerLocation(URL url) {
        if (url == null)
            return;
        locations.add(makeResourceLocation(url));
    }

    protected ResourceLocation makeResourceLocation(URL url) {
        try {
            String str = url.toString();
            if (str.endsWith(".jar")) {
                return ResourceLocation.jar(str);
            } else if (str.contains("jar!")) {
            	if (str.startsWith("jar:file:")) {
            		str = str.substring("jar:file:".length());
            	}
                return ResourceLocation.jar(str.substring(0, str.lastIndexOf("jar!") + 3));
            } else if (str.startsWith("file:")) {
                return ResourceLocation.file(new File(url.getFile()));
            } else {
                if (log.isDebugEnabled())
                    log.debug("Unkown URL " + url);
                //return ResourceLocation.file(new File(url.toURI()));
            }
        }
        catch (Throwable e) {
            if (log.isInfoEnabled())
                log.info("Fail to registerLocation --> " + url, e);
        }
        return new ErrorResourceLocation(url);
    }

    public List<NutResource> scan(String src) {
        return scan(src, null);
    }

    /**
     * 在磁盘目录或者 CLASSPATH(包括 jar) 中搜索资源
     * <p/>
     * <b>核心方法</b>
     * 
     * @param src
     *            起始路径
     * @param regex
     *            资源名需要匹配的正则表达式
     * @return 资源列表
     */
    public List<NutResource> scan(String src, String regex) {
        List<NutResource> list = new ArrayList<NutResource>();
        Pattern pattern = regex == null ? null : Pattern.compile(regex);
        // 先看看是不是文件系统上一个具体的文件
        if (src.startsWith("~/"))
            src = Disks.normalize(src);
        File srcFile = new File(src);
        if (src.startsWith("/") || srcFile.exists()) {
            if (srcFile.exists()) {
                if (srcFile.isDirectory()) {
                    Disks.visitFile(srcFile,
                                    new ResourceFileVisitor(list, src),
                                    new ResourceFileFilter(pattern));
                } else {
                    list.add(new FileResource(src, srcFile));
                }
            }
            else
                scan(src.substring(1), regex);
            //虽然已经找到一些了, 但还是扫描一些吧,这样才全!!
        }
        for (ResourceLocation location : locations) {
            location.scan(src, pattern, list);
        }
        // 如果啥都没找到,那么,用增强扫描
        if (list.isEmpty()) {
            try {
                Enumeration<URL> enu = ClassTools.getClassLoader().getResources(src);
                if (enu != null && enu.hasMoreElements()) {
                    while (enu.hasMoreElements()) {
                        try {
                            URL url = enu.nextElement();
                            ResourceLocation loc = makeResourceLocation(url);
                            if (url.toString().contains("jar!"))
                                loc.scan(src, pattern, list);
                            else
                                loc.scan("", pattern, list);
                        }
                        catch (Throwable e) {
                            if (log.isTraceEnabled())
                                log.trace("", e);
                        }
                    }
                }
            }
            catch (Throwable e) {
                if (log.isDebugEnabled())
                    log.debug("Fail to run deep scan!", e);
            }
        }
        list = new ArrayList<NutResource>((new HashSet<NutResource>(list)));
        if (log.isDebugEnabled())
            log.debugf("Found %s resource by src( %s ) , regex( %s )", list.size(), src, regex);
        return list;
    }

    public List<Class<?>> scanPackage(Class<?> classZ) {
        return scanPackage(classZ.getPackage().getName(), FLT_CLASS);
    }

    public List<Class<?>> scanPackage(Class<?> classZ, String regex) {
        return scanPackage(classZ.getPackage().getName(), regex);
    }

    /**
     * 搜索并返回给定包下所有的类（递归）
     * 
     * @param pkg
     *            包名或者包路径
     */
    public List<Class<?>> scanPackage(String pkg) {
        return scanPackage(pkg, FLT_CLASS);
    }

    /**
     * 搜索给定包下所有的类（递归），并返回所有符合正则式描述的类
     * 
     * @param pkg
     *            包名或者包路径
     * @param regex
     *            正则表达式，请注意你需要匹配的名称为 'xxxx.class' 而不仅仅是类名，从而保证选出的对象都是类文件
     */
    public List<Class<?>> scanPackage(String pkg, String regex) {
        String packagePath = pkg.replace('.', '/').replace('\\', '/');
        if (!packagePath.endsWith("/"))
            packagePath += "/";
        return rs2class(pkg, scan(packagePath, regex));
    }

    public static boolean isInJar(File file) {
        return isInJar(file.getAbsolutePath());
    }

    public static boolean isInJar(String filePath) {
        return filePath.contains(".jar!");
    }

    public static NutResource makeJarNutResource(File file) {
        return makeJarNutResource(file.getAbsolutePath());
    }

    public static NutResource makeJarNutResource(String filePath) {
        JarEntryInfo jeInfo = new JarEntryInfo(filePath);
        try {
            ZipInputStream zis = makeZipInputStream(jeInfo.getJarPath());
            ZipEntry ens = null;
            while (null != (ens = zis.getNextEntry())) {
                if (ens.isDirectory())
                    continue;
                if (jeInfo.getEntryName().equals(ens.getName())) {
                    return makeJarNutResource(jeInfo.getJarPath(), ens.getName(), "");
                }
            }
        }
        catch (IOException e) {}
        return null;
    }

    public static NutResource makeJarNutResource(    final String jarPath,
                                                    final String entryName,
                                                    final String base) throws IOException {
        NutResource nutResource = new NutResource() {

            public InputStream getInputStream() throws IOException {
                ZipInputStream zis = makeZipInputStream(jarPath);
                ZipEntry ens = null;
                while (null != (ens = zis.getNextEntry())) {
                    if (ens.getName().equals(entryName))
                        return zis;
                }
                throw Lang.impossible();
            }
            
            public int hashCode() {
            	return (jarPath + ":" + entryName).hashCode();
            }
        };
        if (entryName.equals(base))
            nutResource.setName(entryName);
        else
            nutResource.setName(entryName.substring(base.length()));
        return nutResource;
    }

    public static ZipInputStream makeZipInputStream(String jarPath) throws MalformedURLException,
            IOException {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(jarPath));
        }
        catch (IOException e) {
            zis = new ZipInputStream(new URL(jarPath).openStream());
        }
        return zis;
    }

    public static final Scans me() {
        return me;
    }

    /**
     * 将一组 NutResource 转换成 class 对象
     * 
     * @param packagePath
     *            包前缀
     * @param list
     *            列表
     * @return 类对象列表
     */
    private static List<Class<?>> rs2class(String pkg, List<NutResource> list) {
        Set<Class<?>> re = new HashSet<Class<?>>(list.size());
        if (!list.isEmpty()) {
            for (NutResource nr : list) {
                if (!nr.getName().endsWith(".class") || nr.getName().endsWith("package-info.class")) {
                    continue;
                }
                // Class快速载入
                String className = pkg + "." + nr.getName().substring(0, nr.getName().length() - 6).replaceAll("[/\\\\]", ".");
                try {
                	Class<?> klass = Lang.loadClass(className);
                    re.add(klass);
					continue;
				}
				catch (Throwable e) {}
                // 失败了? 尝试终极方法,当然了,慢多了
                InputStream in = null;
                try {
                    in = nr.getInputStream();
                    className = ClassTools.getClassName(in);
                    if (className == null) {
                        if (log.isInfoEnabled())
                            log.infof("Resource can't map to Class, Resource %s", nr);
                        continue;
                    }
                    Class<?> klass = Lang.loadClass(className);
                    re.add(klass);
                }
                catch (ClassNotFoundException e) {
                    if (log.isInfoEnabled())
                        log.infof("Resource can't map to Class, Resource %s", nr, e);
                }
                catch (IOException e) {
                    if (log.isInfoEnabled())
                        log.infof("Resource can't map to Class, Resource %s", nr, e);
                }
                finally {
                    Streams.safeClose(in);
                }
            }
        }
        return new ArrayList<Class<?>>(re);
    }

    public static class ResourceFileFilter implements FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                String fnm = f.getName().toLowerCase();
                // 忽略 SVN 和 CVS 文件,还有Git文件
                if (".svn".equals(fnm) || ".cvs".equals(fnm) || ".git".equals(fnm))
                    return false;
                return true;
            }
            if (f.isHidden())
                return false;
            return pattern == null || pattern.matcher(f.getName()).find();
        }

        private Pattern pattern;

        public ResourceFileFilter(Pattern pattern) {
            super();
            this.pattern = pattern;
        }
    }

    public static class ResourceFileVisitor implements FileVisitor {
        public void visit(File f) {
            list.add(new FileResource(base, f));
        }

        String base;
        List<NutResource> list;

        public ResourceFileVisitor(List<NutResource> list, String base) {
            super();
            this.list = list;
            this.base = base;
        }
    }

    private static final String FLT_CLASS = "^.+[.]class$";

    private static final Log log = Logs.get();

    private static final Scans me = new Scans();

    private Set<ResourceLocation> locations = new HashSet<ResourceLocation>();

    private Scans() {
        if (Lang.isAndroid) {
            if (log.isInfoEnabled())
                log.info("Running in Android , so nothing I can scan , just disable myself");
            return;
        }
        // 当前文件夹
        locations.add(ResourceLocation.file(new File(".")));
        // 推测一下nutz自身所在的位置
        registerLocation(Nutz.class);

        // 通过/META-INF/MANIFEST.MF等标记文件,获知所有jar文件的路径
        String[] referPaths = new String[]{    "META-INF/MANIFEST.MF",
                                            "log4j.properties",
                                            ".nutz.resource.mark"};
        ClassLoader cloader = ClassTools.getClassLoader();
        for (String referPath : referPaths) {
            try {
                Enumeration<URL> urls = cloader.getResources(referPath);
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    String url_str = url.toString();
                    if (url.toString().contains("jar!"))
                        url = new URL(url_str.substring(0, url_str.length()
                                                            - referPath.length()
                                                            - 2));
                    else
                        url = new URL(url_str.substring(0, url_str.length() - referPath.length()));
                    registerLocation(url);
                }
            }
            catch (IOException e) {}
        }

        // 把ClassPath也扫描一下
        try {
            String classpath = System.getProperties().getProperty("java.class.path");
            String[] paths = classpath.split(System.getProperties().getProperty("path.separator"));
            for (String pathZ : paths) {
                if (pathZ.endsWith(".jar"))
                    locations.add(ResourceLocation.jar(pathZ));
                else
                    locations.add(ResourceLocation.file(new File(pathZ)));
            }
        }
        catch (Throwable e) {
            // TODO: handle exception
        }

        if (log.isDebugEnabled())
            log.debug("Locations for Scans:\n" + locations);
    }
}
