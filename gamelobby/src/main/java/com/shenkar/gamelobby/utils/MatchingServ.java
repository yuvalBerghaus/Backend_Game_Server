package com.shenkar.gamelobby.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.shenkar.gamelobby.utils.GlobalEnums.Enviroment;

public class MatchingServ {
	private static MatchingServ instance;
	private static Integer roomID = 1000;
	public static MatchingServ getInstance()
	{
		if(instance == null)
			instance = new MatchingServ();
		return instance;
	}
	public Map<String, Object> match_room(Map<String,Object> _Data) {
		Map<String,Object> _ret = new LinkedHashMap<String,Object>();
		String _ws = "ws://localhost:8080/gameserver/game/";
		if(GlobalVariables.curEnviroment == Enviroment.Development)
			_ws = "ws://34.204.6.77:8080/gameserver/game/";
		if(_Data.containsKey("Amount") && _Data.containsKey("UserId")) {
			//_Data will contain the user's details and we will first look if exists any room from redis
			Map<String,String> all_rooms = RedisApi.GetOpenRooms();
			String uid = _Data.get("UserId").toString();
			String betId = _Data.get("Amount").toString();
			
			if(all_rooms.containsKey(betId)) {
				Map<String,Object>current_rooms_of_betId = GlobalFunctions.DeserializeJson(all_rooms.get(betId));
				
				for(String roomId : current_rooms_of_betId.keySet()) { // here we are looking for waiting rooms so we can join it accordingly
					Map<String,Object> current_room = GlobalFunctions.DeserializeJson(current_rooms_of_betId.get(roomId).toString());
					if(current_room.containsKey("state") && current_room.get("state") == "waiting") { // If room is waiting
						current_room.put("uid2", uid);
						current_room.put("Amount_Players", "2");
						current_room.put("state", "Created");
						Map<String,String> _matchingData = new LinkedHashMap<String,String>();
						_ret.put("ConnectionUrl", current_room.get("ConnectionUrl")); // We need the right websocket so we can know exactly to which server we are referring to
						_matchingData.put("roomID", current_room.get("roomID").toString());
						RedisApi.SetUserMatchData(uid, _matchingData);
						return _ret;
					}
				}
				// here we do have a betID but we do not have any room to enter so we need to create it ourselves
				prepareRoom(betId,all_rooms, uid, _ws);

			}
			else {
				// here it means that we didnt find the corresponding betID thus we need to create the bedID and a new room in it
				prepareRoom(betId,all_rooms, uid, _ws);
			}
			/*
			 * else { Map<String,String> _searchData = new LinkedHashMap<String, String>();
			 * _searchData.put("RequestedTime", GlobalFunctions.GetUTCDate());
			 * RedisApi.SetSearchData(_Data.get("UserId").toString(), _searchData); }
			 */	
		}
		return _ret;
	}
	private void prepareRoom(String betId,Map<String,String> all_rooms, String uid , String _ws) {
		// TODO Auto-generated method stub
		Integer _roomid = roomID++;
		Map<String,String> _roomData = new LinkedHashMap<String,String>();
		_roomData.put("state", "Creating");
		_roomData.put("ConnectionUrl", _ws);
		_roomData.put("Amount_Players", "1");
		_roomData.put("uid1", uid);
		_roomData.put("roomID", _roomid.toString());
		String _parsedRoomData = GlobalFunctions.SerializeToJson(_roomData);
		RedisApi.addRooms(betId,_parsedRoomData);
		// If there are no rooms in this bet ID that means we need to add it
		Map<String,String> _matchingData = new LinkedHashMap<String,String>();
		_matchingData.put("roomId", _roomid.toString());
		RedisApi.SetRoomData(_roomid.toString(), _roomData);
		RedisApi.addRooms(betId, _parsedRoomData);
		RedisApi.SetUserMatchData(uid, _matchingData);
	}
}