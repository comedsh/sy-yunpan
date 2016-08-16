package org.shangyang.yunpan.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.shangyang.yunpan.directory.FileAction;
import org.shangyang.yunpan.directory.FileDTO;

/**
 * 
 * @author 商洋
 *
 */
public interface SyncServer {

	public List<FileDTO> check();
	
	/**
	 * See the implementation
	 * 
	 * @param action
	 * @param source
	 * @throws IOException
	 */
	public void sync( FileAction action, File source ) throws IOException;
	
}
