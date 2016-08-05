package org.shangyang.directory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class TestUtils {

	/**
	 * makes up the test scenarios for v1.0
	 * 
	 * -> base_dir1
	 * 	  base_dir1/a/a.txt 'a' 
	 *    base_dir1/a/b.txt	   -> insert ( to server )
	 *    base_dir1/b/          -> insert ( to server ) 
	 *    base_dir1/c/c.txt     -> insert ( to server )
	 *    base_dir1/c/c1/c1.txt -> insert ( to server )
	 * 
	 * -> base_dir2
	 *    base_dir2/a/a.txt 'aa' -> update
	 *    base_dir2/a/c.txt 		-> delete
	 *    base_dir2/b/b.txt 		-> delete
	 *    base_dir2/c/c1/c2.txt  -> delete
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
	 * @throws Exception 
	 * 
	 */
	public static void makeupTestCases0(String rootpath) throws Exception{
		 
		createFile( rootpath + "/dir1/a/a.txt", "a" );
		createFile( rootpath + "/dir1/a/b.txt", null );
		createFile( rootpath + "/dir1/b/", null );
		createFile( rootpath + "/dir1/c/c.txt", null );
		createFile( rootpath + "/dir1/c/c1.txt", null );
		
		TimeUnit.SECONDS.sleep(2); // sleep two seconds for make sure the last update time is changed.
		
		createFile( rootpath + "/dir2/a/a.txt", "aa" );
		createFile( rootpath + "/dir2/a/c.txt", null );
		createFile( rootpath + "/dir2/b/b.txt", null );
		createFile( rootpath + "/dir2/c/c1/c2.txt", null );
		
	}
	
	public static void cleanupTestCases0(String rootpath) throws IOException{
		
		FileUtils.forceDeleteOnExit( new File( rootpath + "/dir1" ) );
		
		FileUtils.forceDeleteOnExit( new File( rootpath + "/dir2" ) );	
	}
	
	static void createFile(String path, String content) throws IOException{
		
		File file = new File(path);
		
		if( !path.endsWith("/") ){
			
			FileUtils.forceMkdirParent( file );
			
			FileUtils.write( file, content == null ? "xx" : content, Charset.defaultCharset() );
		
		} else{
			
			FileUtils.forceMkdir( file ); // or else dir1/b cannot be created
			
		}
		
	}
	
}
