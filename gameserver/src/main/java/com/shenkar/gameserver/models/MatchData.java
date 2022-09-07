package com.shenkar.gameserver.models;

import com.shenkar.gameserver.utils.GlobalFunctions;

public class MatchData 
{
	private String userId1;
	private String userId2;
	private Integer matchId;
	private String matchedDate;
	private Integer rating1;
	private Integer rating2;

	private Boolean isUser1Ready;
	private Boolean isUser2Ready;
	
	public MatchData(String _UserId1,String _UserId2,Integer _MatchId,Integer _Rating1,Integer _Rating2)
	{
		userId1 = _UserId1;
		userId2 = _UserId2;
		matchId = _MatchId;
		matchedDate = GlobalFunctions.GetUTCDate();
		isUser1Ready = false;
		isUser2Ready = false;
		rating1 = _Rating1;
		rating2 = _Rating2;
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


	public Integer getRating1() {
		return rating1;
	}

	public Integer getRating2() {
		return rating2;
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
