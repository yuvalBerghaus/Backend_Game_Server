package com.shenkar.gameserver.utils;

public class GlobalVaribles 
{
	private Integer matchingRating = 50;
	public Integer getMatchingRating() {return matchingRating;}
	
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
