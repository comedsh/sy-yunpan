package org.shangyang.yunpan.directory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class TestUtils {

	/**
	 * makes up the test cases for initial synchronization
	 * 
	 * -> case for /dir3
	 *    empty folder
	 *    
	 * -> case for /dir4 ( target aimed the /dir1 as the server when processing this test case )
	 * 	  /dir1/a/a.txt file name match; last TS match
	 * 	  /dir1/a/b.txt file name match; last TS not match, client < server -> update client
	 * 	  /dir1/c/c.txt file name match; last TS not match, client > server -> update server 
	 *    
	 * -> Anticipate results.
	 * 
	 * @param rootpath
	 * @throws Exception
	 */
	public static void makeupTestCases0(String rootpath) throws Exception{
		
	}
	
	/**
	 * makes up the default test cases and the anticipation results for the normal synchronization
	 * 
	 * -> case for base /dir1
	 * 	  /dir1/a/a.txt 'a' 
	 *    /dir1/a/b.txt	    -> insert ( server )
	 *    /dir1/b/          -> delete its sub directories ( to server ) | 叛逆者... 1. 需要判断是否是 target 的 parent folder，若是，则删除 target 的所有子目录。-> 这种情况最好单独处理，@see SyncServerImpl1#sync(FileAction action)
	 *    /dir1/c/c.txt     -> insert ( server )
	 *    /dir1/c/c1/c1.txt -> insert ( server )
	 *    /dir1/d			-> insert ( server )
	 * 
	 * wait 2 seconds to create base /dir2 cases, so the last ts of dir2 cases always bigger than dir1 cases
	 * 
	 * -> case for base /dir2
	 *    /dir2/a/a.txt 'aa' -> update ( client )
	 *    /dir2/a/c.txt 	 -> delete ( server )
	 *    /dir2/b/b.txt 	 -> delete ( server )
	 *    /dir2/c/c1/c2.txt  -> delete ( server )
	 * 	  	
	 * -> All anticipate results to update the server side
	 *    /a/a.txt  	-> update ( client )
	 *    /a/b.txt  	-> insert ( server )
	 *    /b/       	-> insert ( server )
	 *    /c/c.txt  	-> insert ( server )
	 *    /c/c1/c1.txt  -> insert ( server )
	 *    /a/c.txt  	-> delete ( server )
	 *    /b/b.txt 		-> delete ( server )
	 *    /c/c1/c2.txt  -> delete ( server )
	 *    /d			-> insert ( server )
	 *    
	 * Attention, normal sync case do not have client deleted/inserted cases, those steps only happens on initial steps   
	 *    
	 * @throws Exception 
	 * 
	 */
	public static void makeupTestCases1(String rootpath) throws Exception{
		 
		createFile( rootpath + "/dir1/a/a.txt", "a" );
		createFile( rootpath + "/dir1/a/b.txt", null );
		createFile( rootpath + "/dir1/b/", null );
		createFile( rootpath + "/dir1/c/c.txt", null );
		createFile( rootpath + "/dir1/c/c1.txt", null );
		createFile( rootpath + "/dir1/d/", null );
		
		TimeUnit.SECONDS.sleep(2); // sleep two seconds for make sure the last update time is changed.
		
		createFile( rootpath + "/dir2/a/a.txt", "aa" );
		createFile( rootpath + "/dir2/a/c.txt", null );
		createFile( rootpath + "/dir2/b/b.txt", null );
		createFile( rootpath + "/dir2/c/c1/c2.txt", null );
		
	}
	
	public static void cleanupTestCases1(String rootpath) throws IOException{
		
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
