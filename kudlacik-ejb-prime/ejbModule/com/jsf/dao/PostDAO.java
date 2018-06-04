package com.jsf.dao;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.jsf.entities.Post;

//DAO - Data Access Object for Post entity
//Designed to serve as an interface between higher layers of application and data.
//Implemented as stateless Enterprise Java bean - server side code that can be invoked even remotely.

@Stateless
public class PostDAO {
	private final static String UNIT_NAME = "kudlacik-simplePU";

	// Dependency injection (no setter method is needed)
	@PersistenceContext(unitName = UNIT_NAME)
	protected EntityManager em;

	public void create(Post Post) {
		em.persist(Post);
		// em.flush();
	}

	public Post merge(Post Post) {
		return em.merge(Post);
	}

	public void remove(Post Post) {
		em.remove(em.merge(Post));
	}

	public Post find(Object id) {
		return em.find(Post.class, id);
	}

	public List<Post> getFullList() {
		List<Post> list = null;

		Query query = em.createQuery("select p from Post p");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<Post> getList(Map<String, Object> searchParams, Integer page) {
		List<Post> list = null;

		// 1. Build query string with parameters
		String select = "SELECT p ";
		String from = "FROM Post p ";
		String where = "";
		String orderby = "ORDER BY p.id DESC ";

		// search
		 String description = (String) searchParams.get("description");
		 if (description != null) {
		 	if (where.isEmpty()) {
		 		where = "WHERE ";
		 	} else {
		 		where += "AND ";
		 	}
		 	where += "p.description LIKE :description ";
		 	
		 }
		
		// ... other parameters ... 

		// 2. Create query object
		Query query = em.createQuery(select + from + where + orderby);

		// 3. Set configured parameters
		 if(description != null) {
				query.setParameter("description", "%"+description+"%");
		 }

		// ... other parameters ... 

		// 4. Execute query and retrieve list of Post objects
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public Integer getCount (Map<String, Object> searchParams) {
		List<Post> list = null;
	
		// 1. Build query string with parameters
		String select = "SELECT p ";
		String from = "FROM Post p ";
		String where = "";
		String orderby = "ORDER BY p.id DESC ";
	
		// search
		 String description = (String) searchParams.get("description");
		 if (description != null) {
		 	if (where.isEmpty()) {
		 		where = "WHERE ";
		 	} else {
		 		where += "AND ";
		 	}
		 	where += "p.description LIKE :description ";
		 	
		 }
		
		// ... other parameters ... 
	
		// 2. Create query object
		Query query = em.createQuery(select + from + where + orderby);
	
		// 3. Set configured parameters
		 if(description != null) {
				query.setParameter("description", "%"+description+"%");
		 }
	
		// ... other parameters ... 
	
		// 4. Execute query and retrieve list of Post objects
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return list.size();
	}

}
