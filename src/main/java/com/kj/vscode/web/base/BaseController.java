package com.kj.vscode.web.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Constants;


public abstract class BaseController implements Constants {
	private static Logger log = LoggerFactory.getLogger(BaseController.class);
	
	/**
	 * 提供查询的返回值
	 */
	public final static String SELECTED = "select";
	public final static String YES = "YES";//是
	public final static String NO = "NO";//否
	// 返回json对象的载体
	protected JsonResult jsonResult = null;
	
	protected String URL_PRE = "https://open.95516.com/open/access/1.0/";
	
	/**
     * appId
     */
    public static String APP_ID = "fcb068ab449a46e8a94634435fe9b2af";

    /**
     * 秘钥
     */
    public static String SECRET = "3e22076bdf8f450ba91bc7b6f26d8735";

    /**
     * 对称密钥
     */
    public static String SYMMETRIC_KEY = "26200db5bae60dc7a7431926239b5e5d26200db5bae60dc7";
    
    /**
     * RSA公钥
     */
    public static String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0QRJ81dxUdJNXoJwx81dvExIWP9zGhVVdYWKgOajcQI/5F1Qt67ipEL+pSh30P9roPBv6LWHb42z/htmPUrKXJ4f/WspXkbfBZsERe8XT8NZRnSdR3iZ9RqJKMzgjOetuoeFzTQ5QBalQKfQN9g58FEY0wrGH8DbrRzRImsnOVl0vvdIrqvTji+vD6GzZ8egSz9HZ0e9fQKG4dI1nuH145OfHY/fNe23oWINbXfFpVWiw+WgTTf8XzjVERD3qAT4i3cwB8RdhNlk3ysW0EJrt2/WOJiI2NNK3xzXohqPYdUDRA4aWbRPtIma5EtBcnLFm76mXwkTlk9PJm7CJA3c2QIDAQAB";

    /**
     * RSA签名私钥
     * publicKeyBase64:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCD1ROceL3MQX5q10dJr7xGRdHyKZV/UtddVGiY+Wp4o2pILcKfGZwL5+4dlY0Px7KKUBokdkdjBKkibkmdSfewWHkGPUo4jwvz+TxEwLY9jDN97gc9myEAye7KQQlOX0pn18GMkOqWa3p0tCO5vzCcEsF+UJPmLSsLgCQOgpR8cwIDAQAB
     * privateKeyBase64:MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIPVE5x4vcxBfmrXR0mvvEZF0fIplX9S111UaJj5anijakgtwp8ZnAvn7h2VjQ/HsopQGiR2R2MEqSJuSZ1J97BYeQY9SjiPC/P5PETAtj2MM33uBz2bIQDJ7spBCU5fSmfXwYyQ6pZrenS0I7m/MJwSwX5Qk+YtKwuAJA6ClHxzAgMBAAECgYBL+Rd8LPj0JSo9WCL1DBlsRgBflidZYNxMJMDCp7n8G/C79+MY3SFYmyhWVEEevu7dVpw6Nw+cuKrf9L7nriDW3o86P5J29vPfBxdjZzBrpL/xculaOrnLGwiQSaau/wJegNhQBv6L6zXPUIa/vTb6iQWFKkaMiYcP5eLj0GN6gQJBAOXkKHpZfXV6HuxrBULSsCXd8giTpjOpzPq8yBLyGX7+J+Se1HIAfy/8u5yFB2KRrBk5Z4kujl4d+ITHNqpAYFsCQQCSzfXmbSJ/tjs+4OcBQIfHiWYG/5FNoHugNf+rr0BVXLJz7g9f8NlnL3VEXou55d3PIGvqLwRM8GEIkyp2go/JAkBhG04p2FFRZUNYtCAlyN9VL3pEOzISUqBeLY4JyrAQX4U0Yg71FNaky4noJJ0o+sWKZiAg50SF5v0KAShLJCRPAkBj6W6w25e+KY0a10AA1yVRxQ4+NxNyLIXW3Mlb1FNJhWQiiGPIXQfpNSBvXpVj+LDENgGcCVG492RizJ36uBMZAkAdXQ+SK+OPZ8kFZtn8GZ55OmD44fZgC1139wo4nLJmCjg/rMGpmNT5xDkGtbVZWIu0kRzy1jAOrqtfz21t8zCc
     */
    public static String SIGN_KEY_PACKET = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIPVE5x4vcxBfmrXR0mvvEZF0fIplX9S111UaJj5anijakgtwp8ZnAvn7h2VjQ/HsopQGiR2R2MEqSJuSZ1J97BYeQY9SjiPC/P5PETAtj2MM33uBz2bIQDJ7spBCU5fSmfXwYyQ6pZrenS0I7m/MJwSwX5Qk+YtKwuAJA6ClHxzAgMBAAECgYBL+Rd8LPj0JSo9WCL1DBlsRgBflidZYNxMJMDCp7n8G/C79+MY3SFYmyhWVEEevu7dVpw6Nw+cuKrf9L7nriDW3o86P5J29vPfBxdjZzBrpL/xculaOrnLGwiQSaau/wJegNhQBv6L6zXPUIa/vTb6iQWFKkaMiYcP5eLj0GN6gQJBAOXkKHpZfXV6HuxrBULSsCXd8giTpjOpzPq8yBLyGX7+J+Se1HIAfy/8u5yFB2KRrBk5Z4kujl4d+ITHNqpAYFsCQQCSzfXmbSJ/tjs+4OcBQIfHiWYG/5FNoHugNf+rr0BVXLJz7g9f8NlnL3VEXou55d3PIGvqLwRM8GEIkyp2go/JAkBhG04p2FFRZUNYtCAlyN9VL3pEOzISUqBeLY4JyrAQX4U0Yg71FNaky4noJJ0o+sWKZiAg50SF5v0KAShLJCRPAkBj6W6w25e+KY0a10AA1yVRxQ4+NxNyLIXW3Mlb1FNJhWQiiGPIXQfpNSBvXpVj+LDENgGcCVG492RizJ36uBMZAkAdXQ+SK+OPZ8kFZtn8GZ55OmD44fZgC1139wo4nLJmCjg/rMGpmNT5xDkGtbVZWIu0kRzy1jAOrqtfz21t8zCc";

}
