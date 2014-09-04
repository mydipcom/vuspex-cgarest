package com.missionsky.cgarest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.missionsky.cgarest.utils.HttpHeaderUtil;
import com.missionsky.cgarest.utils.PropertiesUtil;

/**
 * Inspection Service 
 * @author ellis.xie
 * @version 1.0
 */
@Service
public class InspectionService {
	
	@Autowired
	private AuthorizeService authorizeService;
	
	@Autowired
	private JobService jobService;
	
	Properties properties = PropertiesUtil.loadPropertiesFile("");
	
	String host = properties.getProperty("api.host");
	
	/**
	 * Get scheduled inspections for inspector(call api get_inspections) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param inspectorId
	 * @return Scheduled inspections
	 */
	public String getScheduledInspections4Inspector(String appId, String agencyName, String token, String inspectorId){
		String result = null;
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(host + "/v3/inspections?offset=0&limit=100&inspectorIds=" + inspectorId.toUpperCase());
		
		try {
			httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(appId, agencyName, token));
			String response = EntityUtils.toString(httpClient.execute(httpGet).getEntity());
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode inspections = mapper.readTree(response).get("inspections");
			
			HashMap<String, ArrayList<JsonNode>> returnJson = new HashMap<String, ArrayList<JsonNode>>();
			ArrayList<JsonNode> inspectionList = new ArrayList<JsonNode>();
			
			if (inspections.isArray()) {
				for (JsonNode inspection : inspections) {
					String status = inspection.get("status").get("type").getTextValue().toLowerCase();
					if (status.equals("scheduled") || status.equals("pending")) {
						inspectionList.add(inspection);
					}
				}
			}
			returnJson.put("inspections", inspectionList);
			
			result = mapper.writeValueAsString(returnJson);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
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
	
	/**
	 * Get scheduled inspections for contractor(call api get_record_inspections[Need agency Access token]) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param stateLicenseId
	 * @return Scheduled inspections
	 */
	public String getScheduledInspections4Contractor(String appId, String agencyName, String token, String stateLicenseId) {
		String result = null;
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpGet getRecordInspections = null;
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			String luckToken = mapper.readTree(authorizeService.loginAsAgency()).get("access_token").getTextValue();
			
			String jobs = jobService.getJobs4Contractor(appId, agencyName, luckToken, stateLicenseId, true);
			JsonNode records = mapper.readTree(jobs).get("result");
			
			if (records != null && records.isArray()) {
				HashMap<String, ArrayList<JsonNode>> returnJson = new HashMap<String, ArrayList<JsonNode>>();
				ArrayList<JsonNode> inspectionList = new ArrayList<JsonNode>();
				
				for (JsonNode record : records) {
					String recordId = record.get("id").getTextValue();
					String id = recordId.substring(recordId.indexOf("-") + 1, recordId.length());
					getRecordInspections = new HttpGet(host + "/v3/records/" + id + "/inspections?openInspectionsOnly=true&offset=0&limit=1000");
					getRecordInspections.setHeaders(HttpHeaderUtil.createTokenHeaders(properties.getProperty("agency.admin.appid"), properties.getProperty("agency.admin.agencyname"), luckToken));
					JsonNode inspections = mapper.readTree(EntityUtils.toString(httpClient.execute(getRecordInspections).getEntity())).get("inspections");
					if (inspections.isArray()) {
						for (JsonNode inspection : inspections) {
							String status = inspection.get("status").get("type").getTextValue().toLowerCase();
							if (status.equals("scheduled") || status.equals("pending")) {
								JSONObject inspectionJsonObject = new JSONObject(inspection.toString());
								inspectionJsonObject.put("address", "400 NE Squaw Creek Road Rd, Olympic Valley, CA 96146");
								inspectionList.add(mapper.readTree(inspectionJsonObject.toString()));
							}
						}
					}
				}
				
				returnJson.put("inspections", inspectionList);
				result = mapper.writeValueAsString(returnJson);
			} else {
				result = jobs;
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (getRecordInspections != null) {
				getRecordInspections.releaseConnection();
			}
		}
		
		return result;
	}
	
	/**
	 * Get inspection details info(call api get_inspection[Need agency Access token]) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param inspectionId
	 * @param isAgency
	 * @return Inspection details info
	 */
	public String getInspectionDetails(String appId, String agencyName, String token, String inspectionId, Boolean isAgency){
		String result = null;
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(host + "/v3/inspections/" + inspectionId);
		try {
			if (isAgency) {
				httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(appId, agencyName, token));
			} else {
				String luckToken = new ObjectMapper().readTree(authorizeService.loginAsAgency()).get("access_token").getTextValue();
				httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(properties.getProperty("agency.admin.appid"), properties.getProperty("agency.admin.agencyname"), luckToken));
			}
			result = EntityUtils.toString(httpClient.execute(httpGet).getEntity());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/**
	 * Get contractor details info(call api get_record_professionals) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param recordId
	 * @return Contactor details info
	 */
	public String getContractorDetails(String appId, String agencyName, String token, String recordId){
		String result = null;
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(host + "/v3p/records/" + recordId + "/professionals");
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

	/**
	 * get inspector details info for contractor(call api get_inspector[Need agency Access token])
	 * @return inspector details info
	 */
	public String getInspectorDetails() {
		String result = null;
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(host + "/v3/inspector");
		try {
			ObjectMapper mapper = new ObjectMapper();
			String luckToken = mapper.readTree(authorizeService.loginAsAgency()).get("access_token").getTextValue();
			
			httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(properties.getProperty("agency.admin.appid"), properties.getProperty("agency.admin.agencyname"), luckToken));
			
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
	
	/**
	 * Get inspection types by record id(call api get_ref_record_type,get_ref_inspection_types[Need agency Access token]) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param recordId
	 * @return inspection types
	 */
	public String getInspectionTypesByRecordId(String appId, String agencyName, String token, String recordId){
		String result = null;
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			
			ObjectMapper mapper = new ObjectMapper();
			String recordTypeId = mapper.readTree(jobService.getJobDetails(appId, agencyName, token, recordId)).get("records").get(0).get("type").get("id").getTextValue();
			String luckToken = mapper.readTree(authorizeService.loginAsAgency()).get("access_token").getTextValue();
			Header[] tokenHeaders = HttpHeaderUtil.createTokenHeaders(properties.getProperty("agency.admin.appid"), properties.getProperty("agency.admin.agencyname"), luckToken);
			
			HttpGet GetrecordType = new HttpGet(host + "/v3/system/record/types/" + recordTypeId);
			GetrecordType.setHeaders(tokenHeaders);
			
			String inspectionGroups = mapper.readTree(EntityUtils.toString(httpClient.execute(GetrecordType).getEntity())).get("recordType").get("inspectionGroups").get(0).getTextValue();
			
			HttpGet GetInspectionTypes = new HttpGet(host + "/v3/system/inspection/groups/" + inspectionGroups + "/types?offset=0&limit=1000");
			GetInspectionTypes.setHeaders(tokenHeaders);
			
			result = EntityUtils.toString(httpClient.execute(GetInspectionTypes).getEntity());
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
	 * Schedule inspection(call create_inspection[Need agency Access token]) 
	 * @param jsonStr
	 * @return result message
	 */
	public String scheduleInspection(String jsonStr){
		String result = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(jsonStr);
			Iterator<String> fieldNames = json.getFieldNames();
			JSONObject request = new JSONObject();
			JSONObject inspection = new JSONObject();
//			String appId = null;
//			String agencyName = null;
//			String token = null;
			
			while (fieldNames.hasNext()) {
				
//				if (fieldName.equals("app_id")) {
//					appId = json.get(fieldName).getTextValue();
//				} else if (fieldName.equals("agency_name")) {
//					agencyName = json.get(fieldName).getTextValue();
//				} else if (fieldName.equals("token")) {
//					token = json.get(fieldName).getTextValue();
//				} else 
				String fieldName = fieldNames.next();
				if (fieldName.equals("inspector")){
					JSONObject inspector = new JSONObject();
					inspector.put("id", json.get(fieldName).get("id").getTextValue());
					inspector.put("display", json.get(fieldName).get("display").getTextValue());
					inspection.put("inspector", inspector);
				} else if (fieldName.equals("comments")) {
					JsonNode comments = json.get(fieldName);
					if (comments.isArray()) {
						ArrayList<JSONObject> commentsList = new ArrayList<JSONObject>();
						JSONObject commentObj = null;
						for (JsonNode comment : comments) {
							commentObj = new JSONObject();
							commentObj.put("fillDate", comment.get("fillDate").getTextValue());
							commentObj.put("fillPeopleName", comment.get("fillPeopleName").getTextValue());
							commentObj.put("content", comment.get("content").getTextValue());
							commentsList.add(commentObj);
						}
						inspection.put("comments", commentsList);
					}
				}
				else {
					inspection.put(fieldName, json.get(fieldName).getTextValue());
				}
			}
			
			request.put("createInspection", inspection);
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(host + "/v3/inspections");
			String luckToken = mapper.readTree(authorizeService.loginAsAgency()).get("access_token").getTextValue();
			httpPost.setHeaders(HttpHeaderUtil.createTokenHeaders(properties.getProperty("agency.admin.appid"), properties.getProperty("agency.admin.agencyname"), luckToken));
			httpPost.setEntity(new StringEntity(request.toString()));
			result = EntityUtils.toString(httpClient.execute(httpPost).getEntity());
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
	 * Reschedule inspection(call reschedule_inspection[Need agency Access token]) 
	 * @param jsonStr
	 * @return result message
	 */
	public String rescheduleInspection(String jsonStr){
		String result = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(jsonStr);
			Iterator<String> fieldNames = json.getFieldNames();
			JSONObject request = new JSONObject();
			JSONObject inspection = new JSONObject();
			String inspectionId = null;
//			String appId = null;
//			String agencyName = null;
//			String token = null;
			
			while (fieldNames.hasNext()) {
//				if (fieldName.equals("app_id")) {
//					appId = json.get(fieldName).getTextValue();
//				} else if (fieldName.equals("agency_name")) {
//					agencyName = json.get(fieldName).getTextValue();
//				} else if (fieldName.equals("token")) {
//					token = json.get(fieldName).getTextValue();
//				} else 
				String fieldName = fieldNames.next();
				if (fieldName.equals("id")) {
					inspectionId = json.get(fieldName).getTextValue();
					inspection.put(fieldName, json.get(fieldName).getTextValue());
				} else if (fieldName.equals("record")) {
					JsonNode record = json.get(fieldName);
					JSONObject recordObj = new JSONObject();
					recordObj.put("id", record.get("id").getTextValue());
					recordObj.put("display", record.get("display").getTextValue());
					inspection.put("record", recordObj);
				} else if (fieldName.equals("type")) {
					JsonNode type = json.get(fieldName);
					JSONObject typeObj = new JSONObject();
					typeObj.put("id", type.get("id").getTextValue());
					typeObj.put("display", type.get("display").getTextValue());
					inspection.put("type", typeObj);
				} else if (fieldName.equals("inspector")) {
					JsonNode inspector = json.get(fieldName);
					JSONObject inspectorObj = new JSONObject();
					inspectorObj.put("id", inspector.get("id").getTextValue());
					inspectorObj.put("name", inspector.get("name").getTextValue());
					inspectorObj.put("display", inspector.get("display").getTextValue());
					inspection.put("inspector", inspectorObj);
				} else if (fieldName.equals("status")) {
					JsonNode status = json.get(fieldName);
					JSONObject statusObj = new JSONObject();
					statusObj.put("id", status.get("id").getTextValue());
					statusObj.put("type", status.get("type").getTextValue());
					statusObj.put("display", status.get("display").getTextValue());
					inspection.put("status", statusObj);
				} else {
					inspection.put(fieldName, json.get(fieldName).getTextValue());
				}
			}
			
		    request.put("rescheduleInspection", inspection);
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPut httpPut = new HttpPut(host + "/v3/inspections/" + inspectionId + "/reschedule");
			String luckToken = mapper.readTree(authorizeService.loginAsAgency()).get("access_token").getTextValue();
			httpPut.setHeaders(HttpHeaderUtil.createTokenHeaders(properties.getProperty("agency.admin.appid"), properties.getProperty("agency.admin.agencyname"), luckToken));
			httpPut.setEntity(new StringEntity(request.toString()));
			result = EntityUtils.toString(httpClient.execute(httpPut).getEntity());
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
	 * Cancel inspection(call cancel_inspection[Need agency Access token]) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param inspectionId
	 * @return result message
	 */
	public String cancelInspection(String appId, String agencyName, String token, String inspectionId) {
		String result = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			String luckToken = mapper.readTree(authorizeService.loginAsAgency()).get("access_token").getTextValue();
			Header[] tokenHeaders = HttpHeaderUtil.createTokenHeaders(properties.getProperty("agency.admin.appid"), properties.getProperty("agency.admin.agencyname"), luckToken);
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPut httpPut = new HttpPut(host + "/v3/inspections/" + inspectionId + "/cancel");
			httpPut.setHeaders(tokenHeaders);
			
			String response = EntityUtils.toString(httpClient.execute(httpPut).getEntity());
			if(response.equals("{}")){
				result = "{\"status\":\"success\",\"message\": \"The inspection has been canceled.\"}";
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * Get inspections by record id(call get_record_inspections[Need agency Access token]) 
	 * @param appId
	 * @param agencyName
	 * @param token
	 * @param recordId
	 * @param isAgency
	 * @return inspections
	 */
	public String getInspectionsByRecordId(String appId, String agencyName, String token, String recordId, Boolean isAgency) {
		String result = null;
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(host + "/v3/records/" + recordId + "/inspections?openInspectionsOnly=true&offset=0&limit=1000");
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, ArrayList<JsonNode>> returnJson = new HashMap<String, ArrayList<JsonNode>>();
			ArrayList<JsonNode> inspectionList = new ArrayList<JsonNode>();
			if (isAgency) {
				httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(appId, agencyName, token));
			} else {
				String luckToken = mapper.readTree(authorizeService.loginAsAgency()).get("access_token").getTextValue();
				httpGet.setHeaders(HttpHeaderUtil.createTokenHeaders(properties.getProperty("agency.admin.appid"), properties.getProperty("agency.admin.agencyname"), luckToken));
			}
			String response = EntityUtils.toString(httpClient.execute(httpGet).getEntity());
			JsonNode inspections = mapper.readTree(response).get("inspections");
			
			if (inspections != null && inspections.isArray()) {
				for (JsonNode inspection : inspections) {
					String status = inspection.get("status").get("type").getTextValue().toLowerCase();
					if (status.equals("scheduled") || status.equals("pending")) {
						inspectionList.add(inspection);
					}
				}
				returnJson.put("inspections", inspectionList);
				result = mapper.writeValueAsString(returnJson);
			} else {
				result = response;
			}
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
