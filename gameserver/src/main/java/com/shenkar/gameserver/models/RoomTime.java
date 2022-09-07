package com.shenkar.gameserver.models;

public class RoomTime
{
	private Integer globalTurnTime;

	private Integer currentTurnTime;

	private Integer maxTurnTime;
		
	public RoomTime(Integer _MaxTurnTime)
	{
		globalTurnTime = 0;
		currentTurnTime = 0;
		maxTurnTime = _MaxTurnTime;
	}
	
	public Integer getGlobalTurnTime() {
		return globalTurnTime;
	}
	public Integer getCurrentTurnTime() {
		return currentTurnTime;
	}
	public Integer getMaxTurnTime() {
		return maxTurnTime;
	}
	
	public Boolean IsCurrentTimeActive()
	{
		globalTurnTime++;
		currentTurnTime++;
		if(currentTurnTime <= maxTurnTime)
			return true;
		else return false;
	}
	
	public void ResetTurnTime()
	{
		currentTurnTime = 0;
	}
}
