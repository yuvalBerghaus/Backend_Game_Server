package com.shenkar.gameserver.models;

import javax.websocket.Session;

public class User
{
	public enum UserState
	{
		Idle,Disconnected, Searching, PreStartGame, Playing
	};
	
	private UserState state;
	private Session session;
	private String userId;
	public User(Session _Session,String _UserId)
	{
		session = _Session;
		userId = _UserId;
		state= state.Idle;
	}

	public UserState getState() {
		return state;
	}

	public void setState(UserState state) {
		this.state = state;
	}

	public Session getSession() {
		return session;
	}

	public String getUserId() {
		return userId;
	}
	
	
}
