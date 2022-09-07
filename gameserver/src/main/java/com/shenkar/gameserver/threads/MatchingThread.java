package com.shenkar.gameserver.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.shenkar.gameserver.logic.LoggedUsersLogic;
import com.shenkar.gameserver.logic.MatchingLogic;
import com.shenkar.gameserver.logic.SearchingLogic;
import com.shenkar.gameserver.models.MatchData;
import com.shenkar.gameserver.models.SearchData;
import com.shenkar.gameserver.models.User;
import com.shenkar.gameserver.models.User.UserState;
import com.shenkar.gameserver.utils.GlobalFunctions;
import com.shenkar.gameserver.utils.GlobalVaribles;

public class MatchingThread implements Runnable
{
	private Boolean isMatchingRunning = false;
	private Integer matchId;
	private Thread thread;
	
	
	public MatchingThread()
	{
		matchId = 1;
	}
	
	public void StartThread()
	{
		isMatchingRunning = true;
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() 
	{
		while(isMatchingRunning)
		{
			try
			{
				MatchingPlayers();
				Thread.sleep(1000);
			}
			catch(Exception e){}
		}
	}
	
	private void MatchingPlayers()
	{
		try
		{
			Boolean _shouldContinue = true;
			//Sorted list
			List<SearchData> _searchers = SearchingLogic.getInstance().GetSearchingList();
			for(int i=0; i < _searchers.size(); i++)
			{
				_shouldContinue = true;
				SearchData _firstUser = _searchers.get(i);
				for(int j = i + 1; j < _searchers.size() && _shouldContinue; j++)
				{
					SearchData _secondUser = _searchers.get(j);
					if(RatingMatched(_firstUser.getRating(),_secondUser.getRating()))
					{
						List<User> _matchedUsers = Arrays.asList(
								LoggedUsersLogic.getInstance().getLoggedUser(_firstUser.getUserId()),
								LoggedUsersLogic.getInstance().getLoggedUser(_secondUser.getUserId()));
						
						if(CheckIfUserIsValid(_matchedUsers.get(0)) && CheckIfUserIsValid(_matchedUsers.get(1)))
						{
							LinkedHashMap<String, Object> _data = new LinkedHashMap<String, Object>();
							_data.put("Service", "ReadyToPlay");
							_data.put("TempMatchId", ++matchId);
							String _broadcastMessage = GlobalFunctions.SerializeToJson(_data);
							
							List<Boolean> _success = new ArrayList<Boolean>();
							for(User _u : _matchedUsers)
								_success.add(LoggedUsersLogic.getInstance().SendMessage(_u.getSession(), _broadcastMessage));
							
							if(_success.size() == 2 && _success.get(0) && _success.get(1))
							{
								MatchData _matchData = new MatchData(_firstUser.getUserId(), _secondUser.getUserId(),matchId,
										_firstUser.getRating(),_secondUser.getRating());
								MatchingLogic.getInstance().UpdateMatchData(matchId.toString(), _matchData);
								
								for(User _u : _matchedUsers)
								{	
									SearchingLogic.getInstance().rmvFromSearchList(_u.getUserId());
									LoggedUsersLogic.getInstance().getLoggedUser(_u.getUserId()).setState(UserState.PreStartGame);
								}
								_shouldContinue = false;
							}
							else
							{
								LinkedHashMap<String, Object> _cancelData = new LinkedHashMap<String, Object>();
								_data.put("Service", "CancelMatch");
								_data.put("TempMatchId", matchId);
								String _broadcastCancelMessage = GlobalFunctions.SerializeToJson(_data);
								for(User _u : _matchedUsers)
									LoggedUsersLogic.getInstance().SendMessage(_u.getSession(), _broadcastCancelMessage);
							}
						}
					}
				}
			}
			
		}
		catch(Exception e)
		{
			System.out.println("MatchingPlayers " + e.getMessage());
			e.printStackTrace();
		}
		
	}

	private Boolean CheckIfUserIsValid(User _User) 
	{
		if(_User != null && _User.getState() == UserState.Searching && _User.getSession() != null && 
				_User.getSession().isOpen())
			return true;
		return false;
	}

	private boolean RatingMatched(Integer _Rating1, Integer _Rating2) 
	{
		Integer _calc = Math.abs(_Rating1 - _Rating2);
		if(_calc <= GlobalVaribles.getInstance().getMatchingRating())
			return true;
		return false;
	}

}
