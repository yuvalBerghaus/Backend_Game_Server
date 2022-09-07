package com.shenkar.gamelobby.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import com.github.javafaker.Faker;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GlobalFunctions
{
	 public static Map<String, Object> DeserializeJson(String _Json)
	 {
		 Gson _gson = new GsonBuilder().create();
		 java.lang.reflect.Type typeofHashMap = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
		 return _gson.fromJson(_Json, typeofHashMap);
	 }
	 
	 public static String GetUTCDate()
	 {
	     TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	     TimeZone timeZone = TimeZone.getTimeZone("UTC");
	     Calendar calendar = Calendar.getInstance(timeZone);
	     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	     simpleDateFormat.setTimeZone(timeZone);
	     String _returnValue = calendar.get(Calendar.YEAR) + "-" + 
	     (calendar.get(Calendar.MONTH) + 1) + "-" + 
	      calendar.get(Calendar.DAY_OF_MONTH) + " ";
	      _returnValue += calendar.get(Calendar.HOUR_OF_DAY) + ":" +
	      calendar.get(Calendar.MINUTE) + ":" + 
	      calendar.get(Calendar.SECOND);
	      return _returnValue;
	  }
	 
	 public static String SerializeToJson(Object _Value)
	 {
		 Gson _gson = new GsonBuilder().create();
		 return _gson.toJson(_Value);
	 }

	 public static String CreateUserUUID()
	 {
		 return UUID.randomUUID().toString().substring(0,17).toUpperCase();
	 }
	 
	 public static void GlobalResponse(HttpServletResponse _Response,Map<String,Object> _Data)
	 {
		String _retString = GlobalFunctions.SerializeToJson(_Data);
		try 
		{
			PrintWriter out = _Response.getWriter();
		    out.println(_retString);
		} 
		catch (IOException e) {System.out.println("GlobalResponse " + e.getMessage());}
	 }
	 public static String getRandomNumberString() {
		    // It will generate 6 digit random Number.
		    // from 0 to 999999
		    Random rnd = new Random();
		    int number = rnd.nextInt(999999);

		    // this will convert any number sequence into 6 character.
		    return String.format("%06d", number);
	}
	 public static long diffDays(String date1 , String date2) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		Date _date1= sdf.parse(date1);
		Date _date2 = sdf.parse(date2);
		long diff =	_date1.getTime() - _date2.getTime();
		long diff_days = diff / (24 * 60 * 60 * 1000);
		return diff_days;
	 }
	 public static String randomName() {
			Faker fake = new Faker();
			String random_name = fake.name().firstName();
			return random_name;
	 }
	 public static Map<String,String> parseAllToMapString(Map<String,Object> map) {
		 Map<String,String> converted_map = new LinkedHashMap<String,String>();
			for (String key : map.keySet()) {
				converted_map.put(key, map.get(key).toString());
				System.out.println("The key is "+key+" and the value is "+map.get(key));
			}
		return converted_map;
	 }

}
