package org.shangyang.directory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
		
		FileUtils.forceDeleteOnExit( new File( basepath1 ) );
		
		FileUtils.forceDeleteOnExit( new File( basepath2 ) );		
	}
	
	/**
	 * makes up the test scenarios
	 * 
	 * -> dir1
	 * 	  dir1/a/a.txt 'a' 
	 *    dir1/a/b.txt	   -> insert ( to server )
	 *    dir1/b/          -> insert ( to server ) 
	 *    dir1/c/c.txt     -> insert ( to server )
	 *    dir1/c/c1/c1.txt -> insert ( to server )
	 * 
	 * -> dir2
	 *    dir2/a/a.txt 'aa' -> update
	 *    dir2/a/c.txt 		-> delete
	 *    dir2/b/b.txt 		-> delete
	 *    dir2/c/c1/c2.txt  -> delete
	 * 	  	
	 * -> All anticipate results to update the server side
	 *    /a/a.txt  -> update
	 *    /a/b/txt  -> insert
	 *    /b/       -> insert
	 *    /c/c.txt  -> insert
	 *    /c/c1/c1.txt -> insert
	 *    /a/c.txt  -> delete
	 *    /b/b.txt  -> delete
	 *    /c/c1/c2.txt -> delete    
	 * 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * 
	 */
	@Before
	public void before() throws IOException, InterruptedException{
		
		createFile( rootpath + "dir1/a/a.txt", "a" );
		createFile( rootpath + "dir1/a/b.txt", null );
		createFile( rootpath + "dir1/b/", null );
		createFile( rootpath + "dir1/c/c.txt", null );
		createFile( rootpath + "dir1/c/c1.txt", null );
		
		TimeUnit.SECONDS.sleep(2); // sleep two seconds for make sure the last update time is changed.
		
		createFile( rootpath + "dir2/a/a.txt", "aa" );
		createFile( rootpath + "dir2/a/c.txt", null );
		createFile( rootpath + "dir2/b/b.txt", null );
		createFile( rootpath + "dir2/c/c1/c2.txt", null );
		
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
		
		for(FileAction action : actions ){
			
			System.out.println( action.getPath() + ", " + action.getAction() );
			
		}		
	}
	
	void createFile(String path, String content) throws IOException{
		
		File file = new File(path);
		
		if( !path.endsWith("/") ){
			
			FileUtils.forceMkdirParent( file );
			
			FileUtils.write( file, content == null ? "xx" : content, Charset.defaultCharset() );
		
		} else{
			
			FileUtils.forceMkdir( file ); // or else dir1/b cannot be created
			
		}
		
	}
	
	
}
