package com.shenkar.gameserver.logic;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.websocket.Session;

import com.shenkar.gameserver.models.User;
import com.shenkar.gameserver.threads.GameThread;
import com.shenkar.gameserver.utils.GlobalEnums;
import com.shenkar.gameserver.utils.GlobalFunctions;
import com.shenkar.gameserver.utils.RedisApi;

public class ServerLogic 
{
	
	private Map<String,GameThread> activeRooms;
	
	private static ServerLogic instance;
	public static ServerLogic getInstance()
	{
		if(instance == null)
			instance = new ServerLogic();
		return instance;
	}
	
	public ServerLogic()
	{
		activeRooms = new LinkedHashMap<String, GameThread>();
	}
	
	public void AddRoom(String _MatchId,GameThread _Room)
	{
		if(activeRooms != null)
			activeRooms.put(_MatchId, _Room);
	}
	
	public GameThread GetRoom(String _MatchId)
	{return activeRooms.get(_MatchId);}
	
	public void RemoveRoom(String _MatchId)
	{ activeRooms.remove(_MatchId);}
	
	public Boolean RoomExist(String _MatchId)
	{
		if(GetRoom(_MatchId) != null)
			return true;
		return false;
	}
	
	public String OnMessage(String _Message,Session _Session)
	{
		System.out.println("onMessage " + _Message);
		Map<String, Object> _response = new HashMap<String, Object>();
		Map<String, Object> _details = GlobalFunctions.DeserializeJson(_Message);
		String _userId = LoggedUsersLogic.getInstance().getSessionUser(_Session.getId());
		User _user = LoggedUsersLogic.getInstance().getLoggedUser(_userId);
		
		if(_details.containsKey("Service"))
		{
			switch(_details.get("Service").toString())
			{
				case "Login":_response = Login(_details,_Session);break;
				case "Register":_response = Register(_details);break;
				case "ReadyToPlay":_response = ReadyToPlay(_user,_details);break;
				case "SendMove":_response = SendMove(_user,_details);break;
				case "StopGame":_response = StopGame(_userId,_details);break;
			}
			_response.put("Response", _details.get("Service"));
		}
		String _retJSON = GlobalFunctions.SerializeToJson(_response);
		return _retJSON;
	}
	
	private Map<String,Object> Register(Map<String, Object> _Details)
	{
		Map<String,Object> _ret = new LinkedHashMap<String, Object>();
		try
		{
			if(_Details.containsKey("Email") && _Details.containsKey("Password")) 
			{
				System.out.println("Success");
				String _email = _Details.get("Email").toString();
				String _password = _Details.get("Password").toString();
				Map<String,String> _loginData = RedisApi.GetUserData(_email);
			
				if(_loginData.containsKey("Email") == false)
				{
					Map<String,String> _userData = new LinkedHashMap<String, String>();
					_userData.put("Password", _password);
					_userData.put("Email", _email);
					_userData.put("CreatedTime", GlobalFunctions.GetUTCDate());
					_userData.put("UserId", GlobalFunctions.CreateUserUUID());
					RedisApi.SetUserData(_email,_userData);
					_ret.put("IsCreated", true);
				}
				else
				{
					_ret.put("IsCreated", false);
					_ret.put("ErrorCode", "User Already Exist");
				}
			}
			else
			{
				_ret.put("IsCreated", false);
				_ret.put("ErrorCode", "Missing Variables");
			}
		}
		catch(Exception e) 
		{
			_ret.put("IsCreated", false);
			_ret.put("ErrorCode", e.getMessage());
		}
		
		return _ret;
	}
	
	private Map<String,Object> Login(Map<String, Object> _Details,Session _Session)
	{
		Map<String,Object> _ret = new LinkedHashMap<String, Object>(); 
		try
		{
			if(_Details.containsKey("Email") && _Details.containsKey("Password"))
			{
				String _email = _Details.get("Email").toString();
				String _password = _Details.get("Password").toString();
				Map<String,String> _loginData = RedisApi.GetUserData(_email);
				
				if(_loginData.containsKey("Email"))
				{
					if(_loginData.containsKey("Password") && _loginData.get("Password").toString().equals(_password))
					{	
						_ret.put("IsLoggedIn", true);
						_ret.put("UserId", _loginData.get("UserId"));
						LoggedUsersLogic.getInstance().addSessionUser(_Session.getId(),_loginData.get("UserId"));
						User _user = new User(_Session, _loginData.get("UserId"));
						LoggedUsersLogic.getInstance().UpdateLoggedUser(_loginData.get("UserId"), _user);
					}
					else 
					{
						_ret.put("IsLoggedIn", false);
						_ret.put("ErrorCode", "Wrong Password");
					}
				}
				else
				{
					_ret.put("IsLoggedIn", false);
					_ret.put("ErrorCode", "User Doesnt Exist");
				}
			}
			else
			{
				_ret.put("IsLoggedIn", false);
				_ret.put("ErrorCode", "Missing Variables");
			}
		}
		catch(Exception e) 
		{
			_ret.put("IsLoggedIn", false);
			_ret.put("ErrorCode", e.getMessage());
		};
		
		return _ret;
	}
		
	private Map<String,Object> ReadyToPlay(User _User,Map<String, Object> _Details)
	{
		Map<String,Object> _ret = new LinkedHashMap<String, Object>();
		if(_Details.containsKey("MatchId"))
			MatchingLogic.getInstance().ReadyToPlay(_User, _Details.get("MatchId").toString());
		else _ret.put("ErrorCode", GlobalEnums.errorCodes.MissingMatchId.getValue());
		
		return _ret;
	}
	
	private Map<String, Object> SendMove(User _User, Map<String, Object> _Details) 
	{
		Map<String, Object> _retData = new LinkedHashMap<String, Object>();
		try
		{
			if(_User != null && _Details.containsKey("Index"))
			{
				String _matchId = LoggedUsersLogic.getInstance().getUserToMatchId(_User.getUserId());
				GameThread _room = activeRooms.get(_matchId);
				if(_room != null)
				{
					if(_room.getIsRoomActive())
					{
						_retData = _room.ReceivedMove(_User.getUserId(),_Details.get("Index").toString());
					}
					else _retData.put("ErrorCode", GlobalEnums.errorCodes.RoomClosed.getValue());
				}
				else _retData.put("ErrorCode", GlobalEnums.errorCodes.RoomDoesntExist.getValue());
			}
			else _retData.put("ErrorCode", GlobalEnums.errorCodes.MissingVariables.getValue());
		}
		catch(Exception e) {System.out.println("(SendMove) Error: " + e.getMessage());}
		
		return _retData;
	}
	

	private Map<String, Object> StopGame(String _UserId,Map<String, Object> _Details) 
	{
		Map<String, Object> _retData = new LinkedHashMap<String, Object>();
		
		try
		{
			if(_Details.containsKey("Winner"))
			{
				String _matchId = LoggedUsersLogic.getInstance().getUserToMatchId(_UserId);
				GameThread _room = activeRooms.get(_matchId);
				if(_room != null)
				{
					if(_room.getIsRoomActive()) {
						_room.StopGame(_Details.get("Winner").toString());
						_retData.put("Gems", RedisApi.GetUserData(_Details.get("Winner").toString()).get("Gems"));
					}
					else _retData.put("ErrorCode", GlobalEnums.errorCodes.RoomClosed.getValue());
				}
				else _retData.put("ErrorCode", GlobalEnums.errorCodes.RoomDoesntExist.getValue());
			}
			else _retData.put("ErrorCode", GlobalEnums.errorCodes.MissingVariables.getValue());
		}
		catch(Exception e)
		{
			System.out.println("(StopGame) Error:" + e.getMessage());
			_retData.put("ErrorCode", GlobalEnums.errorCodes.Unknown);
		}
		
		return _retData;
	}


	
	
	
	
	
	
	
}
