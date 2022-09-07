package com.shenkar.gameserver.models;

public class SearchData 
{
	private String userId;
	private Integer rating;
	
	public SearchData(String _UserId,Integer _Rating)
	{
		userId = _UserId;
		rating = _Rating;
	}
	
	public String getUserId() {
		return userId;
	}
	public Integer getRating() {
		return rating;
	}
}
