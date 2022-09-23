package com.shenkar.gameserver.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import com.shenkar.gameserver.models.SearchData;
import com.shenkar.gameserver.threads.MatchingThread;

public class SearchingLogic 
{
	private ArrayList<String> searchingList;
	private MatchingThread matchingThread;
	
	private static SearchingLogic instance;
	public static SearchingLogic getInstance()
	{
		if(instance == null)
			instance = new SearchingLogic();
		return instance;
	}
	
	public SearchingLogic()
	{
		searchingList = new ArrayList<String>();
		
		matchingThread = new MatchingThread();
		matchingThread.StartThread();
	}
	public void addToSearchList(String _UserId)
	{
		searchingList.add(_UserId);
	}
	
	public void rmvFromSearchList(String _UserId)
	{
		searchingList.remove(_UserId);
	}
	
	public List<SearchData> GetSearchingList()
	{
		List<SearchData> _searchData = new ArrayList<SearchData>();
		if(searchingList != null)
		{
			for(int i = 0 ; i < searchingList.size(); i++)
				_searchData.add(new SearchData(searchingList.get(i)));
		}
		return SortSearchDataList(_searchData);
	}
	
	protected List<SearchData> SortSearchDataList(List<SearchData> _Data)
    { 
        Collections.sort(_Data, new Comparator<SearchData>() {
      	  public int compare(SearchData c1, SearchData c2) {
      	    if (c1.getRating() > c2.getRating()) return -1;
      	    if (c1.getRating() < c2.getRating()) return 1;
      	    return 0;
      	  }});
      
    	return _Data;
    }
}
