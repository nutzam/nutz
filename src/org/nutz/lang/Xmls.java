package org.nutz.lang;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.nutz.lang.util.Callback2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    /**
     * 快捷的解析 XML 文件的帮助方法
     * 
     * @param xmlFile
     *            XML 文件
     * @return Document 对象
     */
    public static Document xml(File xmlFile) {
        try {
            return xmls().parse(xmlFile);
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
        return Strings.sNull(Strings.trim(sub.getTextContent()), "");
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
    public static void eachChildren(Element ele, String regex, final Each<Element> callback, int off) {
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
}
