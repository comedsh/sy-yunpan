package org.shangyang.yunpan.client;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.shangyang.yunpan.directory.FileChecker;

/**
 * 启动云盘客户端
 * 
 * @author 商洋
 *
 */
public class StartClient {

	public void main(String[] args){
		
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask(){

			@Override
			public void run() {

				FileChecker checker = FileChecker.getInstance();
				
				checker.check( new File("/Users/mac/Documents/OneDrive") ); // hard code there.
				
			}
			
		}, 1000, 1000 * 60 * 5); // 每隔五分钟执行一次。
		
	}
	
}
