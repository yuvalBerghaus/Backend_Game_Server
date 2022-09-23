package com.shenkar.gamelobby.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.shenkar.gamelobby.utils.GlobalFunctions;
import com.shenkar.gamelobby.utils.GlobalVariables;
import com.shenkar.gamelobby.utils.RedisApi;
import com.shenkar.gamelobby.utils.GlobalEnums.Enviroment;

public class MatchingService {
	private Integer roomIdCounter = 10000;
	public static String match_room(Map<String,Object> _Data) {
		//retrieve all possible rooms compare if there is already a userid with a room
		//_Data will contain the user's details and we will first look if exists any room from redis
		Map<String,String> all_rooms = RedisApi.GetOpenRooms();
		String uid = _Data.get("UserId").toString();
		if(all_rooms.isEmpty()) {
			Map<String,String> searchDataRoom = new LinkedHashMap<String,String>();
			searchDataRoom.put("status", "waiting");
			searchDataRoom.put("Uid1", uid);
			RedisApi.SetSearchData(uid, searchDataRoom);
		}
		else {
			//here we are looking for a waiting room
			Boolean found_opponent = false;
			for(String room_key : all_rooms.keySet()) {
				if(all_rooms.get(room_key).equals("waiting")) {
					RedisApi.updateRoomStatus(room_key, "busy");
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
				Map<String,String> searchDataRoom = new LinkedHashMap<String,String>();
				searchDataRoom.put("status", "waiting");
				searchDataRoom.put("Uid1", _Data.get("UserId").toString());
				RedisApi.addRooms(uid);
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
