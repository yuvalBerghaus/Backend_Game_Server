package com.shenkar.gamelobby.controllers;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shenkar.gamelobby.utils.GlobalEnums;
import com.shenkar.gamelobby.utils.GlobalFunctions;
import com.shenkar.gamelobby.utils.GlobalVariables;
import com.shenkar.gamelobby.utils.RedisApi;


public class addGems extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public addGems() {
        super();
        // TODO Auto-generated constructor stub
    }
//hi

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		addGems_component(request, response);
	}
	protected void addGems_component(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> _ret = new LinkedHashMap<String, Object>(); 
		try
		{
			String _message = request.getParameter("Data");
			Map<String,Object> _parsedJson = GlobalFunctions.DeserializeJson(_message);
			if(_parsedJson.containsKey("UserId"))
			{
				String uid = _parsedJson.get("UserId").toString();
				String gems_recieved = _parsedJson.get("Gems").toString();
				Map<String,String> _loginData = RedisApi.GetUserData(_parsedJson.get("UserId").toString());
				if(_loginData.get("UserId").toString().equals(_parsedJson.get("UserId").toString())) {
					String gems = _loginData.get("Gems").toString();
					Double _current_amount_gems = Double.valueOf(gems);
					Double _current_gems_recieved = Double.valueOf(gems_recieved);
					_current_amount_gems += _current_gems_recieved;
					_loginData.put("Gems", _current_amount_gems.toString());
					RedisApi.SetUserData(_loginData.get("UserId").toString(), _loginData);
					_ret.put("Response", "AddGems");
					_ret.put("IsCreated", true);
					_ret.put("Gems", _current_amount_gems.toString());
				}
				else
				{
					_ret.put("IsLoggedIn", false);
					_ret.put("ErrorCode", GlobalEnums.errorCodes.UserDoesntExist.getStrValue());
				}
			}
			else
			{
				_ret.put("IsLoggedIn", false);
				_ret.put("ErrorCode", "Missing Variables");
			}
		}
		catch(Exception e) 
		{
			_ret.put("IsLoggedIn", false);
			_ret.put("ErrorCode", GlobalEnums.errorCodes.Unknown.getStrValue());
		};
		GlobalFunctions.GlobalResponse(response, _ret);
	}

}
