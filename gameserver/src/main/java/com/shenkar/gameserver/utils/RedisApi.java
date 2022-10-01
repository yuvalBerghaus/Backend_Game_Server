package com.shenkar.gameserver.utils;

import java.util.Map;

public class RedisApi 
{
	public static void SetUserData(String _Key,Map<String,String> _UserData)
	{RedisLogic.RedisSetMap(_Key + "/Users", _UserData);}
	
	public static Map<String,String> GetUserData(String _Key)
	{return RedisLogic.RedisGetMap(_Key + "/Users");}
	
	public static Map<String,String> GetSearchData(String _UserId)
	{return RedisLogic.RedisGetMap(_UserId + "/Search");}

	public static String GetMatchId() 
	{return RedisLogic.RedisGet("MatchId");}

	public static void SetMatchId(String _MatchId) 
	{RedisLogic.RedisSet("MatchId",_MatchId);}
	
	public static void SetRoomData(String _MatchId,Map<String,String> _RoomData)
	{RedisLogic.RedisSetMap(_MatchId + "/RoomData", _RoomData);}
	
	public static void SetUserMatchData(String _UserId,Map<String,String> _MatchData)
	{RedisLogic.RedisSetMap(_UserId + "/MatchId", _MatchData);}
	
	public static Map<String,String> GetUserMatchData(String _UserId)
	{return RedisLogic.RedisGetMap(_UserId + "/MatchId");}
	
	public static void RmvUserMatchData(String _UserId)
	{RedisLogic.RedisDelete(_UserId + "/MatchId");}

	public static void SetGameMoveResponse(String _MatchId,String _MoveCounter,String _ResponseData)
	{
		RedisLogic.RedisSet(_MatchId + "/R" + _MoveCounter, _ResponseData);
	}
	public static void SetGameMoveIncoming(String _MatchId,String _MoveCounter,String _IncomingData)
	{
		RedisLogic.RedisSet(_MatchId + "/I" + _MoveCounter, _IncomingData);
	}

	public static String GetGameMoveResponse(String _MatchId,String _MoveCounter)
	{return RedisLogic.RedisGet(_MatchId + "/R" + _MoveCounter);} 
	
	public static String GetGameMoveIncoming(String _MatchId,String _MoveCounter)
	{return RedisLogic.RedisGet(_MatchId + "/I" + _MoveCounter);} 
	//search data is a record that contains the roomID (according to the first user's UID
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
	//this record key gives us all the roooms that are currently running in the server
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
		RedisLogic.RedisDelete("/Rooms");
		RedisLogic.RedisSetMap("/Rooms",all_rooms);
		Map<String,String>search_data = GetSearchData(room_id);
		search_data.put("status", status);
		SetSearchData(room_id,search_data);
	}
}
