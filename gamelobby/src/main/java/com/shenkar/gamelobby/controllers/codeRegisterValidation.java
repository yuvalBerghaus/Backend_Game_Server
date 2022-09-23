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


public class codeRegisterValidation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public codeRegisterValidation() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		registerCode(request, response);
	}
	protected void registerCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String,Object> _ret = new LinkedHashMap<String, Object>(); 
		try
		{
			String _message = request.getParameter("Data");
			Map<String,Object> _parsedJson = GlobalFunctions.DeserializeJson(_message);
			if(_parsedJson.containsKey("Code"))
			{
				Map<String,String> _userData = new LinkedHashMap<String, String>();
				Map<String,String> _uid_data = new LinkedHashMap<String, String>();
				Map<String,String> _loginData = RedisApi.GetUserData(_parsedJson.get("Code").toString());
				//if(GlobalVariables.users.containsKey(_email))
				if(_loginData.containsKey("PhoneNumber") && _loginData.containsKey("Code"))
				{
					if(_loginData.get("Code").equals(_parsedJson.get("Code"))) {
						String uid = GlobalFunctions.CreateUserUUID();
						String NickName = GlobalFunctions.randomName();
						 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
						 OffsetDateTime now = OffsetDateTime.now(); 
						String current_date = dtf.format(now);
						_userData.put("PhoneNumber", _loginData.get("PhoneNumber"));
						_userData.put("Code", _loginData.get("Code"));
						_userData.put("CreatedTime", GlobalFunctions.GetUTCDate());
						_userData.put("UserId", uid);
						_userData.put("NickName", NickName);
						_userData.put("Gems", "0");
						_userData.put("UserId", _userData.get("UserId"));
						_ret.put("Response", "CodeValidation");
						_ret.put("IsCreated", true);
						_ret.put("NickName", _userData.get("NickName"));
						_ret.put("Gems", "0");
						_ret.put("UserId", _userData.get("UserId"));	
						_userData.put("sequence_day", "0");
						_userData.put("Date", current_date);
						RedisApi.SetUserData(uid,_userData);
						_uid_data.put("UserId",_userData.get("UserId"));
						_uid_data.put("Code",_loginData.get("Code"));
						_uid_data.put("PhoneNumber",_loginData.get("PhoneNumber"));
						RedisApi.SetUserData(_loginData.get("PhoneNumber"), _uid_data);
					}
					else 
					{
						_ret.put("IsLoggedIn", false);
						_ret.put("ErrorCode", "Wrong Password");
					}
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
