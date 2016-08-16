package org.shangyang.yunpan.client;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.shangyang.yunpan.directory.FileAction;
import org.shangyang.yunpan.directory.FileChecker;
import org.shangyang.yunpan.directory.FileDTO;
import org.shangyang.yunpan.directory.FileDifference;
import org.shangyang.yunpan.directory.TargetEnum;
import org.shangyang.yunpan.server.SyncServer;

/**
 * 
 * 隐藏文件也需要同步，不过有些系统默认生成隐藏文件不应该被同步
 * 
 * @author 商洋
 *
 */
public class Client {
	
	static Logger logger = null;
	
	static{
		
		System.setProperty("logpath", "/Users/mac/Desktop" );  // so the log4j can dynamic read the ${logpath}
		
		logger = Logger.getLogger(Client.class); // Do notice that, Logger 如果想用系统环境变量作为参数值，必须在 System.setProperties 之后		
		
	}
	
	static Client c = null;
	
	String basePath = "/Users/mac/Documents/OneDrive/"; 
	
	FileDifference differ = ServiceLoader.load( FileDifference.class ).iterator().next();
	
	SyncServer syncServer = ServiceLoader.load( SyncServer.class ).iterator().next();
	
	public static Client getInstance(){
		
		if( c == null ){
			
			c = new Client();
			
		}
		
		return c;
		
	}
	
	public List<FileDTO> check(){
		
		long start = System.currentTimeMillis();
		
		logger.debug("start to check the client file system");
		
		List<FileDTO> files = FileChecker.getInstance().check( new File( basePath ) );
		
		logger.debug("completed check the client file system, time spent " + ( System.currentTimeMillis() - start )/1000 +" seconds" );
		
		return files;
		
	}
	
	public void sync() throws Exception{
		
		List<FileDTO> snapshot1 = this.check();
		
		List<FileDTO> snapshot2 = syncServer.check();
		
		List<FileAction> actions = differ.difference( snapshot1, snapshot2 );
		
		logger.debug("Totolly " + actions +" actions needs to process ");
		
		int number = 0;
		
		for( FileAction action : actions ){
			
			if( action.getTarget().equals(TargetEnum.SERVER ) ){
			
				logger.debug( action.toString() );
				
				this.sync( action );
			
			}else{
				
				logger.warn("of the very first 0.1 version, no server update client scenario;" + action.toString() );
				
			}
			
			logger.debug("current processed "+ ( ++ number ) +" files." );
			
		}
		
	}
	
	/**
	 * 
	 * 如果在提交 UPDATE/INSERT action 时候，发现文件和之前判断时候的状态有变化，证明用户正在编辑该文件，那么这次就放弃同步，交给下次同步处理。
	 * 
	 * 注意，DELETE 不做此判断，直接删除服务器即可，如果在真正提交的时候，发现文件又被 recreate，那么下次同步的时候会自动 sync 给 server
	 *
	 * 
	 * @param action
	 * @throws IOException 
	 */
	void sync( FileAction action ) throws IOException{
		
		switch( action.getAction() ){
		
		case UPDATE:
			
			if( match( action ) ){
				
				syncServer.sync(action, new File( getBasePath() + action.getFile().getRelativePath() ) );
			}
			
			break;
			
		case INSERT:
			
			if( match( action ) ){
				
				syncServer.sync(action, new File( getBasePath() + action.getFile().getRelativePath() ) );
			}
			
			break;
			
		case DELETE:
			
			syncServer.sync(action, null );
			
			break;
		
		}
		
	}
	
	/**
	 * 比对判断时候的文件状态和当前状态是否匹配，若不匹配则返回 false
	 * 
	 * @param action
	 * @return
	 */
	boolean match( FileAction action ){
		
		File current = new File( getBasePath() + action.getFile().getRelativePath() );
		
		if( current.exists() ){
			
			if( current.lastModified() == action.getFile().getLastModified().getTime() ){
				
				return true;
				
			}
			
		}
		
		return false;
		
	}

	public static void main(String[] args){
		
//		Timer timer = new Timer();
//		
//		timer.schedule(new TimerTask(){
//
//			@Override
//			public void run() {
//				
//				try {
//					
//					long start = System.currentTimeMillis(); 
//					
//					logger.debug("start sync");
//					
//					 Client.getInstance().sync();
//					
//					logger.debug("sync completed, current tiemstamp, time spent " + ( System.currentTimeMillis() - start ) / 1000 + " seconds ");
//					
//				} catch (Exception e) {
//					
//					e.printStackTrace();
//				}
//				
//				
//			}
//			
//		}, 1000, 1000 * 60 * 5); // 每隔五分钟执行一次，如果上一次的 scheduler 没有完成，则等待。shit，它是以上次开始执行的时间来计算的....
		
		while(true){
			
			try{
				
				long start = System.currentTimeMillis(); 
				
				logger.debug("start sync");
				
				 Client.getInstance().sync();
				
				logger.debug("sync completed, current tiemstamp, time spent " + ( System.currentTimeMillis() - start ) / 1000 + " seconds ");
				
				TimeUnit.SECONDS.sleep(300); // every 300 seconds executed once.
				
			}catch(Exception e){
			
				e.printStackTrace();
				
			}
		}
		
		
		
	}
	
	public String getBasePath() {
		
		return basePath;
	}

	public void setBasePathPath(String basePath) {
		
		this.basePath = basePath;
	}
	
}
