package com.missionsky.cgarest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.missionsky.cgarest.utils.HttpHeaderUtil;
import com.missionsky.cgarest.utils.PropertiesUtil;

/**
 * Authorize Service
 * @author ellis.xie 
 * @version 1.0
 */
@Service
public class AuthorizeService {
	
	private static Logger logger = LoggerFactory.getLogger(AuthorizeService.class);
	
	Properties properties = PropertiesUtil.loadPropertiesFile("");
	
	/**
	 * login
	 * @param loginName
	 * @param password
	 * @param appId
	 * @param appSecret
	 * @param appType
	 * @param appName
	 * @param agencyName
	 * @return Access token and refresh token
	 */
	public String login(String loginName, String password, String appId, String appSecret, String appType, String appName, String agencyName){
		
		String env = properties.getProperty("global.env");
		String state = UUID.randomUUID().toString();
		
		CookieStore cookieStore = new BasicCookieStore();
		HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
		
		String authCode = null;
		String tokenJson = null;
		
		String url = properties.getProperty("auth.addr");
		String authParams =
				"client_id=" + appId
				+ "&agency_name=" + agencyName
				+ "&environment=" + env
				+ "&redirect_uri=" + properties.getProperty("auth.redirect.addr")
				+ "&state=" + state
				+ "&scope=" + (appType.toLowerCase().equals("agency")?properties.getProperty("auth.agency.scope"):properties.getProperty("auth.citizen.scope"))
				+ "&response_type=code";
		
		HttpPost auth1Post = new HttpPost(url);
		auth1Post.addHeader("Content-Type", HttpHeaderUtil.FORM_CONTENT_TYPE);
		
		HttpPost loginPost = new HttpPost(properties.getProperty("auth.login.addr"));
		
		HttpPost tokenPost = new HttpPost(properties.getProperty("auth.token.addr"));
		
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair("AppId", appId));
		data.add(new BasicNameValuePair("AppType", appType));
		data.add(new BasicNameValuePair("AppSecret", null));
		data.add(new BasicNameValuePair("AppName", appName));
		data.add(new BasicNameValuePair("LoginName", loginName));
		data.add(new BasicNameValuePair("Password", password));
		data.add(new BasicNameValuePair("Agency", agencyName));
		data.add(new BasicNameValuePair("EnvironmentName", env));
		if (appType.toLowerCase().equals("agency")) {
			data.add(new BasicNameValuePair("btnAgencyLogin", "Sign In"));
		} else {
			data.add(new BasicNameValuePair("btnCivicLogin", "Sign In"));
		}
		
		try {
			auth1Post.setEntity(new StringEntity(authParams));
			httpClient.execute(auth1Post);
			
			loginPost.setEntity(new UrlEncodedFormEntity(data));
			loginPost.setHeaders(HttpHeaderUtil.createAuthHeaders());
			loginPost.setHeader("Referer", properties.getProperty("auth.login.addr"));
			
			HttpResponse loginResponse = httpClient.execute(loginPost);
			
			String loginResult = EntityUtils.toString(loginResponse.getEntity());
			
			if (loginResult.startsWith(state + "=")) {
				authCode = loginResult.replace(state + "=", "");
			} else {
				Document loginDoc =  Jsoup.parse(loginResult);
				
				if (loginDoc.getElementsByAttributeValue("action", "/civic/login?returnUrl=%2Foauth2%2Fauthorize").isEmpty()) {
					StringBuffer cookieHeader = new StringBuffer();
					List<Cookie> cookies = cookieStore.getCookies();
					String rvTokenCookie = null;
					String rvTokenPage = null;
					for (Cookie cookie : cookies) {
						cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
						if (cookie.getName().equals("__RequestVerificationToken")) {
							rvTokenCookie = cookie.getValue();
							rvTokenPage = loginDoc.getElementById("form").getElementsByAttributeValue("name", "__RequestVerificationToken").get(0).val();
						}
					}
					
					if (rvTokenCookie != null) {
						HttpPost auth2Post = new HttpPost(properties.getProperty("auth.allow.addr"));
						
						data.clear();
						data.add(new BasicNameValuePair("__RequestVerificationToken", rvTokenPage));
						data.add(new BasicNameValuePair("IsApproved", "true"));
						data.add(new BasicNameValuePair("client_id", appId));
						data.add(new BasicNameValuePair("agency_name", agencyName));
						data.add(new BasicNameValuePair("environment", env));
						data.add(new BasicNameValuePair("redirect_uri", properties.getProperty("auth.redirect.addr")));
						data.add(new BasicNameValuePair("state", state));
						data.add(new BasicNameValuePair("scope", (appType.toLowerCase().equals("agency")?properties.getProperty("auth.agency.scope"):properties.getProperty("auth.citizen.scope"))));
						data.add(new BasicNameValuePair("response_type", "code"));
						
						auth2Post.setEntity(new UrlEncodedFormEntity(data));
						auth2Post.setHeaders(HttpHeaderUtil.createAuthHeaders());
						auth2Post.setHeader("Cookie", cookieHeader.toString());
						if (appType.toLowerCase().equals("agency")) {
							auth2Post.setHeader("Referer", properties.getProperty("auth.addr") + "?agency_name="+ agencyName.toLowerCase() +"&environment=" + env);
						} else {
							auth2Post.setHeader("Referer", properties.getProperty("auth.addr"));
						}
						
						HttpResponse authPostResponse = httpClient.execute(auth2Post);
						authCode = EntityUtils.toString(authPostResponse.getEntity()).replace(state + "=", "");
					}
				} else {
					tokenJson = "{\"status\":\"error\",\"message\": \"Invalid user name or password.\"}";
				}
				
			}
			if (tokenJson == null) {
				if (authCode == null) {
					tokenJson = "{\"status\":\"error\",\"message\": \"cannot get authorization code.\"}";
				} else {
					tokenPost.setHeader("x-accela-appid", appId);
					tokenPost.setHeader("Content-Type", HttpHeaderUtil.JSON_CONTENT_TYPE);
					String tokenState = UUID.randomUUID().toString();
					String params = "client_id=" + appId
							+ "&client_secret=" + appSecret
							+ "&grant_type=authorization_code"
							+ "&redirect_uri=" + properties.getProperty("auth.redirect.addr")
							+ "&code=" + authCode
							+ "&state=" + tokenState
							+ "&agency_name=" + agencyName;
					tokenPost.setEntity(new StringEntity(params));
					HttpResponse response = httpClient.execute(tokenPost);
					tokenJson = EntityUtils.toString(response.getEntity());
				}
			}
		} catch (IOException e) {
			//TODO
			logger.error(e.getMessage());
		} finally {
			auth1Post.releaseConnection();
			loginPost.releaseConnection();
			tokenPost.releaseConnection();
		}
		
		return tokenJson;
	}
	
	/**
	 * login as agency user
	 * @return Agency access token and refresh token
	 */
	public String loginAsAgency(){
		return login(properties.getProperty("agency.admin.loginname"), 
				properties.getProperty("agency.admin.password"), 
				properties.getProperty("agency.admin.appid"), 
				properties.getProperty("agency.admin.appsecret"), 
				"Agency", 
				properties.getProperty("agency.admin.appname"),
				properties.getProperty("agency.admin.agencyname"));
	}

}
