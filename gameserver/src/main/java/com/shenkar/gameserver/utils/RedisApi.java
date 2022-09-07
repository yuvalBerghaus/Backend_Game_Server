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
}
