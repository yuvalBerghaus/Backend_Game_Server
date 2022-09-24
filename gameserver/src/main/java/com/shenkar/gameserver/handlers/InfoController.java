package com.shenkar.gameserver.handlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shenkar.gameserver.logic.ConnectionLogic;
import com.shenkar.gameserver.logic.LoggedUsersLogic;

@WebServlet("/info")
public class InfoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
      //hi
    public InfoController() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String _print = "";
		int _sessionsCount = ConnectionLogic.getInstance().getConnectedUsers().size();
		int _loggedUsers = LoggedUsersLogic.getInstance().getLoggedUsers().size();
		

		_print += "Sessions Count: " + _sessionsCount + System.lineSeparator();
		_print += "Logged Users Count: " + _loggedUsers + System.lineSeparator();
		
		response.getWriter().append(_print);
	}

}
