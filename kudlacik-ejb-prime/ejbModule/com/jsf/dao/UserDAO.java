package com.jsf.dao;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.jsf.entities.User;

//DAO - Data Access Object for User entity
//Designed to serve as an interface between higher layers of application and data.
//Implemented as stateless Enterprise Java bean - server side code that can be invoked even remotely.

@Stateless
public class UserDAO {
	private final static String UNIT_NAME = "kudlacik-simplePU";

	// Dependency injection (no setter method is needed)
	@PersistenceContext(unitName = UNIT_NAME)
	protected EntityManager em;

	public void create(User User) {
		em.persist(User);
		// em.flush();
	}

	public User merge(User User) {
		return em.merge(User);
	}

	public void remove(User User) {
		em.remove(em.merge(User));
	}

	public User find(Object id) {
		return em.find(User.class, id);
	}

	public List<User> getFullList() {
		List<User> list = null;

		Query query = em.createQuery("select p from users p");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<User> getList(Map<String, Object> searchParams) {
		List<User> list = null;

		// 1. Build query string with parameters
		String select = "select p ";
		String from = "from User p ";
		String where = "";
		String orderby = "order by p.email";

		// search for surname
		// String surname = (String) searchParams.get("email");
		// if (surname != null) {
		// 	if (where.isEmpty()) {
		// 		where = "where ";
		// 	} else {
		// 		where += "and ";
		// 	}
		// 	where += "p.surname like :surname ";
		// }
		
		// ... other parameters ... 

		// 2. Create query object
		Query query = em.createQuery(select + from + where + orderby);

		// 3. Set configured parameters
		// if (surname != null) {
		// 	query.setParameter("surname", surname+"%");
		// }

		// ... other parameters ... 

		// 4. Execute query and retrieve list of User objects
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public List<User> loginCheck(Map<String, Object> loginParams){
		List<User> list = null;
		String select = "select p ";
		String from = "from User p ";
		String where = "where ";
		
		String email = (String) loginParams.get("email");
		String passwd = (String) loginParams.get("passwd");
		where += "p.email like :email and p.password like :passwd";
		
		Query query = em.createQuery(select + from + where);
		query.setParameter("email", email);
		query.setParameter("passwd", passwd);
		list = query.getResultList();
		return list;
	}
	
	public User fetchUser(Integer id) {
		User user = new User();
		String select = "select p ";
		String from = "from User p ";
		String where = "where id = ";
		where += id;
		Query query = em.createQuery(select + from + where);
		user = (User) query.getSingleResult();
		return user;
	}
	
	public List<User> fetchUsers() {
		List<User> user = null;
		String select = "select p ";
		String from = "from User p ";
		Query query = em.createQuery(select + from);
		user = query.getResultList();
		return user;
	}

}
