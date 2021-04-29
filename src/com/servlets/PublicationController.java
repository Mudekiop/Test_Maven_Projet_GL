package com.servlets;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beans.Audit;
import com.beans.Modele;
import com.beans.Role;
import com.beans.Utilisateur;
import com.dao.AuditDao;
import com.dao.DaoFactory;
import com.dao.ModeleDao;
import com.dao.UtilisateurDao;

@WebServlet("/publicationController")
public class PublicationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ModeleDao modeleDao;
	private UtilisateurDao utilisateurDao;
	private AuditDao auditDao;
	
	private static String listeEleveString = "listeEleve";
	private static String listeEncadrantMatiereString = "listeEncadrantMatiere";

	public PublicationController() {
		super();
	}

	@Override
	public void init() throws ServletException {
		DaoFactory daoFactory = DaoFactory.getInstance();
		try {
			this.modeleDao = daoFactory.getModeleDao();
			this.utilisateurDao = daoFactory.getUtilisateurDao();
			this.auditDao = daoFactory.getAuditDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Si on choisit un audit
		if (request.getParameter("id") != null) {
			String id = request.getParameter("id");
			Modele modele = null;
			try {
				modele = modeleDao.getModeleById(Integer.valueOf(id));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			List<Utilisateur> listeResponsableOption = utilisateurDao.getListeUtilisateurByRole(Role.RESPONSABLE_OPTION);
			List<Utilisateur> listeResponsableUE = utilisateurDao.getListeUtilisateurByRole(Role.RESPONSABLE_UE);
			List<Utilisateur> listeEncadrantMatiere = utilisateurDao.getListeUtilisateurByRole(Role.ENCADRANT_MATIERE);
			List<Utilisateur> listeEleve = utilisateurDao.getListeUtilisateurByRole(Role.ELEVE);
			request.setAttribute("modele", modele);
			request.setAttribute("listeResponsableOption", listeResponsableOption);
			request.setAttribute("listeResponsableUE", listeResponsableUE);
			request.setAttribute(listeEncadrantMatiereString, listeEncadrantMatiere);
			request.setAttribute(listeEleveString, listeEleve);
			try {
				this.getServletContext().getRequestDispatcher("/WEB-INF/publicationAudit.jsp").forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
		// Sinon on affiche tous les modeles
		else {
			List<Modele> liste = modeleDao.getListeModeles();
			request.setAttribute("listeModeles", liste);
			try {
				this.getServletContext().getRequestDispatcher("/WEB-INF/listeModeles.jsp").forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)			throws ServletException, IOException {
		int idModele = 0;
		try {
			idModele = Integer.valueOf(request.getParameter("idModele"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		Modele modele = modeleDao.getModeleById(idModele);
		Audit audit = new Audit();

		audit.setAuteur(utilisateurDao.getUtilisateurById(2)); //Changer l'utilisateur en fonction de la personne
																//connectée
		audit.setModele(modele);
		audit.setTitre(request.getParameter("titre"));
		Date dateOuverture = Date.valueOf(request.getParameter("dateOuverture"));
		audit.setDateOuverture(dateOuverture);
		Date dateFermeture = Date.valueOf(request.getParameter("dateFermeture"));
		audit.setDateCloture(dateFermeture);

		List<String> listeIdConcernes = new ArrayList<>();
		if(request.getParameterValues("listeRespOption")!=null)
			listeIdConcernes.addAll(Arrays.asList(request.getParameterValues("listeRespOption")));
		if(request.getParameterValues("listeRespUE")!=null)
			listeIdConcernes.addAll(Arrays.asList(request.getParameterValues("listeRespUE")));
		if(request.getParameterValues(listeEncadrantMatiereString)!=null)
			listeIdConcernes.addAll(Arrays.asList(request.getParameterValues(listeEncadrantMatiereString)));
		if(request.getParameterValues(listeEleveString)!=null)
			listeIdConcernes.addAll(Arrays.asList(request.getParameterValues(listeEleveString)));
		
		
		List<Utilisateur> listeUtilisateursConcernes = new ArrayList<>();
		for(String id : listeIdConcernes) {
			try {
				listeUtilisateursConcernes.add(utilisateurDao.getUtilisateurById(Integer.valueOf(id)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		audit.setListeConcerne(listeUtilisateursConcernes);

		audit.setStatutCourrant();
		boolean resultat = auditDao.publierAudit(audit);

		if(resultat)
			try {
				this.getServletContext().getRequestDispatcher("/WEB-INF/accueil.jsp").forward(request, response); //appeler l'url accueil
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}

		else
			try {
				this.getServletContext().getRequestDispatcher("/WEB-INF/erreurPublicationAudit.jsp").forward(request, response); //afficher page erreur
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
	}

}
