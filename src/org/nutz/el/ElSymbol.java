package org.nutz.el;

public class ElSymbol {

	private ElSymbolType type;

	private Object obj;

	public ElSymbolType getType() {
		return type;
	}

	public ElSymbol setType(ElSymbolType type) {
		this.type = type;
		return this;
	}

	public ElSymbol setObj(Object obj) {
		this.obj = obj;
		return this;
	}

	public String getString() {
		return (String) obj;
	}

	public Integer getInteger() {
		return (Integer) obj;
	}

	public Long getLong() {
		return (Long) obj;
	}

	public Float getFloat() {
		return (Float) obj;
	}

	public Boolean getBoolean() {
		return (Boolean) obj;
	}

	public ElOperator getOperator() {
		return (ElOperator) obj;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		switch (type) {
		case LEFT_PARENTHESIS:
			sb.append("(");
			break;
		case RIGHT_PARENTHESIS:
			sb.append(")");
			break;
		case LEFT_BRACKET:
			sb.append("[");
			break;
		case RIGHT_BRACKET:
			sb.append("]");
			break;
		case COMMA:
			sb.append(",");
			break;
		case LONG:
			sb.append(obj.toString()).append('L');
			break;
		case OPT:
		case BOOL:
		case FLOAT:
		case INT:
		case VAR:
			sb.append(obj.toString());
			break;
		case STRING:
			sb.append("'" + obj + "'");
			break;
		case NULL:
			sb.append("null");
			break;
		case UNDEFINED:
			sb.append("undefined");
			break;
		default:
			throw new ElException("Unexpect symbol type '%s'", type.name());
		}
		return sb.toString();
	}

}
