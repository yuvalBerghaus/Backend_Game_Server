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
import com.shenkar.gamelobby.utils.GlobalFunctions;
import com.shenkar.gamelobby.utils.GlobalVariables;
import com.shenkar.gamelobby.utils.RedisApi;

@WebServlet("/login")
public class loginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public loginController() {super();}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Login(request, response);
	}

	private void Login(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String,Object> _ret = new LinkedHashMap<String, Object>(); 
		try
		{
			String _message = request.getParameter("Data");
			Map<String,Object> _parsedJson = GlobalFunctions.DeserializeJson(_message);
			if(_parsedJson.containsKey("PhoneNumber"))
			{
				String phone_number = _parsedJson.get("PhoneNumber").toString();
				Map<String,String> _loginData = RedisApi.GetUserData(phone_number);
				//if(GlobalVariables.users.containsKey(_email))
				if(_loginData.containsKey("PhoneNumber"))
				{
					if(_loginData.get("PhoneNumber").equals(_parsedJson.get("PhoneNumber"))) {
						_ret.put("Response", "Login");
						String random_number = GlobalFunctions.getRandomNumberString();
						_ret.put("Code", random_number);
						_loginData.put("Code", random_number);
						_loginData.put("PhoneNumber", phone_number);
						RedisApi.SetUserData(random_number, _loginData);
						GlobalVariables.current_user.put("Code", random_number);
						GlobalVariables.current_user.put("PhoneNumber", phone_number);
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
		
		_ret.put("Response", "Login");
		
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
