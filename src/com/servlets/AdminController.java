package com.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dao.UtilisateurDao;
import com.dao.DaoFactory;


/**
 * Servlet implementation class PageAccueil
 */
@WebServlet("/PageAccueil")
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UtilisateurDao utilisateursDB;
       
    public AdminController() {
        super();
    }
    
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DaoFactory daoFactory = DaoFactory.getInstance();

		try {
			this.utilisateursDB = daoFactory.getUtilisateurDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("utilisateurs", utilisateursDB.getListUtilisateurs());

		try {
			this.getServletContext().getRequestDispatcher("/WEB-INF/accueilAdmin.jsp").forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		doGet(request, response);
    	} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

}
