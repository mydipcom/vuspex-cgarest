package com.missionsky.cgarest.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.missionsky.cgarest.service.JobService;

/**
 * Job Controller
 * @author ellis.xie 
 * @version 1.0
 */
@RestController
@RequestMapping("job")
public class JobController {
	
	@Autowired
	private JobService service;
	
	@RequestMapping(value="getjobs4inspector")
	public @ResponseBody String getJobs4Inspector(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String inspectorId = jsonMap.get("inspector_id");
		return service.getJobs4Inspector(appId, agencyName, token, inspectorId);
	}
	
	@RequestMapping(value="getjobs4contractor")
	public @ResponseBody String getJobs4Contractor(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String stateLicenseId = jsonMap.get("state_license_id");
		return service.getJobs4Contractor(appId, agencyName, token, stateLicenseId, false);
	}
	
	@RequestMapping(value="getjobdetails")
	public @ResponseBody String getJobDetails(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String recordId = jsonMap.get("record_id");
		return service.getJobDetails(appId, agencyName, token, recordId);
	}
}
