package com.shenkar.gameserver.models;

public class SearchData 
{
	private String userId;
	private Integer rating;
	
	public SearchData(String _UserId)
	{
		userId = _UserId;
	}
	
	public String getUserId() {
		return userId;
	}
	public Integer getRating() {
		return rating;
	}
}
