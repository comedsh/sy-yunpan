package org.shangyang.yunpan.client;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
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
		
		fixpath();
		
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

	/**
	 * for the mac app, the system property of user.dir results not correct, this method try to fix it up. 
	 * 
	 * the current path should be like /Users/mac/Desktop/yunpan.app/Contents
	 * 
	 */
	private static void fixpath(){
		
		System.setProperty("user.dir", StringUtils.removeEnd( System.getProperty("java.library.path"), "/Java" ) );
		
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
		
		inspectSystem(args);
		
		while(true){
			
			try{
				
				long start = System.currentTimeMillis(); 
				
				logger.debug("start sync");
				
				 Client.getInstance().sync();
				
				logger.debug("sync completed, current tiemstamp, time spent " + ( System.currentTimeMillis() - start ) / 1000 + " seconds ");
				
			}catch(Exception e){
			
				logger.error(e.getMessage(), e);
				
			}finally{
				
				try {
					
					TimeUnit.SECONDS.sleep(300);

				} catch (InterruptedException e) {
					
					e.printStackTrace();
				} 
				
			}
		}
				
		
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

	public String getBasePath() {
		
		return basePath;
	}

	public void setBasePathPath(String basePath) {
		
		this.basePath = basePath;
	}
	
	/**
	 * 
	 * @param args main args
	 */
	private static void inspectSystem(String args[]){

		logger.debug("========================== inspecting the arguments start =================================");
		
		for( String arg : args ){
			
			logger.debug("args:" + arg);
			
		}
		
		logger.debug("========================== inspecting the arguments end =================================");
		

		logger.debug( "================================ inspecting System Properties ================================" );
		
		Enumeration<Object> keys = System.getProperties().keys();
		
		while( keys.hasMoreElements() ){
		
			String key = (String) keys.nextElement();
			
			logger.debug( "key:" + key + "; value: "+ System.getProperties().get( key ) );
		}
		
		logger.debug( "================================ System Properties searching end ================================" );
		
		logger.debug( "================================ inspecting VM Properties start ================================" );
		
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		
		List<String> aList = bean.getInputArguments();

		for (int i = 0; i < aList.size(); i++) {
		
			logger.debug( aList.get( i ) );
			
		}
		
		logger.debug( "================================ inspecting VM Properties end ================================" );
		
	}
	
}
