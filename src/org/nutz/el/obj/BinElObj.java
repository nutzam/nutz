package org.nutz.el.obj;

import org.nutz.el.ElException;
import org.nutz.el.ElObj;
import org.nutz.el.ElOperator;
import org.nutz.el.ElValue;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;

/**
 * 二叉树节点表达式对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class BinElObj implements ElObj {

	private BinElObj parent;

	private ElObj left;

	private ElObj right;

	private ElOperator operator;

	public boolean isRoot() {
		return null == parent;
	}

	public boolean hasNoLeft() {
		return null == left;
	}

	public boolean hasNoOperator() {
		return null == operator;
	}

	public boolean hasNoRight() {
		return null == right;
	}

	public BinElObj getParent() {
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

	public BinElObj setLeft(ElObj left) {
		this.left = left;
		if (left instanceof BinElObj)
			((BinElObj) left).parent = this;
		return this;
	}

	public BinElObj setRight(ElObj right) {
		this.right = right;
		if (right instanceof BinElObj)
			((BinElObj) right).parent = this;
		return this;
	}

	public BinElObj setOperator(ElOperator opt) {
		this.operator = opt;
		return this;
	}

	public boolean isLeftOnly() {
		return null != left && null == operator && null == right;
	}

	public BinElObj append(ElOperator opt, ElObj obj) {
		// 没有操作符，直接添加
		if (null == operator) {
			return setOperator(opt).setRight(obj);
		}

		// 否则计算操作符优先级别
		BinElObj nn = new BinElObj();
		// 高权 － 新节点下沉
		if (opt.isHigherThan(operator)) {
			// 递归下沉
			if (right instanceof BinElObj) {
				setRight(((BinElObj) right).append(opt, obj));
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
			BinElObj on = this;
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
		if (null == operator) {
			if (null != left)
				return left.eval(context);
			else if (null != right) {
				return right.eval(context);
			} else {
				throw new ElException("Empty BinElObj");
			}
		}
		return operator.execute(context, left, right);
	}

	public ElValue[] evalArray(Context context) {
		throw new ElException("ElNode don't support this method!");
	}

	public ElObj unwrap() {
		if (null == operator && left != null) {
			if (null != right)
				throw new ElException("BinElObj without operator, but it has right!");
			return left;
		}
		return this;
	}

	public BinElObj unwrapToBin() {
		if (null == operator && left != null) {
			if (null != right)
				throw new ElException("BinElObj without operator, but it has right!");
			if (left instanceof BinElObj)
				return (BinElObj) left;
		}
		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendString(sb, 0);
		return sb.toString();
	}

	public void appendString(StringBuilder sb, int indent) {
		sb.append("OPT  : ").append(operator);

		sb.append('\n');
		appendIndent(sb, indent);
		sb.append("LEFT : ");
		appendObj(sb, indent, left);
		appendIndent(sb, indent);

		sb.append('\n');
		appendIndent(sb, indent);
		sb.append("RIGHT: ");
		appendObj(sb, indent, right);
	}

	private void appendObj(StringBuilder sb, int indent, ElObj obj) {
		if (obj instanceof BinElObj) {
			((BinElObj) obj).appendString(sb, indent + 1);
		} else {
			appendIndent(sb, indent);
			sb.append(null == obj ? "<null>" : obj.toString());
		}
	}

	private static void appendIndent(StringBuilder sb, int indent) {
		sb.append(Strings.dup("   ", indent));
	}

}
