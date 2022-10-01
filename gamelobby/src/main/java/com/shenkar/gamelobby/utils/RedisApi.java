package com.shenkar.gamelobby.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import com.shenkar.gamelobby.utils.RedisLogic;

public class RedisApi 
{
	public static void SetUserData(String _Key,Map<String,String> _UserData)
	{RedisLogic.RedisSetMap(_Key + "/Users", _UserData);}
	
	public static Map<String,String> GetUserData(String _Key)
	{return RedisLogic.RedisGetMap(_Key + "/Users");}
	
	
	public static Map<String,String> GetSearchData(String _UserId)
	{return RedisLogic.RedisGetMap(_UserId + "/Search");}
	
	public static void SetUserRating(String _UserId,String _Rating)
	{RedisLogic.RedisSet(_UserId + "/Rating", _Rating);}
	
	public static String GetUserRating(String _UserId)
	{return RedisLogic.RedisGet(_UserId + "/Rating");}

	public static Map<String,String> GetOpenRooms()
	{
		return RedisLogic.RedisGetMap("/Rooms");
	}
	
	public static void addRooms(String newRoom, String status)
	{
		if(newRoom != null) {
			Map<String,String> all_rooms = GetOpenRooms();
			all_rooms.put(newRoom, status);
			RedisLogic.RedisSetMap("/Rooms",all_rooms);			
		}
	}
	//everytime we implement this function we update the status of all the records according to the status we assigned (to all the records that were created due to the room creation process)
	public static void updateRoomStatus(String room_id,String status) {
		Map<String,String> all_rooms = GetOpenRooms();
		all_rooms.put(room_id, status);
		RedisLogic.RedisSetMap("/Rooms",all_rooms);
		Map<String,String>search_data = GetSearchData(room_id);
		search_data.put("status", status);
		SetSearchData(room_id,search_data);
	}
	public static void SetSearchData(String _roomID,Map<String,String> _SearchData)
	{
		if(_roomID != null && _SearchData != null) {
			RedisLogic.RedisSetMap(_roomID + "/Search", _SearchData);
			addRooms(_roomID, _SearchData.get("status"));	
		}
		else {
			System.out.println("Missing variables in function SetSearchData");
		}
	}
}
