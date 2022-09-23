package com.shenkar.gamelobby.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.shenkar.gamelobby.utils.GlobalFunctions;
import com.shenkar.gamelobby.utils.GlobalVariables;
import com.shenkar.gamelobby.utils.RedisApi;
import com.shenkar.gamelobby.utils.GlobalEnums.Enviroment;

public class MatchingService {
	private Integer roomIdCounter = 10000;
	public Map<String,Object> getRoomDetails(LinkedHashMap<String,Object> _Data) {
		Map<String,Object> _ret = new LinkedHashMap<String,Object>();
		String _ws = "ws://localhost:8168/gameserver/game/";
		if(GlobalVariables.curEnviroment == Enviroment.Development)
			_ws = "ws://54.229.248.156:8080/gameserver/game/";
		_ret.put("ConnectionUrl",_ws);
		_ret.put("IsSearching", true);
		//retrieve all possible rooms compare if there is already a userid with a room
		//_Data will contain the user's details and we will first look if exists any room from redis
		if(room_userId in Rooms) {
			
		}
		else {
			Map<String,String> _searchData = new LinkedHashMap<String, String>();
			_searchData.put("RequestedTime", GlobalFunctions.GetUTCDate());
			RedisApi.SetSearchData(_Data.get("UserId").toString(), _searchData);	
		}
	}
}
