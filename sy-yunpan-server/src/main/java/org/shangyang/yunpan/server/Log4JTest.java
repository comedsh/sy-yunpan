package org.shangyang.yunpan.server;

import org.apache.log4j.Logger;
import org.junit.Test;

public class Log4JTest {
	
	@Test
	public void testOutput(){

		System.setProperty("logpath", "/Users/mac/Desktop");
		
		Logger logger = Logger.getLogger( SyncServerImpl.class );
		
		logger.info("this is the info test message");
		
	}
	
}
