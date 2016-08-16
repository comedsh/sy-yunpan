package org.shangyang.yunpan.client;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.shangyang.yunpan.directory.FileAction;
import org.shangyang.yunpan.directory.FileChecker;
import org.shangyang.yunpan.directory.FileDTO;
import org.shangyang.yunpan.directory.FileDifference;
import org.shangyang.yunpan.directory.TargetEnum;
import org.shangyang.yunpan.server.SyncServer;

public class Client {
	
	Logger logger = Logger.getLogger(Client.class);
	
	static Client c = null;
	
	String basePath = "/Users/mac/Documents/OneDrive/"; 
	
	FileDifference differ = ServiceLoader.load( FileDifference.class ).iterator().next();
	
	SyncServer syncServer = ServiceLoader.load( SyncServer.class ).iterator().next();
	
	public static Client getInstance(){
		
		if( c == null ){
			
			c = new Client();
			
			System.setProperty("logpath", "/Users/mac/Desktop" );  // so the log4j can dynamic read the ${logpath}
			
		}
		
		return c;
		
	}
	
	public List<FileDTO> check(){
		
		return FileChecker.getInstance().check( new File( basePath ) );
		
	}
	
	public void sync() throws Exception{
		
		List<FileDTO> snapshot1 = this.check();
		
		List<FileDTO> snapshot2 = syncServer.check();
		
		List<FileAction> actions = differ.difference( snapshot1, snapshot2 );
		
		for( FileAction action : actions ){
			
			if( action.getTarget().equals(TargetEnum.SERVER ) ){
			
				logger.debug( action.toString() );
				
				this.sync( action );
			
			}else{
				
				logger.warn("of the very first 0.1 version, no server update client scenario;" + action.toString() );
				
			}
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
		
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				
				Client client = new Client();
				
				try {
					
					client.sync();
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				
			}
			
		}, 1000, 1000 * 60 * 5); // 每隔五分钟执行一次，如果上一次的 scheduler 没有完成，则等待。
		
	}
	
	public String getBasePath() {
		
		return basePath;
	}

	public void setBasePathPath(String basePath) {
		
		this.basePath = basePath;
	}
	
}
