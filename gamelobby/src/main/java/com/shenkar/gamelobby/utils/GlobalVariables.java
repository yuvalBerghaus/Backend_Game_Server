package com.shenkar.gamelobby.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.shenkar.gamelobby.utils.GlobalEnums.Enviroment;

public class GlobalVariables 
{
	public static GlobalEnums.Enviroment curEnviroment = Enviroment.local;
	public static String serverVersion = "1.0";
	public static Map<String,Map<String,Object>> users = new LinkedHashMap<String, Map<String,Object>>();
	public static Map<String,String> current_user = new LinkedHashMap<String, String>();
}
