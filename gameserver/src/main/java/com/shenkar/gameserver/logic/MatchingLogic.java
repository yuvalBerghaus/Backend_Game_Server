package com.shenkar.gameserver.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.shenkar.gameserver.models.MatchData;
import com.shenkar.gameserver.models.User;
import com.shenkar.gameserver.threads.GameThread;
import com.shenkar.gameserver.utils.GlobalVaribles;
import com.shenkar.gameserver.utils.RedisApi;

public class MatchingLogic 
{
	private LinkedHashMap<String, MatchData> allMatchesData;
	
	private static MatchingLogic instance;
	public static MatchingLogic getInstance()
	{
		if(instance == null)
			instance = new MatchingLogic();
		return instance;
	}
	
	public MatchingLogic()
	{
		allMatchesData = new LinkedHashMap<String, MatchData>();
	}

	public void UpdateMatchData(String _MatchId,MatchData _Value)
	{
		allMatchesData.put(_MatchId, _Value);
	}
	private void RemoveMatchData(String _MatchId)
	{
		allMatchesData.remove(_MatchId);
	}
	public MatchData GetMatchData(String _MatchId)
	{
		return allMatchesData.get(_MatchId);
	}
	
	public LinkedHashMap<String, Object> ReadyToPlay(User _User,String _MatchId)
	{
		LinkedHashMap<String, Object> _ret = new LinkedHashMap<String, Object>();
		try
		{
			MatchData _curMatchData = GetMatchData(_MatchId);
			if(_curMatchData != null)
			{
				_ret.put("IsSuccess", true);
				_curMatchData.ChangePlayerReady(_User.getUserId(), true);
				if(_curMatchData.IsAllReady()) //Thats an if statement that will enable the game to start between both players
				{
					RemoveMatchData(_MatchId);
					_ret = CreatingMatchLogic(_curMatchData);
				}
			}
		}
		catch(Exception e){System.out.println(e.getLocalizedMessage());}
		
		return _ret;
	}

	private LinkedHashMap<String, Object> CreatingMatchLogic(MatchData _curMatchData) 
	{
		LinkedHashMap<String, Object> _ret = new LinkedHashMap<String, Object>();
		
		//Insert To DB and return MatchId of the DB
		String _matchId = CreateRoom(_curMatchData.getUserId1(),_curMatchData.getUserId2(),
				_curMatchData.getBet1().toString(),
				_curMatchData.getBet2().toString());
		//Create Room
		
		int _turnTime = GlobalVaribles.getInstance().getTurnTime();
		int _destroyTime = GlobalVaribles.getInstance().getDetroyTime();
		User _u1 = LoggedUsersLogic.getInstance().getLoggedUser(_curMatchData.getUserId1());
		User _u2 = LoggedUsersLogic.getInstance().getLoggedUser(_curMatchData.getUserId2());
		List<User> _users = Arrays.asList(_u1,_u2);
		List<Integer> _bets = Arrays.asList(_curMatchData.getBet1(),_curMatchData.getBet2());
		GameThread _gameRoom = new GameThread(_matchId,_users,_bets,_turnTime,_destroyTime);
		ServerLogic.getInstance().AddRoom(_matchId, _gameRoom);
		_gameRoom.StartGame();
		
		//StartGame
		_ret.put("Service", "StartMatch");
		_ret.put("MatchId", _matchId);
		return _ret;
	}
	
	private String CreateRoom(String _UserId1,String _UserId2,String _bet1,String _bet2)
	{
		Integer _matchId = 1;
		String _redisId = RedisApi.GetMatchId();
		if(_redisId.equals("") == false)
			_matchId = Integer.parseInt(_redisId) + 1;
		
		Map<String,String> _newMatch = new LinkedHashMap<String, String>();
		_newMatch.put("MatchId", _matchId.toString());
		_newMatch.put("UserId1", _UserId1);
		_newMatch.put("UserId2", _UserId2);
		_newMatch.put("Bet1", _bet1);
		_newMatch.put("Bet2", _bet2);
	
		RedisApi.SetRoomData(_matchId.toString(),_newMatch);
		RedisApi.SetMatchId(_matchId.toString());
		return _matchId.toString();
	}
}
