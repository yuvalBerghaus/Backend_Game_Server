package com.shenkar.gamelobby.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class RedisApi 
{
	public static void SetUserData(String _Key,Map<String,String> _UserData)
	{RedisLogic.RedisSetMap(_Key + "/Users", _UserData);}
	
	public static Map<String,String> GetUserData(String _Key)
	{return RedisLogic.RedisGetMap(_Key + "/Users");}
	public static void SetUserMatchData(String _UserId,Map<String,String> _MatchData)
	{RedisLogic.RedisSetMap(_UserId + "/MatchId", _MatchData);}
	
	public static Map<String,String> GetSearchData(String _UserId)
	{return RedisLogic.RedisGetMap(_UserId + "/Search");}
	
	public static void SetRoomData(String _MatchId,Map<String,String> _RoomData)
	{RedisLogic.RedisSetMap(_MatchId + "/RoomData", _RoomData);}
	
	public static void SetUserRating(String _UserId,String _Rating)
	{RedisLogic.RedisSet(_UserId + "/Rating", _Rating);}
	
	public static String GetUserRating(String _UserId)
	{return RedisLogic.RedisGet(_UserId + "/Rating");}

	public static Map<String,String> GetOpenRooms()
	{
		return RedisLogic.RedisGetMap("/Rooms");
	}
	
	public static void addRooms(String betId, String _roomData)
	{
		if(betId != null && _roomData != null) {
			Map<String,String> all_rooms = GetOpenRooms();
			Map<String,Object> room_data = GlobalFunctions.DeserializeJson(_roomData);
			if(all_rooms != null) {
				if(all_rooms.containsKey(betId)) {
					Map<String,Object> rooms_of_current_betID = GlobalFunctions.DeserializeJson(all_rooms.get(betId));
					_roomData = GlobalFunctions.SerializeToJson(room_data);
					rooms_of_current_betID.put(room_data.get("roomID").toString(), _roomData);
					all_rooms.put(betId, GlobalFunctions.SerializeToJson(rooms_of_current_betID));
					RedisLogic.RedisSetMap("/Rooms",all_rooms);			
				}
				// Case that no betId exists so we need to create one and put it in
				else {
					Map<String,String> rooms_of_current_betID = new LinkedHashMap<String,String>();
					rooms_of_current_betID.put(room_data.get("roomID").toString(), _roomData);
					String converted_map = GlobalFunctions.SerializeToJson(rooms_of_current_betID);
					all_rooms.put(betId, converted_map);
					RedisLogic.RedisSetMap("/Rooms", all_rooms);
				}
			}
		}
	}
//	//every time we implement this function we update the status of all the records according to the status we assigned (to all the records that were created due to the room creation process)
//	public static void updateRoomStatus(String room_id,String status) {
//		Map<String,String> all_rooms = GetOpenRooms();
//		all_rooms.put(room_id, status);
//		RedisLogic.RedisSetMap("/Rooms",all_rooms);
//		Map<String,String>search_data = GetSearchData(room_id);
//		search_data.put("status", status);
//		SetSearchData(room_id,search_data);
//	}
//	public static void SetSearchData(String _roomID,Map<String,String> _SearchData)
//	{
//		if(_roomID != null && _SearchData != null) {
//			RedisLogic.RedisSetMap(_roomID + "/Search", _SearchData);
//			addRooms(_roomID, _SearchData.get("status"));	
//		}
//		else {
//			System.out.println("Missing variables in function SetSearchData");
//		}
//	}
}
