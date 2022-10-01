package com.shenkar.gameserver.utils;

public class GlobalVaribles 
{
	private Integer matchingBet = 50;
	public Integer getMatchingBet() {return matchingBet;}
	
	private Integer turnTime = 10;
	public Integer getTurnTime() 
	{return turnTime;}
	
	private Integer detroyTime = 500;
	public Integer getDetroyTime() 
	{return detroyTime;}

	private static GlobalVaribles instance;
	public static GlobalVaribles getInstance()
	{
		if(instance == null)
			instance = new GlobalVaribles();
		return instance;
	}
	
	public GlobalVaribles()
	{}
	
	
}
