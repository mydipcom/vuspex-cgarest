package com.missionsky.cgarest.service;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.missionsky.cgarest.utils.HttpHeaderUtil;
import com.missionsky.cgarest.utils.PropertiesUtil;

@Service
public class PermitService {
	
	Properties properties = PropertiesUtil.loadPropertiesFile("");
	
	String host = properties.getProperty("api.host");
	
	/**
	 * Get addresses of permit(call api ) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param recordId
	 * @return Permit addresses
	 */
	public String getPermitAddresses(String appId, String agencyName, String token, String recordId) {
		String result = null;
		
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			HttpGet httpGet = new HttpGet(host + "/v3/records/" + recordId + "/addresses");
			
			httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(appId, agencyName, token));
			
			result = EntityUtils.toString(httpClient.execute(httpGet).getEntity());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Get parcels of permit(call api ) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param recordId
	 * @return Permit parcels
	 */
	public String getPermitParcels(String appId, String agencyName, String token, String recordId) {
		String result = null;
		
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			HttpGet httpGet = new HttpGet(host + "/v3p/records/" + recordId + "/parcels");
			
			httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(appId, agencyName, token));
			
			result = EntityUtils.toString(httpClient.execute(httpGet).getEntity());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * Get owners of permit for inspector(call api ) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param recordId
	 * @return Permit owners
	 */
	public String getPermitOwners4Inspector(String appId, String agencyName, String token, String recordId) {
		String result = null;
		
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			HttpGet httpGet = new HttpGet(host + "/v3/records/" + recordId + "/owners");
			
			httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(appId, agencyName, token));
			
			result = EntityUtils.toString(httpClient.execute(httpGet).getEntity());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Get owners of permit for contractor(call api ) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param recordId
	 * @return Permit owners
	 */
	public String getPermitOwners4Contractor(String appId, String agencyName, String token, String recordId) {
		String result = null;
		
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			HttpGet httpGet = new HttpGet(host + "/v3p/records/" + recordId + "/owners");
			
			httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(appId, agencyName, token));
			
			result = EntityUtils.toString(httpClient.execute(httpGet).getEntity());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	
}
