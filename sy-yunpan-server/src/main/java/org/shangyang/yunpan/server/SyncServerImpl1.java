package org.shangyang.yunpan.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.shangyang.yunpan.directory.FileAction;
import org.shangyang.yunpan.directory.FileChecker;
import org.shangyang.yunpan.directory.FileDTO;

/**
 * Sync Server Services.
 * 
 * the v1.0, the server is drove on the removable harddisk.
 * 
 * 
 * @author 商洋
 *
 */
public class SyncServerImpl1 implements SyncServer {
	
	private static Logger logger = Logger.getLogger( SyncServerImpl1.class );
	
	@Override
	public List<FileDTO> check() {
		
		long start = System.currentTimeMillis();
		
		logger.debug("start to check the server file system");
		
		List<FileDTO> files = FileChecker.getInstance().check( new File( Repository.getInstance().getSyncBase() ) );
		
		logger.debug("completed check the server file system, time spent " + ( System.currentTimeMillis() - start )/1000 +" seconds" );
		
		return files;
	}
	
	/**
	 * for the very first version 1.0, just use the raw source file to do the action with server. 
	 * 
	 * FIXME 目前该方法的实现并没有考虑到并发的情况，乐观锁机制，以及并发更新的情况。
	 * 
	 * 
	 * @param action
	 * @param cache
	 */
	@Override
	public void sync( FileAction action, File source ) throws IOException {
		
		// Assert.assertTrue( action.getFile().isFile() );
		
		String basepath = Repository.getInstance().getSyncBase();
		
		// 声明 Server 上所对应的文件
		File target = new File( basepath, action.getFile().getRelativePath() );
		
		switch ( action.getAction() ){
		
			case DELETE:
				
				if( target.exists() ){
					
					FileUtils.forceDelete( target );
					
					logger.info( "file: "+ target.getAbsolutePath() + ", modified time: " + target.lastModified() +" get deleted" );
					
				}else{
					
					logger.info( "try delete file: "+ action.getFile().getRelativePath() + " failed, because it is not existed");
					
				}
				
				break;
				
			case UPDATE:	
				
				FileUtils.copyFile(source, target); // will override 
				
				target.setLastModified( action.getFile().getLastModified().getTime() ); // 服务器是不应该直接能够获取客户端的源文件的，所以，其实应该这样操作。-> 只是留个注脚。
				
				logger.info( "file: "+ target.getAbsolutePath() + ", modified time: " + target.lastModified() +" get overrided" );
				
				break;
				
			case INSERT:
				
				if( source.isFile() )
					FileUtils.copyFile( source, target );
				
				if( source.isDirectory() )
					FileUtils.forceMkdir( target );
				
				target.setLastModified( action.getFile().getLastModified().getTime() );
				
				logger.info( "file: "+ target.getAbsolutePath() + ", modified time: " + target.lastModified() +" get created" );
				
				break;
		
		}
		
		
	}
	
	

}
