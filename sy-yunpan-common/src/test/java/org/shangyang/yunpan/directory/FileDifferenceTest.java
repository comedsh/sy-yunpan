package org.shangyang.yunpan.directory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shangyang.yunpan.directory.FileAction;
import org.shangyang.yunpan.directory.FileChecker;
import org.shangyang.yunpan.directory.FileDTO;
import org.shangyang.yunpan.directory.IFileDifference;

public class FileDifferenceTest {

	IFileDifference differ = ServiceLoader.load( IFileDifference.class ).iterator().next();
	
	FileChecker checker = FileChecker.getInstance();
	
	static String rootpath;
	
	static String basepath1;
	
	static String basepath2;
	
	@BeforeClass
	public static void beforeClass() throws IOException{
		
		// remove the current path indicator "." from Linux System
		rootpath = StringUtils.removeEnd(new File(".").getCanonicalPath(), ".") + "/src/test/resources/";
		
		basepath1 = rootpath + "dir1/";
		
		basepath2 = rootpath + "dir2/";
			
	}
	
	@Before
	public void before() throws Exception{
		
		TestUtils.cleanupTestCases0(rootpath);		
		
		TestUtils.makeupTestCases0(rootpath);
		
	}
	
	/**
	 * difference with two same path. should return null
	 */
	@Test
	public void testDifference0(){
		
		List<FileDTO> sources = checker.check( new File( basepath1 ) );
		
		List<FileDTO> targets = checker.check( new File( basepath1 ) );
		
		List<FileAction> actions = differ.difference(sources, targets);
		
		Assert.assertTrue( CollectionUtils.isEmpty( actions) );		
		
	}
	
	/**
	 * difference with two difference path, should return the differences. 
	 */
	@Test
	public void testDifference1(){
		
		List<FileDTO> sources = checker.check( new File( basepath1 ) );
		
		List<FileDTO> targets = checker.check( new File( basepath2 ) );
		
		List<FileAction> actions = differ.difference(sources, targets);
		
		Assert.assertTrue( actions.size() > 0 );
		
		for(FileAction action : actions ){
			
			System.out.println( action.getFile() + ";;; " + action.getAction() );
			
		}		
	}
	
	/**
	 * to uses my real directory for the testing
	 */
	@Test
	public void testDifferencePressure(){
		
		File base1 = new File( "/Users/mac/Documents/OneDrive" ); 
		
		File base2 = new File( "/Volumes/Elements/百度云同步盘/OneDrive" );
		
		if( base2.exists() == false ){
			return;
		}
		
		List<FileDTO> sources = checker.check( base1 );
		
		List<FileDTO> targets = checker.check( base2 );
		
		List<FileAction> actions = differ.difference(sources, targets);
		
		for(FileAction action : actions ){
			
			System.out.println( action.getFile() + ", " + action.getAction() );
			
		}						
		
	}
	
	
}
