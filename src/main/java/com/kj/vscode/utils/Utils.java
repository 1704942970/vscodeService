package com.kj.vscode.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import javax.ws.rs.core.MediaType;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
	private static Client c = null;
	private static Client secureClient =null;
	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	public static String DATE_FORMAT = "yyyy-MM-dd";
	/**
	 * 初始化设置
	 */
	static{
		initalizationJersyClient();
		initalizationSecureJersyClient();
	}
	/**
	 * 设置调用参数
	 */
	private static void initalizationJersyClient(){
		try {
			c = Client.create();
			c.setFollowRedirects(true);
			c.setConnectTimeout(10000);
			c.setReadTimeout(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void initalizationSecureJersyClient(){
		try {
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {}
				public void checkServerTrusted(
				java.security.cert.X509Certificate[] chain,
				String authType) throws CertificateException {}
			} }, new SecureRandom());
			HostnameVerifier hv = new HostnameVerifier(){
		        public boolean verify(String urlHostName, SSLSession session){
		            return true;
		        }
		    };
		    
	        HTTPSProperties prop = new HTTPSProperties(hv, context);
	        DefaultClientConfig dcc = new DefaultClientConfig();
	        dcc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, prop);
			secureClient = Client.create(dcc);
			secureClient.setFollowRedirects(true);
			secureClient.setConnectTimeout(10000);
			secureClient.setReadTimeout(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 调用接口
	 */
	public static String sendPOSTRequest(String url, Object map, String contentTpye){
		if(null==c){
			initalizationJersyClient();
		}
		Client client = null;
		if(url.indexOf("https://") == 0){
			if(null==secureClient){
				initalizationJersyClient();
			}
			client = secureClient;
		}else{
			if(null==c){
				initalizationJersyClient();
			}
			client = c;
		}
		WebResource resource = client.resource(url);
		String resultStr = null;
		try {
			Builder builder = resource.accept("*/*");
			ClientResponse res = builder.type(contentTpye).entity(map).post(ClientResponse.class);
			if(res.getStatus() != 200){
				throw new Exception("url:"+url+",response code:" + res.getStatus());
			}
			resultStr = res.getEntity(String.class);
			return resultStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 参数 转为json格式
	 * @param url 调用地址
	 * @param map 设置的参数
	 * @return
	 */
	public static String sendPostGson(String url, Map<String, String> map){
		String ps = gson.toJson(map);

		System.out.println("生成加密后的报文:"+ps);
		return sendPOSTRequest(url, ps, MediaType.APPLICATION_JSON);
	}

	
	/**
	 * 使用公钥加密对称密钥
	 * @param publicKey	公钥
	 * @param symmetricKeyByte	对称密钥字节
	 * @return	加密后的对称密钥字节
	 * @throws Exception
	 */
	public static byte[] encrypt(String publicKey, byte[] symmetricKeyByte) throws Exception {
		byte[] encodedKey = Base64.decodeBase64(publicKey);
		KeySpec keySpec = new X509EncodedKeySpec(encodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey pk = keyFactory.generatePublic(keySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, pk);
		byte[] result = cipher.doFinal(symmetricKeyByte);
		return result;
	}

	/**
	 * 签名加密后的数据装载进map
	 * @param key  对称秘钥
	 * @param params 待加密的字符串
	 * @param encryptKeys 加密字段 
	 * @throws Exception
	 */
	public static void encryptedParamMap(String key, Map<String, String> params, String ... encryptKeys) throws Exception{
		if(encryptKeys != null && encryptKeys.length > 0){
			for(String encryptKey : encryptKeys){
				params.put(encryptKey, getEncryptedValue(params.get(encryptKey), key));
			}
		}
	}

	/**
	 * 3DES加密
	 * @param value	待加密的字符串
	 * @param key	加密密钥
	 * @return	加密后的字符串
	 * @throws Exception
	 */
	public static String getEncryptedValue(String value, String key) throws Exception {
		if (null == value || "".equals(value)) { 
			return ""; 
		} 
		byte[] valueByte = value.getBytes(); 
		byte[] sl = encrypt3DES(valueByte, hexToBytes(key)); 
		String result = Base64.encodeBase64String(sl); 
		// String result = BytesUtil.bytesToHex(sl); 
		return result; 
	}

	/**
	 * 解密
	 * @param value 待解密的字符串
	 * @param key 解密秘钥
	 * @return 解密后字符串
	 * @throws Exception
	 */
	public static String getDecryptedValue(String value, String key) throws Exception {
		if (null == value || "".equals(value)) {
			return "";
		}
		byte[] valueByte = Base64.decodeBase64(value);
		byte[] sl = decrypt3DES(valueByte, hexStr2Bytes(key));
		String result = new String(sl);
		return result;
	}

	/**
	 * 3DES解密
	 * @param input
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt3DES(byte[] input, byte[] key) throws Exception {
		Cipher c = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "DESede"));
		return c.doFinal(input);
	}


	/**
	 * bytes字符串转换为Byte值
	 * @param src String Byte字符串，每个Byte之间没有分隔符(字符范围:0-9 A-F)
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src){
		/*对输入值进行规范化整理*/
		src = src.trim().replace(" ", "").toUpperCase(Locale.US);
		//处理值初始化
		int m=0,n=0;
		int iLen=src.length()/2; //计算长度
		byte[] ret = new byte[iLen]; //分配存储空间

		for (int i = 0; i < iLen; i++){
			m=i*2+1;
			n=m+1;
			ret[i] = (byte)(Integer.decode("0x"+ src.substring(i*2, m) + src.substring(m,n)) & 0xFF);
		}
		return ret;
	}

	/**
	 * 3DES加密
	 * @param input	待加密的字节
	 * @param key	密钥
	 * @return	加密后的字节
	 * @throws Exception
	 */
	private static byte[] encrypt3DES(byte[] input, byte[] key) throws Exception { 
		Cipher c = Cipher.getInstance("DESede/ECB/PKCS5Padding"); 
		c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "DESede")); 
		return c.doFinal(input); 
	}

	/**
	 * 获取随机字符串
	 * @return	随机字符串
	 */
	public static String createNonceStr(){
		String sl = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < 16 ; i ++){
			sb.append(sl.charAt(new Random().nextInt(sl.length())));
		}
		return sb.toString();
	}

	/**
	 * h获取32位随机数字字符串
	 * @return
	 */
	public static String createNonceNumber(){
		String sl = "0123456789";
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < 32 ; i ++){
			sb.append(sl.charAt(new Random().nextInt(sl.length())));
		}
		return sb.toString();
	}
	/**
	 * 获取当前日期时间
	 *
	 * @return
	 */
	public static String getCurrentDateTime(String Dateformat) {
		String datestr = null;
		SimpleDateFormat df = new SimpleDateFormat(Dateformat);
		datestr = df.format(new Date());
		return datestr;
	}

	/**
	 * 在输入日期上增加（+）或减去（-）天数
	 *
	 * @param date   输入日期
	 */
	public static Date addDay(Date date, int iday) {
		Calendar cd = Calendar.getInstance();

		cd.setTime(date);

		cd.add(Calendar.DAY_OF_MONTH, iday);

		return cd.getTime();
	}
	/**
	 * 将日期格式日期转换为字符串格式 自定義格式
	 *
	 * @param date
	 * @param dateformat
	 * @return
	 */
	public static String dateToString(Date date, String dateformat) {
		String datestr = null;
		SimpleDateFormat df = new SimpleDateFormat(dateformat);
		datestr = df.format(date);
		return datestr;
	}

	/**
	 * 将字符串日期转换为日期格式
	 *
	 * @param datestr
	 * @return
	 */
	public static Date stringToDate(String datestr) throws Exception{

		if (datestr == null || datestr.equals("")) {
			return null;
		}
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		try {
			date = df.parse(datestr);
		} catch (ParseException e) {
			date = stringToDate(datestr, "yyyyMMdd");
		}
		return date;
	}

	/**
	 * 将字符串日期转换为日期格式
	 * 自定義格式
	 *
	 * @param datestr
	 * @return
	 */
	public static Date stringToDate(String datestr, String dateformat) throws Exception{
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat(dateformat);
			date = df.parse(datestr);
		return date;
	}
	/**
	 * 签名
	 * @param param	待签名的参数
	 * @param signKey	签名密钥
	 * @return	签名结果字符串
	 * @throws Exception
	 */
	public static String sign(Map<String, String> param, String signKey) throws Exception {
		String value = sortMap(param);
		byte[] keyBytes = Base64.decodeBase64(signKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyf.generatePrivate(keySpec);
		Signature signature = Signature.getInstance("SHA256WithRSA");
		signature.initSign(priKey);
		signature.update(value.getBytes());
		byte[] signed = signature.sign();
		String result = Base64.encodeBase64String(signed);
		return result;
	}
	


	/**
	 * 排序
	 * @param param	待排序的参数
	 * @return	排序结果字符串
	 */
	public static String sortMap(Map<String, String> param){
		StringBuilder result = new StringBuilder();
		Collection<String> keySet = param.keySet();
		List<String> list = new ArrayList<String>(keySet);
		Collections.sort(list);
		for (int i = 0; i < list.size(); ++i) {
			String key = list.get(i);
			if("symmetricKey".equals(key)){
				continue;
			}
//			非空字段需要参与签名
//			if(param.get(key) == null || "".equals(param.get(key).trim())){
//				continue;
//			}
			result.append(key).append("=").append(param.get(key)).append("&");
		}
		return result.substring(0, result.length() - 1);
	}

	
	 public static String sha256(byte[] data) { 
		try { 
				MessageDigest md = MessageDigest.getInstance("SHA-256"); 
				return bytesToHex(md.digest(data)); 
			
			} catch (Exception ex) { 
			
				return null; 
			} 
		}

	/** 
	* 将byte数组转换成16进制字符串 
	* 
	* @param bytes 
	* @return 16进制字符串 
	*/ 
	public static String bytesToHex(byte[] bytes) {
		String hexArray = "0123456789abcdef";
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			int bi = b & 0xff;
			sb.append(hexArray.charAt(bi >> 4));
			sb.append(hexArray.charAt(bi & 0xf));
		}
		return sb.toString();
	}

	 public static byte[] hexToBytes(String hex) {
	        return hexToBytes(hex.toCharArray());
	    }

    public static byte[] hexToBytes(char[] hex) {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127) {
                value -= 256;
            }
            raw[i] = (byte) value;
        }
        return raw;
    }

	public static String createSign(Map<String, String> params){
		StringBuilder sb = new StringBuilder();
		// 将参数以参数名的字典升序排序
		Map<String, String> sortParams = new TreeMap<String, String>(params);
		// 遍历排序的字典,并拼接"key=value"格式
		for (Map.Entry<String, String> entry : sortParams.entrySet()) {
			String key = entry.getKey();
			String value =  entry.getValue().trim();
			if (null != value && !"".equals(value))
				sb.append("&").append(key).append("=").append(value);
		}
		String stringA = sb.toString().replaceFirst("&","");
		String stringSignTemp = stringA ;
		String signValue = sha256(stringSignTemp.getBytes());
		return signValue;
	}




	/**
	 * 签名
	 * @param param	待签名的参数
	 * @param sign      	签名结果字符串
	 * @return	签名结果
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public static boolean verify(Map<String, String> param, String sign,String rescissionPublicKey) throws Exception {
		String value = sortMap(param);
		byte[] keyBytes = Base64.decodeBase64(rescissionPublicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PublicKey pubkey = keyf.generatePublic(keySpec);
		Signature signature = Signature.getInstance("SHA256WithRSA");
		signature.initVerify(pubkey);
		signature.update(value.getBytes());
		boolean result = signature.verify(Base64.decodeBase64(sign.getBytes()));
		return result;
	}

}
