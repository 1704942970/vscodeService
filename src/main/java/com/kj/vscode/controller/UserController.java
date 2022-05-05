package com.kj.vscode.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.kj.vscode.utils.Utils;
import com.kj.vscode.web.base.JsonResult;
import com.kj.vscode.web.base.PageControllerSupport;

@RequestMapping("user")
@Controller
public class UserController extends PageControllerSupport {

	private static Logger log = LoggerFactory.getLogger(UserController.class);
	
	// 获取getUserMobile
	@RequestMapping("getUserMobile")
	@ResponseBody
	public JsonResult getUserMobile(HttpServletRequest request, String code) throws Exception {
		jsonResult = new JsonResult();
		
		try {
			
			String backendToken = getBackendToken();
			
			Map<String, String > result = getAccessToken(backendToken, code);
			
			String userMobile = getUserMobile(backendToken, result.get("accessToken"), result.get("openId"));
			
			jsonResult.setCode(JsonResult.SUCCESS);
			jsonResult.setData(userMobile);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			jsonResult.setCode(JsonResult.FAILED);
			jsonResult.setMsg(e.getMessage());
			jsonResult.setData(e);
		}
		
		return jsonResult;
	}
		
	private String getBackendToken() {

		//调用地址
		String url = URL_PRE + "backendToken";
		//拼装参数Map
		Map<String, String> params = new HashMap<String, String>(0);
		params.put("appId", APP_ID);
		params.put("secret", SECRET);

		//生成随即字符串
		String nonceStr = Utils.createNonceStr();
		params.put("nonceStr", nonceStr);
		
		//生成时间戳
		String timestamp = String.valueOf(System.currentTimeMillis()/1000);
		params.put("timestamp", timestamp);

		String value =  Utils.sortMap(params);//排序之后拼接
		String signature = Utils.sha256(value.getBytes());//进行加密签名
		params.put("signature", signature);
		//仅用于加密，不上送字段
		params.remove("secret");
		String param = new Gson().toJson(params);
	    
		System.out.println("生成入参和签名："+param);
		
		//发送请求
		String resultStr = Utils.sendPostGson(url, params);
		System.out.println("返回值："+resultStr);
		JSONObject jo = JSONObject.parseObject(resultStr);
        String str1 = jo.get("params").toString();
        JSONObject jo1 = JSONObject.parseObject(str1);
        String backendToken = jo1.get("backendToken").toString();
        
		return backendToken;
	}
	
	private Map<String,String> getAccessToken(String backendToken, String code){
		//调用地址
		String url = URL_PRE + "token";

		//拼装参数Map
		Map<String, String> params = new HashMap<String, String>(0);
		params.put("appId", APP_ID);
		params.put("backendToken", backendToken);
		//登录获取的code
		params.put("code", code);

		//接入方直接填写常量字符串authorization_code
		params.put("grantType", "authorization_code");

		//发送请求
		String resultStr = Utils.sendPostGson(url, params);
		System.out.println("获取accessToken和openId:"+resultStr);
		
		System.out.println("返回值："+resultStr);
		JSONObject jo = JSONObject.parseObject(resultStr);
        String str1 = jo.get("params").toString();
        JSONObject jo1 = JSONObject.parseObject(str1);
        
        String accessToken = jo1.get("accessToken").toString();
        String openId = jo1.get("openId").toString();
        
        Map<String, String> result= new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("openId", openId);
        
		return result;
	}
	
	private String getUserMobile(String backendToken, String accessToken, String openId){
		//调用接口地址
		String url = "https://open.95516.com/open/access/1.0/user.mobile";
		
		//拼装参数Map
		Map<String, String> params = new HashMap<String, String>(0);
		 
		params.put("appId", APP_ID);
		params.put("accessToken", accessToken);
		params.put("openId", openId);
		params.put("backendToken", backendToken);

		//发送请求
		String resultStr = Utils.sendPostGson(url, params);
		
		System.out.println("-------获取用户敏感信息-登录手机号:"+resultStr+"---------");
		
		return resultStr;
	}
}
