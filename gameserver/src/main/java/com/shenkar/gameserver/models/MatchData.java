package com.shenkar.gameserver.models;

import com.shenkar.gameserver.utils.GlobalFunctions;

public class MatchData 
{
	private String userId1;
	private String userId2;
	private Integer matchId;
	private String matchedDate;
	private Integer bet1;
	private Integer bet2;

	private Boolean isUser1Ready;
	private Boolean isUser2Ready;
	
	public MatchData(String _UserId1,String _UserId2,Integer _MatchId,Integer _bet1,Integer _bet2)
	{
		userId1 = _UserId1;
		userId2 = _UserId2;
		matchId = _MatchId;
		matchedDate = GlobalFunctions.GetUTCDate();
		isUser1Ready = false;
		isUser2Ready = false;
		bet1 = _bet1;
		bet2 = _bet2;
	}
	
	public String getUserId1() {
		return userId1;
	}

	public String getUserId2() {
		return userId2;
	}

	public Integer getMatchId() {
		return matchId;
	}

	public String getMatchedDate() {
		return matchedDate;
	}


	public Integer getBet1() {
		return bet1;
	}

	public Integer getBet2() {
		return bet2;
	}

	public Boolean getIsUser1Ready() {
		return isUser1Ready;
	}

	public Boolean getIsUser2Ready() {
		return isUser2Ready;
	}

	public void ChangePlayerReady(String _UserId,Boolean _IsReady) 
	{
		if(_UserId.equals(userId1))
			isUser1Ready = _IsReady;
		else if(_UserId.equals(userId2))
			isUser2Ready = _IsReady;
	}
	
	public Boolean IsAllReady()
	{
		if(isUser1Ready && isUser2Ready)
		{
			return true;
		}
		
		return false;
	}
}
