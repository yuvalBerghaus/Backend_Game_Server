package com.shenkar.gameserver.utils;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisLogic 
{
	private static String path = "redis-19002.c300.eu-central-1-1.ec2.cloud.redislabs.com";
	private static int port = 19002;
	private static String userName = "default";
	private static String cachekey = "77MKa6xIHrhbuEeUx19u7Ey1IlH0AOhN";
	
	private static JedisPool poolConnection;
	private static Boolean isInit = false;
	
	private static void RedisInit()
	{
		if(isInit == false)
		{
			poolConnection = new JedisPool(path, port,userName,cachekey);
			isInit = true;
		}
	}
	
	public static String RedisSetMap(String _Key,Map<String,String> _Value)
	{
	   try
	   {
	        RedisInit();
	        Jedis _jedis = poolConnection.getResource();     
	        Long _data = _jedis.hset(_Key, _Value);	
	        _jedis.close();
	        if(_data == 0)
	        	return "";
	        return _data.toString();
	   }
	   catch(Exception e)
	   {System.out.println(e.getMessage());}
	    return "";
	 }

	public static Map<String,String> RedisGetMap(String _Key)
	{
	   RedisInit();
	   Jedis _jedis = poolConnection.getResource();   
	   Map<String,String> _data = _jedis.hgetAll(_Key);	
	   _jedis.close();
	   return _data;
	}
	
	public static String RedisSet(String _Key,String _Value)
	{
	   try
	   {
		RedisInit();
		Jedis _jedis = poolConnection.getResource();  
		String _data = _jedis.set(_Key, _Value);	
		_jedis.close();
		if(_data == null)
		     return "";
		return _data;
	    }
	    catch(Exception e)
	    {System.out.println(e.getMessage());}
	    return "";
	}

	public static String RedisGet(String _Key)
	{
	    RedisInit();
	    Jedis _jedis = poolConnection.getResource();     
	    String _data = _jedis.get(_Key);	
	    _jedis.close();
	    if(_data == null)
	    	return "";
	    return _data;
	 }
	public static Long RedisDelete(String _Key)
	{
		RedisInit();
		Jedis _jedis = poolConnection.getResource();     
	    Long _data = _jedis.del(_Key);	
	    _jedis.close();
	    return _data;
	}
}
