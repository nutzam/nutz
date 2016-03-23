package org.nutz.rpc.json;

public class JsonRpcException extends RuntimeException {

	private static final long serialVersionUID = 8487538528601909009L;

	public JsonRpcException() {
		super();
	}

	public JsonRpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonRpcException(String message) {
		super(message);
	}

	public JsonRpcException(Throwable cause) {
		super(cause);
	}

}
