package com.shenkar.gamelobby.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shenkar.gamelobby.utils.GlobalFunctions;

@WebServlet("/ping")
public class pingController extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
    public pingController() {super();}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Ping Called");
		Map<String, Object> _retData = new LinkedHashMap<String, Object>();
		_retData.put("Response", "Ping");
		_retData.put("Message", "Pong");
		
		String _res = GlobalFunctions.SerializeToJson(_retData);
		PrintWriter _out = response.getWriter();
		_out.println(_res);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PingPost(request, response);
	}
	
	private void PingPost(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			String _message = request.getParameter("Data");
			Map<String, Object> _parsedJson = GlobalFunctions.DeserializeJson(_message);
			_parsedJson.put("Response", "PingPost");
			_parsedJson.put("ServerTime", GlobalFunctions.GetUTCDate());
			String _res = GlobalFunctions.SerializeToJson(_parsedJson);
			PrintWriter _out = response.getWriter();
			_out.println(_res);
		}
		catch(Exception e) {}
	}
}
