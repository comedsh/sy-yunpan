package org.shangyang.directory;

import java.io.Serializable;

/**
 * 
 * Tells, what to do about this file.
 * 
 * @author 商洋
 *
 */
public class FileAction implements Serializable{

	private static final long serialVersionUID = 6007597599312191360L;

	ActionEnum action;
	
	String path;

	public FileAction(String path, ActionEnum action){
		
		this.action = action;
		
		this.path = path;
		
	}
	
	public ActionEnum getAction() {
		return action;
	}

	public void setAction(ActionEnum action) {
		this.action = action;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	
}
