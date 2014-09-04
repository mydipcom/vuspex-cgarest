package com.missionsky.cgarest.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.missionsky.cgarest.service.PermitService;

/**
 * Permit Controller 
 * @author ellis.xie
 * @version 1.0
 */
@RestController
@RequestMapping(value="permit")
public class PermitController {
	
	@Autowired
	private PermitService permitService;
	
	@RequestMapping(value="getpermitaddresses")
	public @ResponseBody String getPermitAddresses(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String recordId = jsonMap.get("record_id");
		return permitService.getPermitAddresses(appId, agencyName, token, recordId);
	}
	
	@RequestMapping(value="getpermitparcels")
	public @ResponseBody String getPermitParcels(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String recordId = jsonMap.get("record_id");
		return permitService.getPermitParcels(appId, agencyName, token, recordId);
	}
	
	@RequestMapping(value="getpermitowners4inspector")
	public @ResponseBody String getPermitOwners4Inspector(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String recordId = jsonMap.get("record_id");
		return permitService.getPermitOwners4Inspector(appId, agencyName, token, recordId);
	}
	
	@RequestMapping(value="getpermitowners4contractor")
	public @ResponseBody String getPermitOwners4Contractor(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String recordId = jsonMap.get("record_id");
		return permitService.getPermitOwners4Contractor(appId, agencyName, token, recordId);
	}
	

}
