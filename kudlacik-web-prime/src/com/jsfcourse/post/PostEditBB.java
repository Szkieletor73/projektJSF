package com.jsfcourse.post;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.jsf.dao.PostDAO;
import com.jsf.entities.Post;


import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class PostEditBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_POST_LIST = "personList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private static final File dataDir = new File(System.getProperty("jboss.server.data.dir"));

	@EJB
	PostDAO postDAO;

	private Long idpost;
	private String imgpath;
	private String description;
	private String owner;
	private UploadedFile img;
	

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
	
	public UploadedFile getImg() {
		return img;
	}
	
	public void setImg(UploadedFile img) {
		this.img = img;
	}
	
	public void upload() {
        if(img != null) {
            FacesMessage message = new FacesMessage("Succesful, ", img.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

	// object to be edited/inserted
	private Post post = null;

	@PostConstruct
	public void postConstruct() {
		// A. load post if passed through session
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		post = (Post) session.getAttribute("post");

		// cleaning: attribute received => delete it from session
		if (post != null) {
			session.removeAttribute("post");
		}

		// B. if object not loaded try to get Post by id passed as GET
		// parameter
		if (post == null) {
			HttpServletRequest req = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			idpost = Long.parseLong(req.getParameter("p"));
			if (idpost != null) {
				// parse id
				Integer id = null;
				try {
					id = Math.toIntExact(idpost);
				} catch (NumberFormatException e) {
				}
				if (id != null) {
					// if parsing successful
					post = postDAO.find(id);
				}
			}
		}

		// if loaded record is to be edited then copy data to properties
		if (post != null && post.getIdpost() != null) {
			setImgPath(post.getImgPath());
			setDescription(post.getDescription());
			//setSurname(post.getSurname());
			// appropriately convert date to string
		}
	}

	private boolean validate(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		boolean result = false;

		if (img == null) {
			ctx.addMessage(null, new FacesMessage("We need an image, boss."));
		}
		if (description == null || description.trim().length() == 0) {
			ctx.addMessage(null, new FacesMessage("We need a description, boss."));
		}
		
		
		
		try(InputStream input = img.getInputstream()){
			String filename = img.getFileName();
			String extension;
			if(filename.length() < 5) {
				throw new IOException("Filename too short");
			} else {
				extension = filename.substring(filename.length()-4);
				filename = filename.substring(0, filename.length()-4);
			}
			Path file = Files.createTempFile(dataDir.toPath(), filename + "-", extension);
			imgpath = file.getFileName().toString();
			
			Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
		} catch(IOException e) {
			ctx.addMessage(null, new FacesMessage("Something went wrong when uploading: " + e));
		}

		// if no errors
		if (ctx.getMessageList().isEmpty()) {
			post.setImgPath(imgpath.trim());
			post.setDescription(description.trim());
			//post.setBirthdate(date);
			result = true;
		}

		return result;
	}

	public String saveData() {

		// no Post object passed
		if (post == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("dont do that"));
			return PAGE_STAY_AT_THE_SAME;
		}

		if (!validate()) {
			return PAGE_STAY_AT_THE_SAME;
		}

		try {
			if (post.getIdpost() == null) {
				// new record
				postDAO.create(post);
			} else {
				// existing record
				postDAO.merge(post);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("An error occured while saving."));
			return PAGE_STAY_AT_THE_SAME;
		}

		return PAGE_POST_LIST;
	}
}
