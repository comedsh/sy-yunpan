package org.shangyang.yunpan.directory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class TestUtils {
	
	/**
	 * makes up the default test cases and the anticipation results for the normal synchronization
	 * 
	 * -> case for base /dir1
	 * 	  /dir1/a/a.txt 'a' 
	 *    /dir1/a/b.txt	    -> insert ( server )
	 *    /dir1/b/          -> delete its sub directories ( to server ) | 叛逆者... 1. 需要判断是否是 target 的 parent folder，若是，则删除 target 的所有子目录。-> 这种情况最好单独处理，@see SyncServerImpl1#sync(FileAction action)
	 *    /dir1/c/c.txt     -> insert ( server )
	 *    /dir1/c/c1.txt    -> insert ( server )
	 *    /dir1/d			-> insert ( server )
	 * 
	 * wait 2 seconds to create base /dir2 cases, so the last ts of dir2 cases always bigger than dir1 cases
	 * 
	 * -> case for base /dir2
	 *    /dir2/a/a.txt 'aa' -> update ( client )
	 *    /dir2/a/c.txt 	 -> delete ( server )
	 *    /dir2/b/b.txt 	 -> delete ( server ), 特别注意的是，此删除过程也需要两步完成，第一步，因为 client 发起删除所有子目录，Server 会删除整个 dir2/b/b.txt，第二步，重新创建 /dir2/b/
	 *    /dir2/c/c1/c2.txt  -> delete ( server ), 特别注意的是，此删除过程分为两步完成，因为是每次同步只删除一个子节点，所以需要分两次同步进行删除，第一次同步删除 c2.txt，第二次同步删除 /c1
	 * 	  	
	 * -> All anticipate results to update the server side
	 *    /a/a.txt  	-> update ( client )
	 *    /a/b.txt  	-> insert ( server )
	 *    /b/       	-> insert ( server )
	 *    /c/c.txt  	-> insert ( server )
	 *    /c/c1.txt  	-> insert ( server )
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
		
		// 因为 V0.1 不包含 Server update Client 的场景，所以修复测试用例。 让 /dir1/a/a.txt 更新 /dir2/a/a.txt
		TimeUnit.MILLISECONDS.sleep(500);
		
		new File( rootpath + "/dir1/a/a.txt").setLastModified( System.currentTimeMillis() );
		
	}
	
	public static void cleanupTestCases1(String rootpath) throws IOException{
		
		if( new File( rootpath + "/dir1" ).exists() )
			FileUtils.forceDelete( new File( rootpath + "/dir1" ) );
		
		if( new File( rootpath + "/dir2" ).exists() )
			FileUtils.forceDelete( new File( rootpath + "/dir2" ) );
		
		if( new File( rootpath + "/dir3" ).exists() )
			FileUtils.forceDelete( new File( rootpath + "/dir3" ) );
	}
	
	public static void createFile(String path, String content) throws IOException{
		
		File file = new File(path);
		
		if( !path.endsWith("/") ){
			
			FileUtils.forceMkdirParent( file );
			
			FileUtils.write( file, content == null ? "xx" : content, Charset.defaultCharset() );
		
		} else{
			
			FileUtils.forceMkdir( file ); // or else dir1/b cannot be created
			
		}
		
	}
	
}
