package org.nutz.ioc.loader.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 使用XML做为Ioc配置文件 <br/>
 * 限制: <br/>
 * <li>必须是良构的XML文件 <li> <li>obj必须定义type,当前实现中IocObject是共享的 <li>
 * 
 * @author wendal(wendal1985@gmail.com)
 * @version 2.0
 */
public class XmlIocLoader implements IocLoader {

	private static final Log LOG = Logs.getLog(XmlIocLoader.class);

	private Map<String, IocObject> iocMap = new LinkedHashMap<String, IocObject>();

	private Map<String, String> parentMap = new TreeMap<String, String>();

	public static final String TAG_OBJ = "obj";
	public static final String TAG_ARGS = "args";
	public static final String TAG_FIELD = "field";

	/**
	 * 仅用于标示内部obj的id,内部obj声明的id将被忽略 <br/>
	 * 该设计基于内部obj也可以使用继承顶层的obj
	 */
	private static int innerId;

	public XmlIocLoader(String... fileNames) {
		try {
			DocumentBuilder builder = Lang.xmls();
			Document document;
			for (String fileName : fileNames) {
				document = builder.parse(Files.findFileAsStream(fileName));
				document.normalizeDocument();
				NodeList nodeListZ = ((Element) document.getDocumentElement()).getChildNodes();
				for (int i = 0; i < nodeListZ.getLength(); i++) {
					if (nodeListZ.item(i) instanceof Element)
						paserBean((Element) nodeListZ.item(i), false);
				}
			}
			handleParent();
			if (LOG.isDebugEnabled())
				LOG.debugf("Load complete :\n%s", Json.toJson(iocMap));
		}
		catch (Throwable e) {
			e.printStackTrace();
			throw Lang.makeThrow("Error");
		}
	}

	public String[] getName() {
		return iocMap.keySet().toArray(new String[iocMap.keySet().size()]);
	}

	public boolean has(String name) {
		return iocMap.containsKey(name);
	}

	public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
		if (has(name))
			return iocMap.get(name);
		throw new ObjectLoadException("Object '" + name + "' without define!");
	}

	private IocObject paserBean(Element beanElement, boolean innerBean) throws Throwable {
		String beanId;
		if (innerBean) {
			beanId = "inner$" + innerId;
			innerId++;
		} else
			beanId = beanElement.getAttribute("name");
		if (beanId == null)
			throw Lang.makeThrow("No name for one bean!");
		if (iocMap.containsKey(beanId))
			throw Lang.makeThrow("Name of bean is not unique! name=" + beanId);

		if (LOG.isDebugEnabled())
			LOG.debugf("Resolving bean define, name = %s", beanId);
		IocObject iocObject = new IocObject();
		String beanType = beanElement.getAttribute("type");
		if (!Strings.isBlank(beanType))
			iocObject.setType(Class.forName(beanType));
		String beanScope = beanElement.getAttribute("scope");
		if (!Strings.isBlank(beanScope))
			iocObject.setScope(beanScope);
		String beanParent = beanElement.getAttribute("parent");
		if (!Strings.isBlank(beanParent))
			parentMap.put(beanId, beanParent);

		parseArgs(beanElement, iocObject);
		parseFields(beanElement, iocObject);
		parseEvents(beanElement, iocObject);

		iocMap.put(beanId, iocObject);
		if (LOG.isDebugEnabled())
			LOG.debugf("Resolved bean define, name = %s", beanId);
		return iocObject;
	}

	private void parseArgs(Element beanElement, IocObject iocObject) throws Throwable {
		NodeList argsNodeList = beanElement.getElementsByTagName(TAG_ARGS);
		if (argsNodeList.getLength() > 0) {
			Element argsElement = (Element) argsNodeList.item(0);
			NodeList argNodeList = argsElement.getChildNodes();
			for (int i = 0; i < argNodeList.getLength(); i++) {
				if (argNodeList.item(i) instanceof Element)
					iocObject.addArg(parseX((Element) argNodeList.item(i)));
			}
		}
	}

	private void parseFields(Element beanElement, IocObject iocObject) throws Throwable {
		NodeList fieldNodeList = beanElement.getElementsByTagName(TAG_FIELD);
		if (fieldNodeList.getLength() > 0) {
			int len = fieldNodeList.getLength();
			for (int i = 0; i < len; i++) {
				Element fieldElement = (Element) fieldNodeList.item(i);
				IocField iocField = new IocField();
				iocField.setName(fieldElement.getAttribute("name"));
				if (fieldElement.hasChildNodes()) {
					NodeList nodeList = fieldElement.getChildNodes();
					for (int j = 0; j < nodeList.getLength(); j++) {
						if (nodeList.item(j) instanceof Element) {
							iocField.setValue(parseX((Element) nodeList.item(j)));
							break;
						}
					}
				}
				iocObject.addField(iocField);
			}
		}
	}

	static final String STR_TAG = "str";
	static final String ARRAY_TAG = "array";
	static final String MAP_TAG = "map";
	static final String ITEM_TAG = "item";
	static final String LIST_TAG = "list";
	static final String SET_TAG = "list";
	static final String OBJ_TAG = "obj";
	static final String INT_TAG = "int";
	static final String SHORT_TAG = "short";
	static final String LONG_TAG = "long";
	static final String FLOAT_TAG = "float";
	static final String DOUBLE_TAG = "double";
	static final String BOOLEAN_TAG = "bool";
	static final String REFER_TAG = "refer";
	static final String JAVA_TAG = IocValue.TYPE_JAVA;
	static final String FILE_TAG = IocValue.TYPE_FILE;
	static final String EVN_TAG = IocValue.TYPE_ENV;
	static final String JNDI_TAG = IocValue.TYPE_JNDI;
	static final String SYS_TAG = IocValue.TYPE_SYS;

	private IocValue parseX(Element element) throws Throwable {
		IocValue iocValue = new IocValue();
		String type = element.getNodeName();
		if (EVN_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(EVN_TAG);
			iocValue.setValue(element.getTextContent());
		} else if (SYS_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(SYS_TAG);
			iocValue.setValue(element.getTextContent());
		} else if (JNDI_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(JNDI_TAG);
			iocValue.setValue(element.getTextContent());
		} else if (JAVA_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(JAVA_TAG);
			iocValue.setValue(element.getTextContent());
		} else if (REFER_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(REFER_TAG);
			iocValue.setValue(element.getTextContent());
		} else if (FILE_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(FILE_TAG);
			iocValue.setValue(element.getTextContent());
		} else if (OBJ_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(null);
			iocValue.setValue(paserBean(element, true));
		} else if (MAP_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(null);
			iocValue.setValue(paserMap(element));
		} else if (LIST_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(null);
			iocValue.setValue(paserCollection(element));
		} else if (ARRAY_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(null);
			iocValue.setValue(paserCollection(element).toArray());
		} else if (SET_TAG.equalsIgnoreCase(type)) {
			iocValue.setType(null);
			Set<Object> set = new HashSet<Object>();
			set.addAll(paserCollection(element));
			iocValue.setValue(set);
		} else {
			iocValue.setType(null);
			iocValue.setValue(element.getFirstChild().getTextContent());
		}
		return iocValue;
	}

	private List<IocValue> paserCollection(Element element) throws Throwable {
		List<IocValue> list = new ArrayList<IocValue>();
		if (element.hasChildNodes()) {
			NodeList nodeList = element.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node instanceof Element) {
					list.add((IocValue) parseX((Element) node));
				}
			}
		}
		return list;
	}

	private Map<String, ?> paserMap(Element element) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (element.hasChildNodes()) {
			NodeList nodeList = element.getElementsByTagName(ITEM_TAG);
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i) instanceof Element) {
					Element elementItem = (Element) nodeList.item(i);
					String key = elementItem.getAttribute("key");
					if (map.containsKey(key))
						throw new IllegalArgumentException("key is not unique!");
					NodeList list = elementItem.getChildNodes();
					for (int j = 0; j < list.getLength(); j++) {
						if (list.item(j) instanceof Element) {
							map.put(key, list.item(j).getTextContent());
							break;
						}
					}
					if (!map.containsKey(key))
						map.put(key, null);
				}
			}
		}
		return map;
	}

	private void parseEvents(Element beanElement, IocObject iocObject) {
		NodeList eventsNodeList = beanElement.getElementsByTagName("events");
		if (eventsNodeList.getLength() > 0) {
			Element eventsElement = (Element) eventsNodeList.item(0);
			IocEventSet iocEventSet = new IocEventSet();
			NodeList fetchNodeList = eventsElement.getElementsByTagName("fetch");
			if (fetchNodeList.getLength() > 0)
				iocEventSet.setFetch(((Element) fetchNodeList.item(0)).getTextContent());
			NodeList createNodeList = eventsElement.getElementsByTagName("create");
			if (createNodeList.getLength() > 0)
				iocEventSet.setCreate(((Element) createNodeList.item(0)).getTextContent());
			NodeList deposeNodeList = eventsElement.getElementsByTagName("depose");
			if (deposeNodeList.getLength() > 0)
				iocEventSet.setDepose(((Element) deposeNodeList.item(0)).getTextContent());
			if (iocEventSet.getCreate() == null)
				if (iocEventSet.getDepose() == null)
					if (iocEventSet.getFetch() == null)
						return;
			iocObject.setEvents(iocEventSet);
		}
	}

	private void handleParent() {
		// 检查parentId是否都存在.
		for (String parentId : parentMap.values())
			if (!iocMap.containsKey(parentId))
				throw Lang.makeThrow("发现无效的parent=%s", parentId);
		// 检查循环依赖
		List<String> parentList = new ArrayList<String>();
		for (Entry<String, String> entry : parentMap.entrySet()) {
			if (!check(parentList, entry.getKey()))
				throw Lang.makeThrow("发现循环依赖! bean id=%s", entry.getKey());
			parentList.clear();
		}
		while (parentMap.size() != 0) {
			Iterator<Entry<String, String>> it = parentMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				String beanId = entry.getKey();
				String parentId = entry.getValue();
				if (parentMap.get(parentId) == null) {
					IocObject newIocObject = Iocs.mergeWith(iocMap.get(beanId),
															iocMap.get(parentId));
					iocMap.put(beanId, newIocObject);
					it.remove();
				}
			}
		}
	}

	private boolean check(List<String> parentList, String currentBeanId) {
		if (parentList.contains(currentBeanId))
			return false;
		String parentBeanId = parentMap.get(currentBeanId);
		if (parentBeanId == null)
			return true;
		parentList.add(currentBeanId);
		return check(parentList, parentBeanId);
	}
}
