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
		
		System.setProperty("logpath", "/Users/mac/Desktop/yunpan-log" );  // so the log4j can dynamic read the ${logpath}
		
		logger = Logger.getLogger(Client.class); // Do notice that, Logger 如果想用系统环境变量作为参数值，必须在 System.setProperties 之后		
		
	}
	
	static Client c = null;
	
	String basePath = "/Users/mac/Documents/OneDrive/"; 

	// only processes on the production env.
	static String[][] SYNCPATHS = new String[][]{		
		{"/Users/mac/Documents/OneDrive/", "/Volumes/Elements/百度云同步盘/OneDrive/"},
		{"/Users/mac/workspace/", "/Volumes/Elements/百度云同步盘/workspace/"},
		{"/Users/mac/programs/", "/Volumes/Elements/百度云同步盘/programs/"} };
		
	// index for SYNPATHS	
	static int SYNCPOS = 0;
	
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
	 * the current path of mac should be like /Users/mac/Desktop/yunpan.app/Contents, and the java.library.path is the most approach to it, and this is only works for jdk1.7, 
	 * the path of jdk1.8 get changed.
	 * 
	 */
	private static void fixpath(){
		
		
		// if fixed, decided by the vm parameter set from the javapackager configuration. 
		
		if( StringUtils.equals( System.getProperty("fixpath"), "true") && StringUtils.equals( System.getProperty("platform"), "mac") ){
			
			// FIXME, 下面这个方式只有在 JDK1.7 才有效，如果是 JDK1.8，java.library.path 变了
			System.setProperty("user.dir", StringUtils.removeEnd( System.getProperty("java.library.path"), "/Java" ) );
		
		}
		
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
		
		// System.setProperty("platform", "mac"); // the the production execution switcher from Eclipse.
		
		inspectSystem(args);
		
		while(true){
			
			try{
				
				long start = System.currentTimeMillis(); 
				
				logger.debug("start sync");
				
				ClientTrayIcon.getInstance().init();
				
				Client.getInstance().prepareProduction();
				
				Client.getInstance().sync();
				
				ClientTrayIcon.getInstance().stop();
				
				logger.debug("sync completed, current tiemstamp, time spent " + ( System.currentTimeMillis() - start ) / 1000 + " seconds ");
				
			}catch(Exception e){
			
				logger.error(e.getMessage(), e);
				
				ClientTrayIcon.getInstance().error();
				
			}finally{
				
				try {
					
					// SYNCPOS == 0 for the production process check, make sure only sleep after all the SYNCPATHS get processed.
					if( SYNCPOS == 0 ) TimeUnit.SECONDS.sleep(300);

				} catch (InterruptedException e) {
					
					e.printStackTrace();
				} 
				
			}
		}
				
		
	}
	
	// multiple folder sync support, and so far only works for the production environment.
	// the vm parameter platform was set from dist_mac.xml
	private void prepareProduction() throws Exception {
		
		if( StringUtils.equals( System.getProperty("platform"), "mac" ) ) {
			
			basePath = SYNCPATHS[ SYNCPOS ][0];
			
			String targetBasePath = SYNCPATHS[ SYNCPOS ][1];
			
			Client.setServerBasePath( syncServer, targetBasePath );
			
			SYNCPOS ++;
			
			if( SYNCPOS == 3 ) SYNCPOS = 0;
			
		}
		
	}

	public List<FileDTO> check(){
		
		long start = System.currentTimeMillis();
		
		logger.debug("start to check the client file system, "+basePath);
		
		List<FileDTO> files = FileChecker.getInstance().check( new File( basePath ) );
		
		logger.debug("completed check the client file system, time spent " + ( System.currentTimeMillis() - start )/1000 +" seconds" );
		
		return files;
		
	}
	
	public void sync() throws Exception{
		
		ClientTrayIcon.getInstance().rotate();
		
		List<FileDTO> snapshot1 = this.check();
		
		List<FileDTO> snapshot2 = syncServer.check();
		
		List<FileAction> actions = differ.difference( snapshot1, snapshot2 );
		
		logger.debug("Totolly " + actions.size() +" actions needs to process ");
		
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
	
	public static void setServerBasePath( SyncServer syncServer, String basepath ) throws Exception{
		
		ClassLoader cl = (ClassLoader) syncServer.getClass().getDeclaredMethod("getClassLoader").invoke(syncServer, (Object[])null );
		
		Class<?> clazz = cl.loadClass("org.shangyang.yunpan.server.Repository");
		
		Object instance = clazz.getDeclaredMethod("getInstance").invoke(null, (Object[]) null);
		
		clazz.getDeclaredMethod("setBasePath", String.class ).invoke(instance, basepath );
		
	}
	
}
