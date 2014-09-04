package com.missionsky.cgarest.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.missionsky.cgarest.service.InspectionService;

/**
 * Inspector Controller 
 * @author ellis.xie
 * @version 1.0
 */
@RestController
@RequestMapping(value="inspection")
public class InspectionController {
	
	@Autowired
	private InspectionService inspectionService;
	
	@RequestMapping(value="getscheduledinspections4inspector")
	public @ResponseBody String getScheduledInspections4Inspector(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String inspectorId = jsonMap.get("inspector_id");
		return inspectionService.getScheduledInspections4Inspector(appId, agencyName, token, inspectorId);
	}
	
	@RequestMapping(value="getscheduledinspections4contractor")
	public @ResponseBody String getScheduledInspections4Contractor(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String stateLicenseId = jsonMap.get("state_license_id");
		return inspectionService.getScheduledInspections4Contractor(appId, agencyName, token, stateLicenseId);
	}
	
	@RequestMapping(value="getinspectiondetails")
	public @ResponseBody String getInspectionDetails(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String inspectionId = jsonMap.get("inspection_id");
		return inspectionService.getInspectionDetails(appId, agencyName, token, inspectionId, true);
	}
	
	@RequestMapping(value="getinspectiondetails4contractor")
	public @ResponseBody String getInspectionDetails4Contractor(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String inspectionId = jsonMap.get("inspection_id");
		return inspectionService.getInspectionDetails(appId, agencyName, token, inspectionId, false);
	}
	
	@RequestMapping(value="getcontractordetails")
	public @ResponseBody String getContractorDetails(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String recordId = jsonMap.get("record_id");
		
		return inspectionService.getContractorDetails(appId, agencyName, token, recordId);
	}
	
	@RequestMapping(value="getinspectordetails")
	public @ResponseBody String getInspectorDetails(@RequestBody HashMap<String, String> jsonMap){
		return inspectionService.getInspectorDetails();
	}
	
	@RequestMapping(value="getinspectiontypesbyrecordid")
	public @ResponseBody String getInspectionTypesByRecordId(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String recordId = jsonMap.get("record_id");
		return inspectionService.getInspectionTypesByRecordId(appId, agencyName, token, recordId); 
	}
	
	@RequestMapping(value="scheduleinspection")
	public @ResponseBody String scheduleInspection(@RequestBody String jsonStr){
		return inspectionService.scheduleInspection(jsonStr);
	}
	
	@RequestMapping(value="rescheduleinspection")
	public @ResponseBody String rescheduleInspection(@RequestBody String jsonStr){
		return inspectionService.rescheduleInspection(jsonStr);
	}
	
	@RequestMapping(value="cancelinspection")
	public @ResponseBody String cancelInspection(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String inspectionId = jsonMap.get("inspection_id");
		return inspectionService.cancelInspection(appId, agencyName, token, inspectionId);
	}
	
	@RequestMapping(value="getinspectionsbyrecordid4inspector")
	public @ResponseBody String getInspectionsByRecordId4Inspector(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String recordId = jsonMap.get("record_id");
		return inspectionService.getInspectionsByRecordId(appId, agencyName, token, recordId, true); 
	}
	
	@RequestMapping(value="getinspectionsbyrecordid4contractor")
	public @ResponseBody String getInspectionsByRecordId4Contractor(@RequestBody HashMap<String, String> jsonMap){
		String appId = jsonMap.get("app_id");
		String agencyName = jsonMap.get("agency_name");
		String token = jsonMap.get("token");
		String recordId = jsonMap.get("record_id");
		return inspectionService.getInspectionsByRecordId(appId, agencyName, token, recordId, false); 
	}
}
