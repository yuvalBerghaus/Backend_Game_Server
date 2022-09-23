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


public class codeLoginValidation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public codeLoginValidation() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Login_Validation(request, response);
	}
	protected void Login_Validation(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String,Object> _ret = new LinkedHashMap<String, Object>(); 
		try
		{
			String _message = request.getParameter("Data");
			Map<String,Object> _parsedJson = GlobalFunctions.DeserializeJson(_message);

			if(_parsedJson.containsKey("Code"))
			{
				Map<String,String> data_to_login = RedisApi.GetUserData(_parsedJson.get("Code").toString());
				Map<String,String> _loginData = RedisApi.GetUserData(data_to_login.get("Code").toString());
				if(_parsedJson.get("Code").toString().equals(_loginData.get("Code").toString())) {
					String user_id = _loginData.get("UserId").toString();
					_loginData = RedisApi.GetUserData(user_id);
					_ret.put("Response", "CodeValidation");
					_ret.put("IsCreated", true);
					_ret.put("NickName", _loginData.get("NickName"));
					_ret.put("Gems", _loginData.get("Gems"));
					_ret.put("UserId", _loginData.get("UserId"));
					String last_date = null;
					String current_date = null;
					Integer sequence_day = 0;
					Double daily_bonus = 0.0;
					Double gems = 0.0;
					long diffDays = 0;
					 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
					 OffsetDateTime now = OffsetDateTime.now(); 
					  current_date = dtf.format(now);
						if(!_loginData.containsKey("Date")) { 
							_loginData.put("sequence_day", "0");
							_loginData.put("Gems", "0.0");
							_loginData.put("Date", current_date);
						}
						else {
							gems = Double.parseDouble(_loginData.get("Gems"));
							  last_date = _loginData.get("Date");
							  diffDays = GlobalFunctions.diffDays(current_date, last_date);
							  if(diffDays == 1) {
								  if(Integer.parseInt(_loginData.get("sequence_day")) > 7) {
									  _loginData.put("sequence_day", "0");
								  }
								  else {
									  sequence_day = Integer.parseInt(_loginData.get("sequence_day"));
									  sequence_day++;
									  daily_bonus =  sequence_day.doubleValue() * 10.0;
									  gems += daily_bonus;
									  _loginData.put("sequence_day", sequence_day.toString()); 
									  _loginData.put("Gems", gems.toString());
								  }
							  }
							  else if(diffDays > 1) {
								  _loginData.put("sequence_day", "0");
								  
							  }
							  _ret.put("Gems", _loginData.get("Gems"));
							  _loginData.put("Date", current_date);
							  //								i
						}
						RedisApi.SetUserData(_loginData.get("UserId"), _loginData);
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
//		String _retString = GlobalFunctions.SerializeToJson(_ret);
//		try 
//		{
//			PrintWriter out = response.getWriter();
//		    out.println(_retString);
//		} 
//		catch (IOException e) {}
	}

}
