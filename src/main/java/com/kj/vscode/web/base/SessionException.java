package com.kj.vscode.web.base;

public class SessionException extends Exception {

	/**
	 * session 检查Exception
	 */
	private static final long serialVersionUID = 1L;
	
	public SessionException() {
	}
	public SessionException(String message, Throwable cause) {
		super(message,cause);
	}
}
