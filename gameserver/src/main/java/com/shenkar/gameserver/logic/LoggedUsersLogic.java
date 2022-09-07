package com.shenkar.gameserver.logic;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.websocket.Session;

import com.shenkar.gameserver.models.User;

public class LoggedUsersLogic 
{
	private Map<String,String> sessionsUsers;
	private Map<String,User> loggedUser;
	private Map<String,String> usersMatchIds;
	
	
	private static LoggedUsersLogic instance;
	public static LoggedUsersLogic getInstance()
	{
		if(instance == null)
			instance = new LoggedUsersLogic();
		return instance;
	}
	 
	public LoggedUsersLogic()
	{
		sessionsUsers = new LinkedHashMap<String, String>();
		loggedUser = new LinkedHashMap<String, User>();
		usersMatchIds = new LinkedHashMap<String, String>();
	}
	
	public Map<String,String> getSessionsUsers(){return sessionsUsers;}
	public void removeSessionUser(String _Key) {sessionsUsers.remove(_Key);}
	public void addSessionUser(String _Key,String _Value) {sessionsUsers.put(_Key, _Value);}
	public String getSessionUser(String _Key) { return sessionsUsers.get(_Key);}
	public void removeUserSessionUserId(String _UserId)
	{
		User _user = loggedUser.get(_UserId);
		if(_user != null && _user.getSession() != null)
			removeSessionUser(_user.getSession().getId());
	}

	public Map<String,User> getLoggedUsers(){return loggedUser;}
	public void removeLoggedUser(String _Key) {loggedUser.remove(_Key);}
	public void UpdateLoggedUser(String _Key,User _Value) {loggedUser.put(_Key, _Value);}
	public User getLoggedUser(String _Key) { return loggedUser.get(_Key);}
	
	public Boolean SendMessage(Session _UserSession,String _Message)
	{
		try
		{
			if(_UserSession != null && _UserSession.isOpen())
			{	
				_UserSession.getBasicRemote().sendText(_Message);
				return true;
			}
		}
		catch(Exception e) {System.out.println(e.getMessage());}
		return false;
	}
	
	public void setUserToMatchId(String _UserId,String _MatchId)
	{usersMatchIds.put(_UserId, _MatchId);}
	
	public String getUserToMatchId(String _UserId)
	{return usersMatchIds.get(_UserId);}
	
	public void removeUserIdMatchId(String _UserId)
	{usersMatchIds.remove(_UserId);}
}
