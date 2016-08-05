package org.shangyang.yunpan.directory;

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
	
	FileDTO file;

	public FileAction(FileDTO file, ActionEnum action){
		
		this.action = action;
		
		this.file = file;
		
	}
	
	public ActionEnum getAction() {
		return action;
	}

	public void setAction(ActionEnum action) {
		this.action = action;
	}

	public FileDTO getFile() {
		return file;
	}

	public void setFileDTO(FileDTO file) {
		this.file = file;
	}
	
}
