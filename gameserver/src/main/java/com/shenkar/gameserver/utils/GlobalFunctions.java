package com.shenkar.gameserver.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.websocket.Session;

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
	     SimpleDateFormat simpleDateFormat = 
	 new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
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

	 public static Boolean SendMessage(Session _Session,String _Message)
	 {
		 try
		 {
			 if(_Session !=null && _Session.isOpen())
			 {	 
				 _Session.getBasicRemote().sendText(_Message);
				 return true;
			 }
			 else System.out.println("SendMessage Failed, " + _Session);
		 }
		 catch(Exception e){System.out.println("SendMessage Failed, " + e.getMessage());}
		 return false;
	 }

	 public static Date ParseStringDate(String _DateToParse)
		{
			try
			{
				String[] _tokens = _DateToParse.split( "[ ]");
			
				String[] _tokens2 = _tokens[0].split("[-]");
				String[] _tokens3 = _tokens[1].split("[:]");
				int _year = Integer.parseInt(_tokens2[0]);
				
				int _month = Integer.parseInt(_tokens2[1]) - 1;
				int _days = Integer.parseInt(_tokens2[2]);
				int _hours = Integer.parseInt(_tokens3[0]);
				int _minutes = Integer.parseInt(_tokens3[1]);
				
				String _t = _tokens3[2];
				String[] _sec = _t.split(Pattern.quote("."));
				int _seconds = Integer.parseInt(_sec[0]);

				Calendar cal = Calendar.getInstance();
				cal.set(_year,_month, _days,_hours,_minutes,_seconds); 
				return cal.getTime();
			}
			catch(Exception e)
			{
				String _e = e.getMessage();
			}
			
			Calendar cal = Calendar.getInstance();
			return cal.getTime();
		}
	 
	 public static Date GetUTCDateTime(){return ParseStringDate(GetUTCDate());}

	 public static Integer GetRandomNumber(int aStart, int aEnd)
	 {
		Random aRandom = new Random();
		long range = (long)aEnd - (long)aStart + 1;
		long fraction = (long)(range * aRandom.nextDouble());
		int randomNumber =  (int)(fraction + aStart);
		return randomNumber;
	 }
}
