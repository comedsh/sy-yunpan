package org.shangyang.yunpan;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shangyang.yunpan.directory.FileAction;
import org.shangyang.yunpan.directory.FileChecker;
import org.shangyang.yunpan.directory.FileDTO;
import org.shangyang.yunpan.directory.FileDifference;
import org.shangyang.yunpan.directory.TestUtils;

public class StartClientTest {
	
	static String rootpath;
	
	static String basepath1;
	
	static String basepath2;
	
	static String basepath3; // the server directory
	
	FileDifference differ = ServiceLoader.load( FileDifference.class ).iterator().next();
	
	@BeforeClass
	public static void beforeClass() throws IOException{
		
		// remove the current path indicator "." from Linux System
		rootpath = StringUtils.removeEnd(new File(".").getCanonicalPath(), ".") + "/src/test/resources/";
		
		basepath1 = rootpath + "dir1/";
		
		basepath2 = rootpath + "dir2/";
		
		basepath3 = rootpath + "dir3/"; 
			
	}
	
	@Before
	public void before() throws Exception{
		
		File f = new File( basepath3);
		
		if( f.exists() ) FileUtils.forceDelete( new File( basepath3) );
		
		TestUtils.cleanupTestCases1(rootpath);		
		
		TestUtils.makeupTestCases1(rootpath);
		
		FileUtils.forceMkdir( new File(basepath3) );
		
	}
	
	/**
	 * 
	 * Server 端是一个空的文件夹
	 * 
	 * 将 dir1/ 中的所有数据同步到 dir2/ 中
	 * 
	 * 
	 * 初始同步
	 */
	@Test
	public void testSync1(){
		
		FileChecker checker = FileChecker.getInstance();
		
		List<FileDTO> clients = checker.check( new File( basepath1 ) );
		
		List<FileDTO> server = checker.check( new File( basepath3 ) );
		
		List<FileAction> actions = differ.difference1(clients, server);
		
		for(FileAction action : actions ){
			
			System.out.println( action.toString() );
			
		}			
		
	}
	
	/**
	 * 同步
	 */
	public void testSync2(){
		
	}
	
}
