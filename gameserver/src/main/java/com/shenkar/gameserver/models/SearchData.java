package com.shenkar.gameserver.models;

public class SearchData 
{
	private String userId;
	private Integer bet;
	
	public SearchData(String _UserId,Integer _bet)
	{
		userId = _UserId;
		bet = _bet;
	}
	
	public String getUserId() {
		return userId;
	}
	public Integer getBet() {
		return bet;
	}
}
