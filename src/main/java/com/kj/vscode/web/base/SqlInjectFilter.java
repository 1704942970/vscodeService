package com.kj.vscode.web.base;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sql注入过滤器
 */
@WebFilter(urlPatterns = "/userCertBuild/buildP12FileForNoLogin", filterName = "sqlInjectFilter")
public class SqlInjectFilter implements Filter{
	
	private static Logger log = LoggerFactory.getLogger(SqlInjectFilter.class);
	
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Map parametersMap = request.getParameterMap();
        Iterator it = parametersMap.entrySet().iterator();
        
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String[] value = (String[]) entry.getValue();
            for (int i = 0; i < value.length; i++) {
            	log.debug("sql过滤检查值：" + value[i]);
                if (null != value[i] && containsSqlInjection(value[i])) {
                    log.info("您输入的参数("+value[i]+")有非法字符，请输入正确的参数！");
                    String message =java.net.URLEncoder.encode("您输入的参数有非法字符，请输入正确的参数！");
                    //request.setAttribute("message", "您输入的参数("+value[i]+")有非法字符，请输入正确的参数！");
                   //request.getRequestDispatcher("/toCertBuildForNoLoginPage").forward(request, response);
                    ((HttpServletResponse) response).sendRedirect("/toCertBuildForNoLoginPage?message=" + message);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
        
    }

    @Override
    public void destroy() {

    }
    
    /**
	 * 是否含有sql注入，返回true表示含有
	 * @param obj
	 * @return
	 */
	public boolean containsSqlInjection(Object obj){
	    Pattern pattern= Pattern.compile("\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|'|%)");
	    Matcher matcher=pattern.matcher(obj.toString());
	    boolean b = matcher.find();
	    return b;
	}
}
