package com.kj.vscode.web.base;

import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {
	public void addInterceptors(InterceptorRegistry registry) {
		// 多个拦截器组成一个拦截器链
		// addPathPatterns 用于添加拦截规则
		// excludePathPatterns 用户排除拦截
		registry.addInterceptor(
				(HandlerInterceptor) new SessionCheckInterceptor())
				.addPathPatterns("/**")
				// 放过的请求路径
				.excludePathPatterns(
						Arrays.asList("/", "/asserts/**", "/user"));
		;
		WebMvcConfigurer.super.addInterceptors(registry);
	}

}
