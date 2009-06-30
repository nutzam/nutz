package org.nutz.java;

public class JavaConstructor extends JavaBodyMethod {

	public JavaConstructor(JavaClass javaClass) {
		super();
		this.setName(javaClass.getName());
		this.setReturnType(javaClass);
	}

	@Override
	public JavaMethod setReturnType(JavaType returnType) {
		if (this.isReturnVoid())
			super.setReturnType(returnType);
		return this;
	}

	@Override
	protected StringBuilder createMethodHead() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getModifierString());
		sb.append(getName()).append(" (");
		if (getParams().size() > 0) {
			for (JavaParam p : getParams())
				sb.append(p.renderSource()).append(", ");
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(")");
		return sb;
	}

}
