package com.missionsky.cgarest.utils;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class HttpHeaderUtil {
	
	public final static String JSON_CONTENT_TYPE = "application/json";
	
	public final static String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
	
	public final static String AUTH_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	
	public final static String AUTH_ACCEPT_ENCODING = "gzip,deflate,sdch";
	
	public final static String AUTH_ACCEPT_LANGUAGE = "en-US,en;q=0.8,en-US;q=0.6,en;q=0.4";
	
	public final static String AUTH_CACHE_CONTROL = "max-age=0";
	
	public final static String AUTH_CONNECTION = "keep-alive";
	
	public final static String AUTH_HOST = "auth.accela.com";
	
	public final static String AUTH_ORIGIN = "https://auth.accela.com";
	
	public final static String AUTH_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36";
	
	public static Header[] createAuthHeaders() {
		Header[] headers = new BasicHeader[9];
		headers[0] = new BasicHeader("Accept", AUTH_ACCEPT);
		headers[1] = new BasicHeader("Accept-Encoding", AUTH_ACCEPT_ENCODING);
		headers[2] = new BasicHeader("Accept-Language", AUTH_ACCEPT_LANGUAGE);
		headers[3] = new BasicHeader("Cache-Control", AUTH_CACHE_CONTROL);
		headers[4] = new BasicHeader("Connection", AUTH_CONNECTION);
		headers[5] = new BasicHeader("Content-Type", FORM_CONTENT_TYPE);
		headers[6] = new BasicHeader("Host", AUTH_HOST);
		headers[7] = new BasicHeader("Origin", AUTH_ORIGIN);
		headers[8] = new BasicHeader("User-Agent", AUTH_USER_AGENT);
		return headers;
	}
	
	public static Header[] createTokenHeaders(String appId, String agencyName, String token){
		Header[] headers = new BasicHeader[4];
		headers[0] = new BasicHeader("Content-Type", JSON_CONTENT_TYPE);
		headers[1] = new BasicHeader("Accept", JSON_CONTENT_TYPE);
		headers[2] = new BasicHeader("x-accela-appid", appId);
		headers[3] = new BasicHeader("x-accela-agency", agencyName);
		headers[3] = new BasicHeader("Authorization", token);
		return headers;
	}
	
	
	
	

}
