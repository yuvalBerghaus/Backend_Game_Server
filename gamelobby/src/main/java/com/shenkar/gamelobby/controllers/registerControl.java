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

import com.shenkar.gamelobby.utils.GlobalFunctions;
import com.shenkar.gamelobby.utils.GlobalVariables;
import com.shenkar.gamelobby.utils.RedisApi;

@WebServlet("/register")//hi
public class registerControl extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public registerControl() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Register(request,  response);
	}

	private void Register(HttpServletRequest request, HttpServletResponse response)
	{
		String pGen_code = "";
		Map<String,Object> _ret = new LinkedHashMap<String, Object>();
		try
		{
			String _message = request.getParameter("Data");
			
			Map<String, Object> _parsedJson = GlobalFunctions.DeserializeJson(_message);
			if(_parsedJson.containsKey("PhoneNumber")) 
			{
				String gen_code = new String(GlobalFunctions.getRandomNumberString());
				pGen_code = gen_code;
				System.out.println("Success");
				String phone_number = _parsedJson.get("PhoneNumber").toString();
				Map<String,String> _loginData = RedisApi.GetUserData(phone_number);
				if(_loginData.containsKey("PhoneNumber") == false)
				{
					_loginData.put("Code", gen_code);
					_loginData.put("PhoneNumber", phone_number);
					RedisApi.SetUserData(gen_code, _loginData);
				}
				else if(_loginData.containsKey("PhoneNumber") && _loginData.containsKey("Code") && _parsedJson.containsKey("Code"))
				{
					if(_loginData.get("PhoneNumber") == _parsedJson.get("PhoneNumber")) {
						_ret.put("IsCreated", false);
						_ret.put("ErrorCode", "User Already Exist");	
					}	
				}
			}
			else
			{
				_ret.put("IsCreated", false);
				_ret.put("ErrorCode", "Missing Variables");
			}
			
			_ret.put("Response", "Register");
			_ret.put("Code", pGen_code);
			String _retString = GlobalFunctions.SerializeToJson(_ret);
			PrintWriter out = response.getWriter();
		 	out.println(_retString);
		}
		catch(Exception e) 
		{
			_ret.put("IsCreated", false);
			_ret.put("ErrorCode", e.getMessage());
		}
	}
}
