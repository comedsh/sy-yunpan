package org.shangyang.yunpan;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * the bugs verfication for the v0.0.1
 * 
 * @author 商洋
 *
 */
public class Bugs001VerficationTest {

	/**
	 * bug 1
	 * 
	 * the path will be created at the first sync, and removed at the second sync 
	 * 
	 */
	@Test
	public void testBug1(){

		String source = "/Documents/learning/Software/Architect/web 后端 - 应用服务器 ( Web Server )/J2EE - Servlet/Servlet 3.0 - Web Fragment Project";
		
		String target = "/Documents/learning/Software/Architect/web 后端 - 应用服务器 ( Web Server )/J2EE - Servlet/Servlet 3.0 - Web Fragment Project";
		
		System.out.println( "by replace all:" + target.replaceAll( source, "" ) );		
		
		System.out.println( "by replace:" + target.replace( source, "" ) );	
		
		assertFalse( "replace all not works", target.replaceAll(source, "").length() == 0 );
		
		assertTrue( "replace works", target.replace(source, "").length() == 0 );
		
	}
	
	
}
