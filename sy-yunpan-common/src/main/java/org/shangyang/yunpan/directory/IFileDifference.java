package org.shangyang.yunpan.directory;

import java.util.List;

public interface IFileDifference {
	
	/**
	 * 
	 * 比较两个文件目录结构，并生成 target 需要执行的 FileAction，
	 * 
	 * 
	 * 第二个版本？
	 * 
	 * @return
	 */
	public List<FileAction> difference( List<FileDTO> sources, List<FileDTO> targets );
	
}
