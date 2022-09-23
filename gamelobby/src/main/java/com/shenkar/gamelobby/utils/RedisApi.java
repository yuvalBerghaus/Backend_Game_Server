package com.shenkar.gamelobby.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class RedisApi 
{
	public static void SetUserData(String _Key,Map<String,String> _UserData)
	{RedisLogic.RedisSetMap(_Key + "/Users", _UserData);}
	
	public static Map<String,String> GetUserData(String _Key)
	{return RedisLogic.RedisGetMap(_Key + "/Users");}
	

//	public static void SetSearchData(String _UserId,Map<String,String> _SearchData)
//	{
//		RedisLogic.RedisSetMap(_UserId + "/Search", _SearchData);
//		addRooms(_UserId);
//		
//	}
	
	public static Map<String,String> GetSearchData(String _UserId)
	{return RedisLogic.RedisGetMap(_UserId + "/Search");}
	
	public static void SetUserRating(String _UserId,String _Rating)
	{RedisLogic.RedisSet(_UserId + "/Rating", _Rating);}
	
	public static String GetUserRating(String _UserId)
	{return RedisLogic.RedisGet(_UserId + "/Rating");}
//	public static Map<String,String> GetOpenRooms()
//	{
//		return RedisLogic.RedisGetMap("/Rooms");
//	}
//	public static void CreateRooms(String currentRoom)
//	{
//		Map<String,String> all_rooms = new LinkedHashMap<String,String>();
//		all_rooms.put(currentRoom, "waiting");
//		RedisLogic.RedisSetMap("/Rooms",all_rooms);
//	}
//	public static void addRooms(String newRoom)
//	{
//		Map<String,String> all_rooms = GetOpenRooms();
//		all_rooms.put(newRoom, "waiting");
//		RedisLogic.RedisSetMap("/Rooms",all_rooms);
//	}
//	public static void updateRoomStatus(String room_id,String status) {
//		Map<String,String> all_rooms = GetOpenRooms();
//		all_rooms.put(room_id, status);
//		RedisLogic.RedisSetMap("/Rooms",all_rooms);
//		Map<String,String>search_data = GetSearchData(room_id);
//		search_data.put("status", status);
//		SetSearchData(room_id,search_data);
//	}
}
