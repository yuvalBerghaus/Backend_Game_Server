package com.shenkar.gamelobby.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shenkar.gamelobby.utils.GlobalEnums;
import com.shenkar.gamelobby.utils.GlobalEnums.Enviroment;
import com.shenkar.gamelobby.utils.GlobalFunctions;
import com.shenkar.gamelobby.utils.GlobalVariables;
import com.shenkar.gamelobby.utils.RedisApi;

@WebServlet("/searchingOpponent")
public class searchingController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public searchingController() {super();}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SearchingOpponent(request, response);
	}

	private void SearchingOpponent(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String,Object> _ret = new LinkedHashMap<String, Object>(); 
		try
		{
			String _ws = "ws://localhost:8168/gameserver/game/";
			if(GlobalVariables.curEnviroment == Enviroment.Development)
				_ws = "ws://54.229.248.156:8080/gameserver/game/";
			String _message = request.getParameter("Data");
			Map<String,Object> _parsedJson = GlobalFunctions.DeserializeJson(_message);
			if(_parsedJson.containsKey("UserId"))
			{
				String _userId = _parsedJson.get("UserId").toString();
				if(_userId.equals("") == false)
				{
					MatchingService.match_room(_parsedJson);
					System.out.println("Hola como estas?");
					_ret.put("ConnectionUrl",_ws);
					_ret.put("IsSearching", true);
				}
				else
				{
					_ret.put("IsSearching", false);
					_ret.put("ErrorCode", GlobalEnums.errorCodes.MissingUserId.getValue());
				}
			}
			else
			{
				_ret.put("IsSearching", false);
				_ret.put("ErrorCode", GlobalEnums.errorCodes.MissingUserId.getValue());
			}
		}
		catch(Exception e) 
		{
			_ret.put("IsSearching", false);
			_ret.put("ErrorMessage", e.getMessage());
			_ret.put("ErrorCode", GlobalEnums.errorCodes.Unknown.getValue());
		};
		
		_ret.put("Response", "SearchingOpponent");
		String _retString = GlobalFunctions.SerializeToJson(_ret);
		try 
		{
			PrintWriter out = response.getWriter();
		    out.println(_retString);
		} 
		catch (IOException e) {}
	}
}
