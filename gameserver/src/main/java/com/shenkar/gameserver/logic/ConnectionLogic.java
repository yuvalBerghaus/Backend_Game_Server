package com.shenkar.gameserver.logic;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import com.shenkar.gameserver.models.MatchData;
import com.shenkar.gameserver.models.User;
import com.shenkar.gameserver.models.User.UserState;
import com.shenkar.gameserver.utils.GlobalFunctions;
import com.shenkar.gameserver.utils.RedisApi;

public class ConnectionLogic 
{
	private Map<String,Session> connectedUsers;
	private static ConnectionLogic instance;
	public static ConnectionLogic getInstance()
	{
		if(instance == null)
			instance = new ConnectionLogic();
		return instance;
	}
	
	public ConnectionLogic()
	{
		connectedUsers = new LinkedHashMap<String, Session>();
	}

	
	public Map<String, Session> getConnectedUsers() {return connectedUsers;}

	public void OpenConnection(String _Details,Session _Session)
	{
		System.out.println("OpenConnection " + _Details);
		try
		{
			Map<String, Object> _details = GlobalFunctions.DeserializeJson("{" + _Details + "}");
			if(_details != null && _details.containsKey("UserId"))
			{
				connectedUsers.put(_Session.getId(), _Session);
				String _userId = _details.get("UserId").toString();
				
				//Delete old mapping to old session for the user id
				LoggedUsersLogic.getInstance().removeUserSessionUserId(_userId);
				
				//Add a new mapping: session to userid
				LoggedUsersLogic.getInstance().addSessionUser(_Session.getId(), _userId);
				User _user = new User(_Session,_userId);
				_user.setState(UserState.Searching);
				LoggedUsersLogic.getInstance().UpdateLoggedUser(_userId, _user);
				Map<String,String> _searchingData = RedisApi.GetSearchData(_userId);
				System.out.println(_searchingData);
//				if(_searchingData != null && _searchingData.containsKey("Rating"))
				if(_searchingData != null)
				{
					try
					{
						Integer bet = 0;
						if(_searchingData.containsKey("bet2"))
							bet = (int) Double.parseDouble(_searchingData.get("bet2"));
						else if(_searchingData.containsKey("bet1")) {
							bet = (int) Double.parseDouble(_searchingData.get("bet1"));
						}
						SearchingLogic.getInstance().addToSearchList(_userId, bet);
					}
					catch(Exception e) {Disconnect(_Session);};
				}
				else Disconnect(_Session);
			}
			else Disconnect(_Session);
		}
		catch(Exception e)
		{
			System.out.println("OpenConnection " + e.getMessage());
			Disconnect(_Session);
		}
	}

	public void ClosedConnection(Session _Session,CloseReason _CloseReason)
	{
		System.out.println("onClose " + _CloseReason);
		try
		{
			connectedUsers.remove(_Session.getId());
			String _UserId = LoggedUsersLogic.getInstance().getSessionUser(_Session.getId());
			if(_UserId.equals("") == false)
			{
				User _user = LoggedUsersLogic.getInstance().getLoggedUser(_UserId);
				if(_user != null)
				{
					Map<String,String> _matchData = RedisApi.GetUserMatchData(_UserId);
					if(_matchData != null && _matchData.containsKey("MatchId")) 
					{
						MatchData _data = MatchingLogic.getInstance().GetMatchData(_matchData.get("MatchId"));
						if(_data != null)
						{
							_user.setState(User.UserState.Disconnected);
							LoggedUsersLogic.getInstance().UpdateLoggedUser(_UserId, _user);
						}
						else 
						{
							LoggedUsersLogic.getInstance().removeLoggedUser(_UserId);
							SearchingLogic.getInstance().rmvFromSearchList(_UserId);
							//RedisApi.RmvUserMatchData(_UserId);
						}
					}
					else 
					{
						LoggedUsersLogic.getInstance().removeLoggedUser(_UserId);
						SearchingLogic.getInstance().rmvFromSearchList(_UserId);
					}
				}
			}
		}
		catch(Exception e) {}
	}
	
	public void OnErrorException(Session _Session,Throwable thr)
	{
		System.out.println("onError " + thr.getMessage());
	}	

	private void Disconnect(Session _Session) 
	{
		if(_Session != null)
		{
			try {_Session.close();} 
			catch (IOException e) {}
		}
	}

}
