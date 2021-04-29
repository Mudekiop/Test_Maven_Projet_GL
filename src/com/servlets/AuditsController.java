package com.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.beans.Utilisateur;
import com.dao.AuditDao;
import com.dao.DaoFactory;

@WebServlet("/auditsController")
public class AuditsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AuditDao auditDao;

	public AuditsController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		DaoFactory daoFactory = DaoFactory.getInstance();

		HttpSession session = request.getSession();
		Utilisateur utilisateur = new Utilisateur();

		try {
			this.auditDao = daoFactory.getAuditDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (session.getAttribute("id") == null) {
			utilisateur.setId(0);
		} else {
			utilisateur.setId((int) session.getAttribute("id"));
		}
		try {
			request.setAttribute("listeAudits", this.auditDao.listerMesAudits(utilisateur));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			this.getServletContext().getRequestDispatcher("/WEB-INF/audits.jsp").forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.doGet(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

}
