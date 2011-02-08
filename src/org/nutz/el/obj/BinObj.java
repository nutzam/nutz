package org.nutz.el.obj;

import org.nutz.el.ElException;
import org.nutz.el.ElObj;
import org.nutz.el.ElOperator;
import org.nutz.el.ElValue;
import org.nutz.el.opt.AccessOperator;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;

public class BinObj implements ElObj {

	private BinObj parent;

	private ElObj left;

	private ElObj right;

	private ElOperator operator;

	public boolean isRoot() {
		return null == parent;
	}

	public BinObj getParent() {
		return parent;
	}

	public ElObj getLeft() {
		return left;
	}

	public ElObj getRight() {
		return right;
	}

	public ElOperator getOperator() {
		return operator;
	}

	public BinObj setLeft(ElObj left) {
		this.left = left;
		if (left instanceof BinObj)
			((BinObj) left).parent = this;
		return this;
	}

	public BinObj setRight(ElObj right) {
		this.right = right;
		if (right instanceof BinObj)
			((BinObj) right).parent = this;
		return this;
	}

	public BinObj setOperator(ElOperator opt) {
		this.operator = opt;
		return this;
	}

	public boolean isAccessOperation() {
		return null != operator && (operator instanceof AccessOperator);
	}

	public BinObj append(ElOperator opt, ElObj obj) {
		BinObj nn = new BinObj();
		// 高权 － 新节点下沉
		if (opt.isHigherThan(operator)) {
			// 递归下沉
			if (right instanceof BinObj) {
				setRight(((BinObj) right).append(opt, obj));
			}
			// 下沉一级
			else {
				nn.setLeft(right).setOperator(opt).setRight(obj);
				setRight(nn);
			}
		}
		// 同权或者低权 － 新节点上升
		else {
			// 寻找到一个权重不比新节点权重高的祖先节点
			BinObj on = this;
			while (on.parent != null) {
				on = on.parent;
				if (!on.getOperator().isHigherThan(opt))
					break;
			}
			nn.setLeft(on).setOperator(opt).setRight(obj);
		}

		// 总是返回新创建的节点
		return nn;
	}

	public ElValue eval(Context context) {
		return operator.execute(context, left, right);
	}

	public ElValue[] evalArray(Context context) {
		throw new ElException("ElNode don't support this method!");
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendString(sb, 0);
		return sb.toString();
	}

	public void appendString(StringBuilder sb, int indent) {
		sb.append("<").append(operator).append(">");

		sb.append('\n');
		appendIndent(sb, indent);
		sb.append("LEFT:");
		appendObj(sb, indent, left);
		appendIndent(sb, indent);

		sb.append('\n');
		appendIndent(sb, indent);
		sb.append("RIGHT:");
		appendObj(sb, indent, right);
	}

	private void appendObj(StringBuilder sb, int indent, ElObj obj) {
		if (obj instanceof BinObj) {
			((BinObj) obj).appendString(sb, indent + 1);
		} else {
			appendIndent(sb, indent);
			sb.append(null == obj ? "<null>" : obj.toString());
		}
	}

	private static void appendIndent(StringBuilder sb, int indent) {
		sb.append(Strings.dup("   ", indent));
	}

}
