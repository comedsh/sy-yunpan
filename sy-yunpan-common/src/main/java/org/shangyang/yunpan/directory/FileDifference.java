package org.shangyang.yunpan.directory;

import java.util.List;

/**
 * Compare the file structure between Client and Server, and generate the file action.
 * 
 * @author 商洋
 *
 */
public interface FileDifference {
	
	/**
	 * 
	 * 比较两个文件目录结构，并生成 Normal Sync 需要执行的 FileAction，
	 * 
	 * 
	 * @param sources the file structure of client
	 * @param targets the file structure of server
	 * 
	 * @return
	 */
	public List<FileAction> difference( List<FileDTO> sources, List<FileDTO> targets );
	
}
