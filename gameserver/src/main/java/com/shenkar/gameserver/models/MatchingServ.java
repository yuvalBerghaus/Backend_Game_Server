package com.shenkar.gameserver.models;

import java.util.LinkedHashMap;
import java.util.Map;

import com.shenkar.gameserver.utils.RedisApi;

public class MatchingServ {
	private Integer roomIdCounter = 10000;
	private static MatchingServ instance;
	public static MatchingServ getInstance()
	{
		if(instance == null)
			instance = new MatchingServ();
		return instance;
	}
	public String match_room(Map<String,Object> _Data) {
		//retrieve all possible rooms compare if there is already a userid with a room
		//_Data will contain the user's details and we will first look if exists any room from redis
		Map<String,String> all_rooms = RedisApi.GetOpenRooms();
		String uid = _Data.get("UserId").toString();
		//checking  if there are no rooms then obviously the requesting user needs to create the room himself
		if(all_rooms.isEmpty()) {
			Map<String,String> searchDataRoom = new LinkedHashMap<String,String>();
			searchDataRoom.put("status", "waiting");
			searchDataRoom.put("uid1", uid);
			RedisApi.SetSearchData((++roomIdCounter).toString(), searchDataRoom);
		}
		else {
			//here we are looking for a waiting room
			Boolean found_opponent = false;
			for(String room_key : all_rooms.keySet()) {
				if(all_rooms.get(room_key).equals("waiting")) {
					RedisApi.updateRoomStatus(room_key, "busy");
					all_rooms.put(room_key, "busy");
					found_opponent = true;
					Map<String,String> found_room = RedisApi.GetSearchData(room_key);
					found_room.put("uid2", uid);
					//now we need to update in both reshumot
					RedisApi.SetSearchData(room_key, found_room);
					return room_key;
				}
			}
			//if player did not find an opponent we must create the room
			if(!found_opponent) {
				String status = "waiting";
				Map<String,String> searchDataRoom = new LinkedHashMap<String,String>();
				searchDataRoom.put("status", status);
				searchDataRoom.put("Uid1", _Data.get("UserId").toString());
				RedisApi.addRooms(uid, status);
			}
		}
		return uid;
		/*
		 * else { Map<String,String> _searchData = new LinkedHashMap<String, String>();
		 * _searchData.put("RequestedTime", GlobalFunctions.GetUTCDate());
		 * RedisApi.SetSearchData(_Data.get("UserId").toString(), _searchData); }
		 */
	}
}