package org.shangyang.yunpan.server;
import java.io.IOException;

import org.junit.Test;
import org.shangyang.yunpan.server.Repository;

/**
 * 
 * @author 商洋
 *
 */
public class RepositoryTest {
	
	@Test
	public void testProperties() throws IOException{
		
		Repository r = Repository.getInstance();
		
		System.out.println( r.getSyncBase() );
		
	}
	
}
