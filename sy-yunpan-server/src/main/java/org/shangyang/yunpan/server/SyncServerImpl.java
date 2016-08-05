package org.shangyang.yunpan.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.shangyang.directory.ActionEnum;
import org.shangyang.directory.FileAction;
import org.shangyang.directory.FileChecker;
import org.shangyang.directory.FileDTO;

/**
 * Sync Server Services.
 * 
 * 
 * @author 商洋
 *
 */
public class SyncServerImpl implements SyncServer {
	
	private static Logger logger = Logger.getLogger( SyncServerImpl.class );
	
	@Override
	public List<FileDTO> check() {
		
		return FileChecker.getInstance().check( new File( Repository.getInstance().getSyncBase() ) );
	}
	
	@Override
	public void sync( FileAction action, File source ) throws IOException {
		
		String basepath = Repository.getInstance().getSyncBase();
		
		File target;
		
		if( action.getAction().equals(ActionEnum.DELETE) ){
			
			target = new File( basepath + File.pathSeparator + action.getPath() );
			
			FileUtils.forceDelete( target );
			
			logger.info( "file: "+ action.getPath() + ", last modified: "+ action.getFile().getLastModified().getTime() +" has been deleted" ); 
			
		}
		
	}
	
	

}
