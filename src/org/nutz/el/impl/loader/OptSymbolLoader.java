package org.nutz.el.impl.loader;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.util.List;

import org.nutz.el.El;
import org.nutz.el.ElOperator;
import org.nutz.el.ElSymbol;
import org.nutz.el.ElSymbolType;
import org.nutz.el.ann.OptHidden;
import org.nutz.el.opt.AbstractOperator;
import org.nutz.lang.Lang;
import org.nutz.resource.Scans;

public class OptSymbolLoader extends AbstractSymbolLoader {

	private OptNode root;

	private OptNode cursor;

	@SuppressWarnings("unchecked")
	public OptSymbolLoader() {
		root = new OptNode();
		// 自动加载操作符成为一棵排序过的树
		List<Class<?>> optTypes = Scans.me().scanPackage(AbstractOperator.class);
		for (Class<?> optType : optTypes) {
			if (!Modifier.isAbstract(optType.getModifiers())
				&& ElOperator.class.isAssignableFrom(optType)
				&& null == optType.getAnnotation(OptHidden.class)) {

				Class<? extends ElOperator> theType = (Class<? extends ElOperator>) optType;
				ElOperator optObj = El.opt(theType);
				char[] cs = optObj.getString().toCharArray();
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
