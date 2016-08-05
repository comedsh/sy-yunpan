package org.shangyang.yunpan.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.shangyang.directory.FileAction;
import org.shangyang.directory.FileDTO;

/**
 * 
 * @author 商洋
 *
 */
public interface SyncServer {

	public List<FileDTO> check();
	
	/**
	 * for the very first version 1.0, just use the raw source file to do the action with server. 
	 * 
	 * @param action
	 * @param cache
	 */
	public void sync( FileAction action, File source ) throws IOException;
	
}
