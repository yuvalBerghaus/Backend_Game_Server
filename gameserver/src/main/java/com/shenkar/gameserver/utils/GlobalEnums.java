package com.shenkar.gameserver.utils;

public class GlobalEnums 
{
	public enum Enviroment{
		local,Development
	}
	
	public enum errorCodes
	{
		Unknown(3000),MissingMatchId(3001),RoomClosed(3002),RoomDoesntExist(3003),MissingVariables(3004),
		NotPlayerTurn(3005);
		
		private final int errorCode;
		errorCodes(int errorCode){this.errorCode = errorCode;}
		public int getValue() { return errorCode; }
		public String getStrValue() {return((Integer)errorCode).toString();}
	};

}
