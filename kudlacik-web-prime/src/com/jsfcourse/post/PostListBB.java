package com.jsfcourse.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.jsf.dao.PostDAO;
import com.jsf.entities.Post;
import com.jsf.entities.User;

@ManagedBean
public class PostListBB {
	private static final String PAGE_POST_EDIT = "personEdit?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private Long idpost;
	private String imgpath;
	private String description;
	private String owner;
	private Integer page = 1;
	private Integer maxPage;

	// getters, setters (properties used at the JSF page)

	public Long getIdpost() {
		return idpost;
	}

	public String getImgPath() {
		return imgpath;
	}

	public void setImgPath(String imgpath) {
		this.imgpath = imgpath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
	
	public Integer getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(Integer maxPage) {
		this.maxPage = maxPage;
	}

	//Dependency injection
	// - no setter method needed in this case
	@EJB
	PostDAO postDAO;
	
	public List<Post> getFullList(){
		return postDAO.getFullList();
	}

	public List<Post> getList(){
		List<Post> list = null;
		
		//1. Prepare search params
		Map<String,Object> searchParams = new HashMap<String, Object>();
		
		if (description != null && description.length() > 0){
			searchParams.put("description", "%"+description+"%");
		}
		
		//2. Get list
		list = postDAO.getList(searchParams, page);
		maxPage = list.size();
		
		return list;
	}
	
	public String newPost(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		Post post = new Post();
		post.setUser((User)session.getAttribute("user"));
		session.setAttribute("post", post);
		return PAGE_POST_EDIT;
	}

	public String editPost(Post post){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.setAttribute("post", post);
		return PAGE_POST_EDIT;
	}

	public String deletePost(Post post){
		postDAO.remove(post);
		return PAGE_STAY_AT_THE_SAME;
	}

}
