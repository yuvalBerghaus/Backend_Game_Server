package com.shenkar.gameserver.threads;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.shenkar.gameserver.logic.LoggedUsersLogic;
import com.shenkar.gameserver.logic.ServerLogic;
import com.shenkar.gameserver.models.RoomTime;
import com.shenkar.gameserver.models.User;
import com.shenkar.gameserver.models.User.UserState;
import com.shenkar.gameserver.utils.GlobalEnums;
import com.shenkar.gameserver.utils.GlobalFunctions;
import com.shenkar.gameserver.utils.RedisApi;

public class GameThread implements Runnable
{
	private Thread currentThread;
	private String matchId;
	private int turnTime;
	private int destroyTime;
	private List<User> users;
	private List<String> usersIds;
	private List<Integer> ratings;
	private Boolean isRoomActive;
	private Boolean isDetroyThread;
	private Date startDate;
	private int curIndex;
	private Integer moveCounter;
	private RoomTime roomTime;
	
	public GameThread(String _MatchId,List<User> _Users,List<Integer> _Ratings,int _TurnTime,int _DestroyTime)
	{
		matchId = _MatchId;
		turnTime = _TurnTime;
		destroyTime = _DestroyTime;
		users = _Users;
		ratings = _Ratings;
		isRoomActive = false;
		moveCounter = 0;
		roomTime = new RoomTime(_TurnTime);
		
		usersIds = new ArrayList<String>();
		for(int i=0; i < users.size();i++)
		{
			if(users.get(i) != null)
			{
				usersIds.add(users.get(i).getUserId());
				users.get(i).setState(UserState.Playing);
				LoggedUsersLogic.getInstance().UpdateLoggedUser(users.get(i).getUserId(), users.get(i));
				LoggedUsersLogic.getInstance().setUserToMatchId(users.get(i).getUserId(), _MatchId);
				
				Map<String,String> _redisData = new LinkedHashMap<String, String>();
				_redisData.put("MatchId", _MatchId);
				//need to save also path to server
				RedisApi.SetUserMatchData(users.get(i).getUserId(), _redisData);
			}
		}
	}
	
	public Boolean getIsRoomActive()
	{return isRoomActive;}
	
	@Override
	public void run() 
	{
		while(isRoomActive)
		{
			try
			{
				if(roomTime.IsCurrentTimeActive() == false)
					ChangeTurn();
				
				if(isDetroyThread && destroyTime < roomTime.getGlobalTurnTime())
					StopGame("Tie");
				
				Thread.sleep(1000);
			}
			catch(Exception e) {System.out.println("(run) Error: " + e.getMessage());}
		}
	}
	
	private void ChangeTurn() 
	{
		PassTurn();
		Map<String, Object> _notifyData = new LinkedHashMap<String, Object>();
		_notifyData.put("Service", "PassTurn");
		_notifyData.put("CP", usersIds.get(curIndex));		//Current Player
		_notifyData.put("MC", moveCounter);				//Move Counter
		String _toSend = GlobalFunctions.SerializeToJson(_notifyData);
		BroadcastToRoom(_toSend);
		
		RedisApi.SetGameMoveResponse(matchId, moveCounter.toString(), _toSend);
	}

	public void StartGame()
	{
		try
		{
			startDate = GlobalFunctions.GetUTCDateTime();
			curIndex = GlobalFunctions.GetRandomNumber(0,1);
			
			Map<String,Object> _retData = new LinkedHashMap<String, Object>();
			_retData.put("Service", "StartGame");
			_retData.put("MI", matchId);					//MatchId
			_retData.put("TT", startDate);					//StartDate
			_retData.put("MTT", turnTime);			    	//Max Turn Time
			_retData.put("CP", usersIds.get(curIndex));		//Current Player
			_retData.put("Players", usersIds);				//PL
			_retData.put("MC", moveCounter);				//Move Counter
			
			String _toSend = GlobalFunctions.SerializeToJson(_retData);
			BroadcastToRoom(_toSend);
			
			RedisApi.SetGameMoveResponse(matchId, moveCounter.toString(), _toSend);
			
			isRoomActive = true;
			isDetroyThread = true;
			currentThread = new Thread(this);
			currentThread.start();
		}
		catch(Exception e) {System.out.println("(StartGame) Error: " + e.getMessage());};
	}

	private void BroadcastToRoom(String _toSend)
	{
		try
		{
			for(User user: users)
				if(user != null)
					LoggedUsersLogic.getInstance().SendMessage(user.getSession(), _toSend);
		}
		catch(Exception e) {System.out.println("(BroadcastToRoom) Error: " + e.getMessage());}
	}

	public Map<String, Object> ReceivedMove(String _UserId, String _Index) 
	{
		Map<String, Object> _retData = new LinkedHashMap<String, Object>();
		try
		{
			if(users.get(curIndex).getUserId().equals(_UserId))
			{
				PassTurn();
				_retData.put("Service", "BroadcastMove");
				_retData.put("SenderId", _UserId);
				_retData.put("Index", _Index);
				_retData.put("CP", usersIds.get(curIndex));
				_retData.put("MC", moveCounter);
				String _toSend = GlobalFunctions.SerializeToJson(_retData);
				BroadcastToRoom(_toSend);
				
				RedisApi.SetGameMoveResponse(matchId, moveCounter.toString(), _toSend);
			}
			else _retData.put("ErrorCode", GlobalEnums.errorCodes.NotPlayerTurn.getValue());
		}
		catch(Exception e) {System.out.println("(ReceivedMove) Error: " + e.getMessage());}
		
		return _retData;
	}
	
	private void PassTurn()
	{
		moveCounter++;
		curIndex = curIndex == 0 ? 1 : 0;
		roomTime.ResetTurnTime();
	}

	public void StopGame(String _Winner) 
	{
		try
		{
			Map<String, Object> _notifyData = new LinkedHashMap<String, Object>();
			_notifyData.put("Service", "StopGame");
			_notifyData.put("Winner", _Winner);
			_notifyData.put("MC", moveCounter);
			String _toSend = GlobalFunctions.SerializeToJson(_notifyData);
			BroadcastToRoom(_toSend);
			
			RedisApi.SetGameMoveResponse(matchId, moveCounter.toString(), _toSend);
			CloseRoom();
		}
		catch(Exception e) {System.out.println("(StopGame) Error: " + e.getMessage());}
		
		
	}

	private void CloseRoom() 
	{
		isDetroyThread = false;
		isRoomActive = false;
		ServerLogic.getInstance().RemoveRoom(matchId);
		
		for(int i=0; i < users.size();i++)
		{
			Map<String,String> _data = RedisApi.GetUserMatchData(users.get(i).getUserId());
			if(_data.size() >  0)
			{
				String _matchId = _data.get("MatchId").toString();
				if(_matchId.equals(matchId))
				{
					RedisApi.RmvUserMatchData(users.get(i).getUserId());
					LoggedUsersLogic.getInstance().removeLoggedUser(users.get(i).getUserId());
					LoggedUsersLogic.getInstance().removeSessionUser(users.get(i).getSession().getId());
				}
			}
		}
		System.out.println("CloseRoom " + LoggedUsersLogic.getInstance().getLoggedUsers().size() +
				" " + LoggedUsersLogic.getInstance().getSessionsUsers().size());
	}

}















