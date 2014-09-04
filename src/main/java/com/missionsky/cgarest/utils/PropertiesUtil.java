package com.missionsky.cgarest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
//	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	private static final String DEFAULT_CONFIG = PropertiesUtil.class.getResource("/").getPath()+"/conf.properties";
	
	private PropertiesUtil(){
		
	}
	
	public static Properties loadPropertiesFile(String filePath){
		Properties properties = new Properties();
		InputStream is = null;
		try {
			try {
				if(filePath==null || filePath.trim().equals("")){
					filePath = DEFAULT_CONFIG;
				}
				is = new FileInputStream(new File(filePath));
				properties.load(is);
			} finally{
				if(is != null){
					is.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
//			logger.error(e.getMessage());
			properties = null;
		}
		return properties;
	}
	
	public static Map<String, Properties> loadPropertiesFiles(List<String> filePaths){
		Map<String, Properties> propertiesMap = new HashMap<String, Properties>();
		for(String filePath:filePaths){
			propertiesMap.put(filePath, loadPropertiesFile(filePath));
		}
		return propertiesMap;
	}
	
	public static boolean storePropertiesFile(String filePath, Properties properties){
		return storePropertiesFile(filePath, getProperty(filePath));
	}
	
	public static boolean storePropertiesFile(String filePath,Map<String, String> propertyMap){
		Properties properties = new Properties();
		FileWriter writer = null;
		try {
			try {
				writer = new FileWriter(filePath);
				for(Map.Entry<String, String> entry:propertyMap.entrySet()){
					properties.put(entry.getKey(), entry.getValue());
				}
				properties.store(writer, null);
			} finally{
				if(writer != null){
					writer.close();
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
//			logger.error(e.getMessage());
			return false;
		}
	}
	
	public static boolean storePropertiesFiles(List<String> filePaths,
			List<Map<String, String>> propertyMaps) {
		int filePathSize = filePaths.size();
		if (filePathSize != propertyMaps.size()) {
			return false;
		}
		for (int i = 0; i < filePathSize; i++) {
			if (!storePropertiesFile(filePaths.get(i), propertyMaps.get(i))) {
				return false;
			}
		}
		return true;
	}

	
	public static Map<String, String> getProperty(List<String> keys,Properties properties){
		Map<String, String> propertyMap = new HashMap<String, String>();
		for(String key:keys){
			propertyMap.put(key, properties.getProperty(key));
		}
		return propertyMap;
	}
	
	public static String getString(String key, String defaultValue,
			Properties properties) {
		return (properties == null) ? null : properties.getProperty(key,
				defaultValue);
	}
	
	public static Integer getInteger(String key, Integer defaultValue,
			Properties properties) {
		String stringValue = (properties == null) ? null : properties
				.getProperty(key, defaultValue.toString());
		Integer value = null;
		if (stringValue == null) {
			return null;
		}
		try {
			value = Integer.valueOf(stringValue);
		} catch (NumberFormatException e) {
			value = defaultValue;
		}
		return value;
	}
	
	public static Map<String, String> getProperty(List<String> keys,String filePath){
		return getProperty(keys, loadPropertiesFile(filePath));
	}
	
	public static Boolean getBoolean(String key, Boolean defaultValue,
			Properties properties) {
		String stringValue = (properties == null) ? null : properties
				.getProperty(key, defaultValue.toString());
		return (stringValue == null) ? null : Boolean.valueOf(stringValue);
	}

	public static Double getDouble(String key, Double defaultValue,
			Properties properties) {
		String stringValue = (properties == null) ? null : properties
				.getProperty(key, defaultValue.toString());
		Double value = null;
		if (stringValue == null) {
			return null;
		}
		try {
			value = Double.valueOf(stringValue);
		} catch (NumberFormatException e) {
			value = defaultValue;
		}
		return value;
	}

	public static Date getDate(String key, Date defaultValue,
			Properties properties) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String stringValue = (properties == null) ? null : properties
				.getProperty(key, sdf.format(defaultValue));
		Date value = null;
		if (stringValue == null) {
			return null;
		}
		try {
			value = sdf.parse(stringValue);
		} catch (ParseException e) {
			value = defaultValue;
		}

		return value;
	}
	
	public static Map<String, String> getProperty(Properties properties) {
		Map<String, String> propertyMap = new HashMap<String, String>();
		for (Iterator<Object> iter = properties.keySet().iterator(); iter
				.hasNext();) {
			String key = iter.next().toString();
			propertyMap.put(key, properties.getProperty(key));
		}
		return propertyMap;
	}
	
	public static Map<String, String> getProperty(String filePath) {
		return getProperty(loadPropertiesFile(filePath));
	}
}
