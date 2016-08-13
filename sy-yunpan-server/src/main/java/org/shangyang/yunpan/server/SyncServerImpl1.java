package org.shangyang.yunpan.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
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
		
		return FileChecker.getInstance().check( new File( Repository.getInstance().getSyncBase() ) );
	}
	
	/**
	 * v1.0
	 * 
	 * 单独处理文件夹的情况，客户端传来一个叶子节点 Directory，那么，若服务器有这样的目录，直接将子目录删除
	 * 
	 * 第一期可以这么搞，只有一个客户端且只有一个服务器。
	 * 
	 * @param action
	 * @throws IOException
	 */
	@Override
	public void sync(FileAction action) throws IOException {

		Assert.assertTrue( action.getFile().isDirectory() );
		
		String basepath = Repository.getInstance().getSyncBase();
		
		File target = new File( basepath, action.getFile().getRelativePath() );
		
		if( target.exists() ){
			
			FileUtils.forceDelete( target ); // 会连同自己一起删除 
			
			FileUtils.forceMkdir( target ); // 所以这里补回来
			
			target.setLastModified( action.getFile().getLastModified().getTime() ); 
			
			logger.info( "file: "+ target.getAbsolutePath() + ", modified time: " + target.lastModified() +" get created" );
			
		}else{
			
			logger.info( "try delete directory: "+ action.getFile().getRelativePath() + " failed, because it is not existed");
			
		}
		
	}
	
	/**
	 * for the very first version 1.0, just use the raw source file to do the action with server. 
	 * 
	 * @param action
	 * @param cache
	 */
	@Override
	public void sync( FileAction action, File source ) throws IOException {
		
		Assert.assertTrue( action.getFile().isFile() );
		
		String basepath = Repository.getInstance().getSyncBase();
		
		// 通过合并两个路径生成一个新的文件
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
				
				FileUtils.copyFile(source, target);
				
				target.setLastModified( action.getFile().getLastModified().getTime() );
				
				logger.info( "file: "+ target.getAbsolutePath() + ", modified time: " + target.lastModified() +" get created" );
				
				break;
		
		}
		
		
	}
	
	

}
