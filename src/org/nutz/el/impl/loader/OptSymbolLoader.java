package org.nutz.el.impl.loader;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.util.List;

import org.nutz.el.ElException;
import org.nutz.el.ElOperator;
import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;
import org.nutz.el.ann.Opt;
import org.nutz.el.ann.Weight;
import org.nutz.el.opt.AbstractOperator;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.resource.Scans;

public class OptSymbolLoader extends AbstractSymbolLoader {

	private OptNode root;

	private OptNode cursor;

	public OptSymbolLoader() {
		root = new OptNode();
		// 自动加载操作符成为一棵排序过的树
		List<Class<?>> optTypes = Scans.me().scanPackage(AbstractOperator.class);
		for (Class<?> optType : optTypes) {
			if (!Modifier.isAbstract(optType.getModifiers())
				&& ElOperator.class.isAssignableFrom(optType)) {
				Opt opt = optType.getAnnotation(Opt.class);
				Weight wei = optType.getAnnotation(Weight.class);

				if (null != opt && null != wei) {
					String optValue = opt.value();
					if (Strings.isBlank(optValue)) {
						throw Lang.makeThrow(	ElException.class,
												"Operator type '%s' lack Annoation '@Opt'",
												optType.getName());
					}
					try {
						ElOperator optObj = (ElOperator) optType.newInstance();
						optObj.setWeight(wei.value());
						optObj.setString(opt.value());
						char[] cs = optValue.toCharArray();
						OptNode on = root;
						for (char c : cs) {
							on = on.addNode(c);
						}
						// 检查节点是否是空节点
						if (on.getOperator() != null) {
							throw Lang.makeThrow(	"Operator '%s' and '%s' has duplicate '@Opt'",
													on.getOperator().getClass().getName(),
													optType.getName());
						}
						// 记录操作符对象
						on.setOperator(optObj);
					}
					catch (Exception e) {
						throw new ElException(e);
					}
				} else {
					// 没有声明 @Opt 和 @Weight 的操作符实现类，会被忽略
				}
			}
		}

	}

	public boolean isMyTurn(ElSymbol prev, int c) {
		if (prev != null && prev.getType() == ElSymbolType.OPT)
			return false;
		cursor = root.getChild((char) c);
		return null != cursor;
	}

	public int load(Reader reader) throws IOException {
		int c;
		while (-1 != (c = reader.read())) {
			OptNode on = cursor.getChild((char) c);
			if (null == on) {
				break;
			} else {
				cursor = on;
			}
		}
		symbol = new ElSymbol().setType(ElSymbolType.OPT).setObj(cursor.getOperator());
		return c;
	}

}
