package com.missionsky.cgarest.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.missionsky.cgarest.utils.HttpHeaderUtil;
import com.missionsky.cgarest.utils.PropertiesUtil;

@Service
public class JobService {
	
	@Autowired
	AuthorizeService authorizeService;
	
	@Autowired
	InspectionService inspectionService;
	
	Properties properties = PropertiesUtil.loadPropertiesFile("");
	
	String host = properties.getProperty("api.host");
	
	/**
	 * Get job lists for inspector
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param inspectorId
	 * @return Job list for inspector
	 */
	public String getJobs4Inspector(String appId, String agencyName, String token, String inspectorId){
		String result = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String scheduledInspections = inspectionService.getScheduledInspections4Inspector(appId, agencyName, token, inspectorId);
			JsonNode inspections = mapper.readTree(scheduledInspections).get("inspections");
			
			HashMap<String, ArrayList<JsonNode>> returnJson = new HashMap<String, ArrayList<JsonNode>>();
			HashMap<String, JsonNode> jobMap = new HashMap<String, JsonNode>();
			ArrayList<JsonNode> JobList = new ArrayList<JsonNode>();
			JsonNode record = null;
			if (inspections.isArray()) {
				for (JsonNode inspection : inspections) {
					record = inspection.get("record");
					jobMap.put(record.get("id").getTextValue(), record);
				}
			}
			for (String key : jobMap.keySet()) {
				JobList.add(jobMap.get(key));
			}
			returnJson.put("jobs", JobList);
			
			result = mapper.writeValueAsString(returnJson);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Get job lists for contractor(call api search_records[Need agency Access token]) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param stateLicenseId
	 * @return Job list for contractor
	 */
	public String getJobs4Contractor(String appId, String agencyName, String token, String stateLicenseId, Boolean isAgency){
		String result = null;
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPost httpPost = new HttpPost(host + "/v3p/search/records/");
		
		JSONObject licenseNumber = new JSONObject();
		licenseNumber.put("licenseNumber", stateLicenseId);
		JSONObject professional = new JSONObject();
		professional.put("professional", licenseNumber);
		try {
			if (isAgency) {
				httpPost.setHeaders(HttpHeaderUtil.createTokenHeaders(appId, agencyName, token));
			} else {
				ObjectMapper mapper = new ObjectMapper();
				String luckToken = mapper.readTree(authorizeService.loginAsAgency()).get("access_token").getTextValue();
				httpPost.setHeaders(HttpHeaderUtil.createTokenHeaders(properties.getProperty("agency.admin.appid"), properties.getProperty("agency.admin.agencyname"), luckToken));
			}
			
			httpPost.setEntity(new StringEntity(professional.toString()));
			
			result = EntityUtils.toString(httpClient.execute(httpPost).getEntity());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpPost.releaseConnection();
		}
		
		return result;
	}
	
	/**
	 * Get job details for inspector/contractor(call api get_record) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param recordId
	 * @return Job details info
	 */
	public String getJobDetails(String appId, String agencyName, String token, String recordId){
		String result = null;
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(host + "/v3/records/" + recordId);
		try {
			httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(appId, agencyName, token));
			
			result = EntityUtils.toString(httpClient.execute(httpGet).getEntity());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		
		return result;
	}
}
