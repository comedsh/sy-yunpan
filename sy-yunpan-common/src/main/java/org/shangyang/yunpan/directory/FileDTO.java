package org.shangyang.yunpan.directory;

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
	
	String relativePath;
	
	Date lastModified;
	
	private boolean isFile;
	
	private boolean isDirectory;

	public FileDTO(String relativePath, Date lastModified, boolean isFile ){
		
		this.relativePath = relativePath;
		
		this.lastModified = lastModified;
		
		this.setFile(isFile);
		
		this.setDirectory(!isFile);
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String path) {
		this.relativePath = path;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	@Override
	public boolean equals(Object t) {
		
		return StringUtils.equals( this.getRelativePath(), ( (FileDTO) t ).getRelativePath() );
	}

	@Override
	public int compareTo(FileDTO t) {
		
		return this.getRelativePath().compareTo(t.getRelativePath());
		
	}

	@Override
	public String toString() {

		return getRelativePath()+"; "+ this.getLastModified().getTime() ;
	}
	
	public static String toRelativePath(String basepath, String fullpath){
		
		return fullpath.replace(basepath, "");
		
	}
	
}
