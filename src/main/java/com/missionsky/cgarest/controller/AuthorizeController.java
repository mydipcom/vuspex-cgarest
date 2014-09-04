package com.missionsky.cgarest.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.missionsky.cgarest.service.AuthorizeService;

/**
 * Authorize Controller
 * @author ellis.xie 
 * @version 1.0
 */
@Controller
@RequestMapping(value="authorize")
public class AuthorizeController {
	
	@Autowired
	private AuthorizeService service;
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	public @ResponseBody String login(@RequestBody HashMap<String, String> jsonMap){
		
		String loginName = jsonMap.get("login_name");
		String password = jsonMap.get("password");
		String appId = jsonMap.get("app_id");
		String appSecret = jsonMap.get("app_secret");
		String appType = jsonMap.get("app_type");
		String appName = jsonMap.get("app_name");
		String agencyName = jsonMap.get("agency_name");
		return service.login(loginName, password, appId, appSecret, appType, appName, agencyName);
	}
	
	@RequestMapping(value="authresponse")
	public @ResponseBody String authResponse(@RequestParam(value="code", required=false) String code, @RequestParam(value="state", required=false) String state){
		return state + "=" + code;
	}
	
}
