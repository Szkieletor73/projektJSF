package com.jsfcourse.post;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.jsf.dao.UserDAO;
import com.jsf.entities.User;

@ManagedBean
public class Login {
	private static final String PAGE_MAIN = "personList?faces-redirect=true";
	private static final String PAGE_LOGIN = "login";
	private static final String PAGE_STAY_AT_THE_SAME = null;
	public static Integer iduser;
	static String email;
	public static String username;
	
	
	static String pass;
	
	public Integer getIduser() {
		return iduser;
	}
	public void setIduser(Integer iduser) {
		Login.iduser = iduser;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		Login.email = email;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		Login.pass = pass;
	}
	
	public void setUsername(String username) {
		Login.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	

	public boolean validateData() {
		boolean result = true;
		FacesContext ctx = FacesContext.getCurrentInstance();

		// check if not empty
		if (email == null || email.length() == 0) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Email field must not be empty", "null"));
		}

		if (pass == null || pass.length() == 0) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Password field must not be empty", "null"));
		}

		if (ctx.getMessageList().isEmpty()) {
			result = true;
		} else {
			result = false;
		}
		return result;

	}

	public String doLogin(String email, String pass) throws ServletException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		User user = null;
		
		// 1. check parameters and stay if errors
		if (!validateData()) {
			return PAGE_STAY_AT_THE_SAME;
		}

		// 2. verify login and password - get User from "database"
		user = getUserFromDatabase(email, pass);

		// 3. if bad login or password - stay with error info
		if (user == null) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Wrong email or password. Please try again.", null));
			return PAGE_STAY_AT_THE_SAME;
		}

		// 4. if login ok - save User object in session
		HttpSession session = (HttpSession) ctx.getExternalContext().getSession(true);
		session.setAttribute("user", user);
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		request.login(user.getUsername(), pass);


//		// and enter the system
		return PAGE_MAIN;
	}

	public static User getUser() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		return (User) session.getAttribute("user");
	}
	
	
	
	public String doLogout(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		//Invalidate session
		// - all objects within session will be destroyed
		// - new session will be created (with new ID)
		session.invalidate();
		return PAGE_LOGIN;
	}
	
	
	@EJB
	UserDAO userDAO;
	// simulate finding user in database
	private User getUserFromDatabase(String email, String pass) {
		User u = null;
		List<User> list = null;
		Map<String,Object> loginParams = new HashMap<String, Object>();
		loginParams.put("email", email);
		loginParams.put("passwd", pass);
		list = userDAO.loginCheck(loginParams);
		if(list.size() == 1) {
			User identifier = list.get(0);			
			u = new User(email, identifier.getUsername(), pass);
			u.setEmail(identifier.getEmail());
			u.setUsername(identifier.getUsername());
			u.setPassword(identifier.getPassword());
			u.setId(identifier.getId());
			u.setRole(identifier.getRole());
		}	
		return u;
	}
	public User getUser(Integer id) throws ClassNotFoundException, SQLException{		
		User user = null;
		user = userDAO.fetchUser(id);
		return user;
		
	}
	public List<User> getUsers() throws ClassNotFoundException, SQLException{		
		List<User> user = null;
		user = userDAO.fetchUsers();
		return user;
		
	}
	public String set(Integer id, String ret) throws ClassNotFoundException, SQLException {
		Login.iduser = id;
		return ret;
	}
	
	public String userDelete(Integer id) {
		User user = new User();
		user.setId(id);
		userDAO.remove(user);
		return "user removed";
	}
	
	public void userEdit(Integer id, String email, String username, String password, Integer role) throws ParseException, ClassNotFoundException, SQLException {
		User user = new User();
		user = (User) getUser(id);
		user.setId(id);		
		user.setEmail(email);
		user.setPassword(password);
		user.setRole(role);
		
		// apply changes
		userDAO.merge(user);
	}
	
}