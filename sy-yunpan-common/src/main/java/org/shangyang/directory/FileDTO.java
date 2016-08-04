package org.shangyang.directory;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * The specific file dto defined for remote call of sharing file information. 
 * 
 * @author 商洋
 *
 */
public class FileDTO implements Comparable<FileDTO>, Serializable{
	
	private static final long serialVersionUID = 4785853643953657986L;
	
	// this is the relative path related
	
	String path;
	
	Date lastModified;

	public FileDTO(String path, Date lastModified){
		
		this.path = path;
		
		this.lastModified = lastModified;
		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	@Override
	public boolean equals(Object t) {
		
		return StringUtils.equals(this.getPath(), ( (FileDTO) t ).getPath() );
	}

	@Override
	public int compareTo(FileDTO t) {
		
		return this.getPath().compareTo(t.getPath());
		
	}
	
}
