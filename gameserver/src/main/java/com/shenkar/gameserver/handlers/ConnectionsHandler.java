package com.shenkar.gameserver.handlers;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.shenkar.gameserver.logic.ConnectionLogic;
import com.shenkar.gameserver.logic.ServerLogic;

@ServerEndpoint(value="/game/{Details}")
public class ConnectionsHandler 
{
	@OnOpen
	public void onOpen(@PathParam("Details") String _Details,Session _Session)
	{
		ConnectionLogic.getInstance().OpenConnection(_Details, _Session);
	}
	
	@OnClose
	public void onClose(Session _Session,CloseReason _CloseReason)
	{
		ConnectionLogic.getInstance().ClosedConnection(_Session, _CloseReason);
	}
	
	@OnError
	public void onError(Session _Session,Throwable thr)
	{
		ConnectionLogic.getInstance().OnErrorException(_Session, thr);
	}
	
	@OnMessage
	public String onMessage(String _Message,Session _Session)
	{
		return ServerLogic.getInstance().OnMessage(_Message, _Session);
	}
}

	
	
	