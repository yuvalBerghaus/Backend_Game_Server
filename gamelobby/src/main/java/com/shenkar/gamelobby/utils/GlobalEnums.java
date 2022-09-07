package com.shenkar.gamelobby.utils;

public class GlobalEnums 
{
	public enum Enviroment{
		local,Development
	}
	
	public enum errorCodes
	{
		Unknown(2000),UserDoesntExist(2001),MissingUserId(2002);
		
		private final int errorCode;
		errorCodes(int errorCode){this.errorCode = errorCode;}
		public int getValue() { return errorCode; }
		public String getStrValue() {return((Integer)errorCode).toString();}
	};

}
