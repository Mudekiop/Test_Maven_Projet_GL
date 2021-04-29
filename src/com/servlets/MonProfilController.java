package com.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/monProfilController")
public class MonProfilController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MonProfilController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.getServletContext().getRequestDispatcher("/WEB-INF/monProfil.jsp").forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// a mettre dans toutes les servlets car deconnexion dans le menu 
		if (request.getParameter("deco") != null) {
			HttpSession session = request.getSession();
			if (session.getAttribute("email") != null) {
				session.invalidate();
			}
		} else {
			request.setAttribute("errorMsg", request.getSession());
		}
		
		try {
			this.doGet(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

}
