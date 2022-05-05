package com.kj.vscode.web.base;

/**
 * 请求响应Json格式数据，需以此对象进行返回
 * @author wuxh
 *
 */
public class JsonResult {
	
	public final static String SUCCESS = "SUCCESS";//处理成功
	public final static String FAILED = "FAILED";//处理失败
	
	/**
	 * 响应码
	 */
	private String code;
	
	/**
	 * 描述，可存放exception的message
	 */
	private String msg;
	
	/**
	 * 返回数据
	 */
	private Object data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
