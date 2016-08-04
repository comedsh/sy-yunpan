package org.shangyang.directory;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;


/**
 * 
 * Check the repositories and fill up the file structures.
 * 
 * @author 商洋
 *
 */
public class FileChecker {

	static FileChecker service = null;
	
	public static FileChecker getInstance(){
		
		if( service == null ){
			
			service = new FileChecker();
			
		}
		
		return service;
		
	}
	
	/**
	 * 
	 * 从当前 path 中检出目录结构信息; 
	 * 
	 * @param the base path
	 * @param path
	 * @return the file structures that has the basepath prefix removed.
	 */
	public List<FileDTO> check(File base) {
		
		String basepath = base.getPath();
		
		Assert.assertTrue( "Must be a Directory", base.isDirectory() );
		
		List<FileDTO> files = new LinkedList<FileDTO>();
		
		this.check(basepath, base, files);
		
		Collections.sort(files);
		
		return files;

	}
	
	// 递归的检出文件目录结构
	private void check(String basePath, File file, List<FileDTO> dtos) {

		java.io.File[] fs = file.listFiles();

		if (fs == null) {
			return;
		}

		for (java.io.File f : fs) {
			
			if (f.isFile()) {
				
				dtos.add( new FileDTO( f.getPath().replaceAll(basePath, ""), new Date( f.lastModified() ) ) );
				
			} else {
				
				// 注意了，如果是空目录，那么表示也是一个叶子节点，必须得添加，否则该目录结构不会被创建
				if( f.listFiles().length == 0 ) {
					
					dtos.add( new FileDTO( f.getPath().replaceAll(basePath, ""), new Date( f.lastModified() ) ) );
					
				}
					
				
				check(basePath, f, dtos );
				
			}
		}

	}
}
