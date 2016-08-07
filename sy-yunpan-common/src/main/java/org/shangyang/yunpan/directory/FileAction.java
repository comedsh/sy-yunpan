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
	
	TargetEnum target;
	
	FileDTO file;

	public FileAction(FileDTO file, ActionEnum action, TargetEnum target){
		
		this.action = action;
		
		this.target = target;
		
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

	public TargetEnum getTarget() {
		return target;
	}

	public void setTarget(TargetEnum target) {
		this.target = target;
	}

	@Override
	public String toString() {

		return this.file.toString() + "; " + action + " -> " + target;
	}
	
	
}
