package com.kj.vscode.web.base;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 异常拦截
 * 
 * @author wuxh
 */
@ControllerAdvice
public class ExceptionInterceptor {
		
	private static Logger log = LoggerFactory.getLogger(SessionCheckInterceptor.class);
	
	// 拦截所有的Exception，进入错误界面
	@ExceptionHandler(Exception.class)
	public String exceptionHandler(Model model, Exception e) {
		log.info("拦截异常", e);
		StackTraceElement[] els = e.getStackTrace();
		StringBuilder sb = new StringBuilder(e.toString());
		sb.append("\r\n");
		for (int i = 0; i < els.length; i++) {
			sb.append("\tat ");
			sb.append(els[i].toString());
			sb.append("\r\n");
		}
		e.printStackTrace();
		
		model.addAttribute("exception",e);
		model.addAttribute("exceptionStack",sb);
		
		return "base/error";
	}

	// 拦截SessionException，进入错误界面
	@ExceptionHandler(SessionException.class)
	public String sessionCheckHandler(HttpServletRequest request, Exception e)
			throws Exception {
		log.info("拦截会话异常", e);
		request.setAttribute("message", e.getMessage());
		request.setAttribute("redirectTo", "/");
		return "base/redirect";
		
	}
}
