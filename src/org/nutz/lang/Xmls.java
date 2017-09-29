package org.nutz.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.nutz.lang.util.Callback2;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.Tag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML 的快捷帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Xmls {

    /**
     * 帮你快速获得一个 DocumentBuilder，方便 XML 解析。
     * 
     * @return 一个 DocumentBuilder 对象
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder xmls() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    
    public static Document xml(InputStream ins) {
        return xml(ins, null);
    }

    /**
     * 快捷的解析 XML 文件的帮助方法，它会主动关闭输入流
     * 
     * @param ins
     *            XML 文件输入流
     * @return Document 对象
     */
    public static Document xml(InputStream ins, Charset charset) {
        try {
            if (charset == null)
                charset = Encoding.CHARSET_UTF8;
            return xmls().parse(new InputSource(new InputStreamReader(ins, charset)));
        }
        catch (SAXException e) {
            throw Lang.wrapThrow(e);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        catch (ParserConfigurationException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Streams.safeClose(ins);
        }
    }

    public static Document xml(File xmlFile) {
        return xml(xmlFile, null);
    }
    
    /**
     * 快捷的解析 XML 文件的帮助方法
     * 
     * @param xmlFile
     *            XML 文件
     * @return Document 对象
     */
    public static Document xml(File xmlFile, Charset charset) {
        InputStream ins = null;
        try {
            ins = new FileInputStream(xmlFile);
            return xml(ins, charset);
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 获取某元素下某节点的全部文本内容（去掉左右空白）
     * 
     * @param ele
     *            元素
     * @param subTagName
     *            子元素名
     * @return 内容，null 表示子元素不存在, 空串表示元素没有对应文本内容
     */
    public static String get(Element ele, String subTagName) {
        Element sub = firstChild(ele, subTagName);
        if (null == sub)
            return null;
        return getText(sub);
    }

    public static String getText(Element ele) {
        StringBuilder sb = new StringBuilder();
        joinText(ele, sb);
        return Strings.trim(sb);
    }

    public static void joinText(Element ele, StringBuilder sb) {
        if (null == ele)
            return;
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node nd = nl.item(i);
            switch (nd.getNodeType()) {
            case Node.TEXT_NODE:
                sb.append(nd.getNodeValue());
                break;
            case Node.CDATA_SECTION_NODE:
                sb.append(nd.getNodeValue());
                break;
            case Node.ELEMENT_NODE:
                joinText((Element) nd, sb);
                break;
            default:
                break;
            }
        }
    }

    /**
     * 获取 XML 元素内第一个子元素
     * 
     * @param ele
     *            XML 元素
     * @return 子元素，null 表示不存在
     */
    public static Element firstChild(Element ele) {
        final Element[] tag = new Element[1];
        eachChildren(ele, null, new Each<Element>() {
            public void invoke(int index, Element cld, int length) {
                tag[0] = cld;
                Lang.Break();
            }
        });
        return tag[0];
    }

    /**
     * 获取 XML 元素内第一个名字所有符合一个正则表达式的子元素
     * 
     * @param ele
     *            XML 元素
     * @param regex
     *            元素名称正则表达式
     * @return 子元素，null 表示不存在
     */
    public static Element firstChild(Element ele, String regex) {
        final Element[] tag = new Element[1];
        eachChildren(ele, regex, new Each<Element>() {
            public void invoke(int index, Element cld, int length) {
                tag[0] = cld;
                Lang.Break();
            }
        });
        return tag[0];
    }

    /**
     * 从一个 XML 元素开始，根据一条 XPath 获取一个元素
     * 
     * @param ele
     *            XML 元素
     * @param xpath
     *            要获取的元素的 XPath
     * @return 元素，null 表示不存在
     */
    public static Element getEle(Element ele, String xpath) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xp = factory.newXPath();
        try {
            XPathExpression expression = xp.compile(xpath);
            return (Element) expression.evaluate(ele, XPathConstants.NODE);
        }
        catch (XPathExpressionException e) {
            throw Lang.wrapThrow(e);
        }

    }

    /**
     * 从某个元素里获取一个指定下标的子元素
     * 
     * @param ele
     *            XML 元素
     * @param index
     *            子元素下标（0 base）
     * @return 子元素
     */
    public static Element getChild(Element ele, int index) {
        return getChild(ele, index, null);
    }

    /**
     * 从某个元素里获取一个指定下标且指定名称的子元素
     * 
     * @param ele
     *            XML 元素
     * @param index
     *            子元素下标（0 base）
     * @param regex
     *            元素名称的正则表达式
     * @return 子元素
     */
    public static Element getChild(Element ele, final int index, String regex) {
        final int pos = index;
        final Element[] tag = new Element[1];
        eachChildren(ele, null, new Each<Element>() {
            public void invoke(int index, Element cld, int length) {
                if (index >= pos) {
                    tag[0] = cld;
                    Lang.Break();
                }
            }
        });
        return tag[0];
    }

    /**
     * 获取 XML 元素内最后一个子元素
     * 
     * @param ele
     *            XML 元素
     * @return 子元素，null 表示不存在
     */
    public static Element lastChild(Element ele) {
        final Element[] tag = new Element[1];
        eachChildren(ele, null, new Each<Element>() {
            public void invoke(int index, Element cld, int length) {
                tag[0] = cld;
                Lang.Break();
            }
        }, -1);
        return tag[0];
    }

    /**
     * 获取 XML 元素内最后一个名字所有符合一个正则表达式的子元素
     * 
     * @param ele
     *            XML 元素
     * @param regex
     *            元素名称正则表达式
     * @return 子元素，null 表示不存在
     */
    public static Element lastChild(Element ele, String regex) {
        final Element[] tag = new Element[1];
        eachChildren(ele, regex, new Each<Element>() {
            public void invoke(int index, Element cld, int length) {
                tag[0] = cld;
                Lang.Break();
            }
        }, -1);
        return tag[0];
    }

    /**
     * 获取 XML 元素内所有子元素
     * 
     * @param ele
     *            XML 元素
     * @return 一个子元素的列表
     */
    public static List<Element> children(Element ele) {
        return children(ele, null);
    }

    /**
     * 获取 XML 元素内名字符合一个正则表达式的元素
     * 
     * @param ele
     *            XML 元素
     * @param regex
     *            元素名称正则表达式
     * @return 一个子元素的列表
     */
    public static List<Element> children(Element ele, String regex) {
        final List<Element> list = new ArrayList<Element>(ele.getChildNodes().getLength());
        eachChildren(ele, regex, new Each<Element>() {
            public void invoke(int index, Element cld, int length) {
                list.add(cld);
            }
        });
        return list;
    }

    /**
     * 迭代 XML 元素内所有子元素
     * 
     * @param ele
     *            XML 元素
     * @param callback
     *            回调
     */
    public static void eachChildren(Element ele, Each<Element> callback) {
        eachChildren(ele, null, callback);
    }

    /**
     * 迭代 XML 元素内名字符合一个正则表达式的子元素
     * 
     * @param ele
     *            XML 元素
     * @param regex
     *            元素名称正则表达式
     * @param callback
     *            回调
     */
    public static void eachChildren(Element ele, String regex, final Each<Element> callback) {
        Xmls.eachChildren(ele, regex, callback, 0);
    }

    /**
     * 判断某个元素下是否有子元素
     * 
     * @param ele
     *            元素
     * @param regex
     *            子元素名称的正则表达式，如果为 null，则元素内如果有任意元素都会返回 false
     * @return 是否有子元素
     */
    public static boolean hasChild(Element ele, String regex) {
        NodeList nl = ele.getChildNodes();
        int len = nl.getLength();
        for (int i = 0; i < len; i++) {
            Node nd = nl.item(i);
            if (nd instanceof Element) {
                if (null == regex)
                    return false;
                if (((Element) nd).getTagName().matches(regex))
                    return true;
            }
        }
        return false;
    }

    /**
     * 迭代 XML 元素内名字符合一个正则表达式的子元素
     * 
     * @param ele
     *            XML 元素
     * @param regex
     *            元素名称正则表达式
     * @param callback
     *            回调
     * @param off
     *            偏移量。0 表示从第一个迭代。 -1 表示从最后一个迭代。-2表示从倒数第二个迭代
     */
    public static void eachChildren(Element ele,
                                    String regex,
                                    final Each<Element> callback,
                                    int off) {
        if (null == ele || null == callback)
            return;

        // 正则式
        final Pattern p = null == regex ? null : Pattern.compile(regex);

        NodeList nl = ele.getChildNodes();

        // 循环子
        final int len = nl.getLength();

        // 每次循环执行
        Callback2<Integer, Node> eachInvoke = new Callback2<Integer, Node>() {
            public void invoke(Integer index, Node nd) {
                if (nd instanceof Element)
                    try {
                        Element tag = (Element) nd;
                        if (null == p || p.matcher(tag.getTagName()).find())
                            callback.invoke(index, tag, len);
                    }
                    catch (ExitLoop e) {
                        throw Lang.wrapThrow(e);
                    }
                    catch (ContinueLoop e) {}
                    catch (LoopException e) {
                        throw Lang.wrapThrow(e);
                    }
            }
        };

        try {
            // 负向迭代
            if (off < 0) {
                for (int i = len + off; i >= 0; i--) {
                    eachInvoke.invoke(i, nl.item(i));
                }
            }
            // 正向迭代
            else {
                for (int i = off; i < len; i++) {
                    eachInvoke.invoke(i, nl.item(i));
                }
            }
        }
        catch (ExitLoop e) {}
        catch (RuntimeException e) {
            if (e.getCause() instanceof ExitLoop)
                return;
            else
                throw e;
        }
    }

    /**
     * 获取该 XML 元素内所有的属性的值，按照Map的形式返回
     * 
     * @param ele
     *            XML 元素
     * @return 所有属性的值
     */
    public static Map<String, String> getAttrs(Element ele) {
        NamedNodeMap nodeMap = ele.getAttributes();
        Map<String, String> attrs = new HashMap<String, String>(nodeMap.getLength());
        for (int i = 0; i < nodeMap.getLength(); i++) {
            attrs.put(nodeMap.item(i).getNodeName(), nodeMap.item(i).getNodeValue());
        }
        return attrs;
    }

    /**
     * 从 XML 元素中得到指定属性的值，如该指定属性不存在，则返回Null
     * 
     * @param ele
     *            XML 元素
     * @return 该指定属性的值
     */
    public static String getAttr(Element ele, String attrName) {
        Node node = ele.getAttributes().getNamedItem(attrName);
        return node != null ? node.getNodeValue() : null;
    }

    /**
     * 根据一个 XML 节点，将其变成一个 Map。
     * <p/>
     * <b>注意: 不支持混合节点</b>
     * 
     * @param ele
     *            元素
     * 
     * @return 一个 Map 对象
     */
    public static NutMap asMap(Element ele) {
        return asMap(ele, false);
    }

    /**
     * 根据一个 XML 节点，将其变成一个 Map。
     * <p/>
     * <b>注意: 不支持混合节点</b>
     * 
     * @param ele
     *            元素
     * @param lowFirst
     *            是否把所有key的首字母都小写
     * 
     * @return 一个 Map 对象
     */
    public static NutMap asMap(Element ele, final boolean lowFirst) {
        return asMap(ele, lowFirst, false);
    }
    public static NutMap asMap(Element ele, final boolean lowFirst, final boolean dupAsList) {
        return asMap(ele, lowFirst, dupAsList, null);
    }
    public static NutMap asMap(Element ele, final boolean lowerFirst, final boolean dupAsList, final List<String> alwaysAsList) {
        final NutMap map = new NutMap();
        eachChildren(ele, new Each<Element>() {
            public void invoke(int index, Element _ele, int length)
                    throws ExitLoop, ContinueLoop, LoopException {
                String key = _ele.getNodeName();
                if (lowerFirst)
                    key = Strings.lowerFirst(key);
                Map<String, Object> tmp = asMap(_ele, lowerFirst, dupAsList, alwaysAsList);
                if (!tmp.isEmpty()) {
                    if (alwaysAsList != null && alwaysAsList.contains(key)) {
                        map.addv2(key, tmp);
                    }
                    else if (dupAsList) {
                        map.addv(key, tmp);
                    }
                    else {
                        map.setv(key, tmp);
                    }
                    return;
                }
                String val = getText(_ele);
                if (!Strings.isBlank(val)) {
                    if (alwaysAsList != null && alwaysAsList.contains(key)) {
                        map.addv2(key, map);
                    }
                    else if (dupAsList)
                        map.addv(key, val);
                    else
                        map.setv(key, val);
                }
            }
        });
        return map;
    }

    /**
     * 将一个下面格式的 XML:
     * 
     * <pre>
     * &lt;xml&gt;
     * &lt;key1&gt;value1&lt;/key1&gt;
     * &lt;key2&gt;value2&lt;/key2&gt;
     * &lt;/xml&gt;
     * </pre>
     * 
     * 转换成一个 Map
     * 
     * @param xml
     *            XML 字符串
     * 
     * @return Map
     */
    public static NutMap xmlToMap(String xml) {
        return Xmls.asMap(Xmls.xml(Lang.ins(xml)).getDocumentElement());
    }
    
    public static NutMap xmlToMap(InputStream ins) {
        return Xmls.asMap(Xmls.xml(ins).getDocumentElement());
    }
    
    public static NutMap xmlToMap(InputStream ins, final boolean lowerFirst, final boolean dupAsList, final List<String> alwaysAsList) {
        return Xmls.asMap(Xmls.xml(ins).getDocumentElement(), lowerFirst, dupAsList, alwaysAsList);
    }

    /**
     * 将一个 Map 转换成 XML 类似:
     * 
     * <pre>
     * &lt;xml&gt;
     * &lt;key1&gt;value1&lt;/key1&gt;
     * &lt;key2&gt;value2&lt;/key2&gt;
     * &lt;/xml&gt;
     * </pre>
     * 
     * @param map
     *            Map
     * @return XML 字符串
     */
    public static String mapToXml(Map<String, Object> map) {
        return mapToXml("xml", map);
    }
    
    public static String mapToXml(String root, Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        map2Tag(root, map).toXml(sb, 0);
        return sb.toString();
    }
    
    protected static Tag map2Tag(String rootName, Map<String, Object> map) {
        Tag rootTag = Tag.tag(rootName);
        for (Map.Entry<String, Object> en : map.entrySet()) {
            String key = en.getKey();
            Object val = en.getValue();
            List<Tag> children = obj2tag(key, val);
            for (Tag child : children) {
                rootTag.add(child);
            }
        }
        return rootTag;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<Tag> obj2tag(String nodeName, Object val) {
        List<Tag> tags = new ArrayList<Tag>();
        if (null == val)
            return tags;
        if (val instanceof Map) {
            tags.add(map2Tag(nodeName, (Map<String, Object>) val));
        } else if (val instanceof Collection) {
            for (Object object : (Collection)val) {
                for (Tag tag : obj2tag(nodeName, object)) {
                    tags.add(tag);
                }
            }
        } else {
            tags.add(Tag.tag(nodeName).setText(val.toString()));
        }
        return tags;
    }
    
    /**
     * 从一个 XML 元素开始，根据一条 XPath 获取一组元素
     * 
     * @param ele
     *            XML 元素
     * @param xpath
     *            要获取的元素的 XPath
     * @return 元素列表
     */
    public static List<Element> getEles(Element ele, String xpath) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xp = factory.newXPath();
        try {
            XPathExpression expression = xp.compile(xpath);
            NodeList nodes = (NodeList) expression.evaluate(ele, XPathConstants.NODESET);
            List<Element> list = new ArrayList<Element>();
            int len = nodes.getLength();
            for (int i = 0; i < len; i++) {
                Node node = nodes.item(i);
                if (node instanceof Element) {
                    list.add((Element)node);
                }
            }
            return list;
        }
        catch (XPathExpressionException e) {
            throw Lang.wrapThrow(e);
        }
    }
    
    public static String HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
}
