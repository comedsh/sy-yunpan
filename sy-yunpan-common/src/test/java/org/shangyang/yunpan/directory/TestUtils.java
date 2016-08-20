package org.shangyang.yunpan.directory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class TestUtils {
	
	/**
	 * makes up the default test cases and the anticipation results for the normal synchronization
	 * 
	 * This test cases made only for the v0.0.1 sync, the Server is the totally mirror from Client only, means there is no update from Server to Client
	 * 
	 * -> case for base /dir1
	 * 	  /dir1/a/a.txt 'a' 
	 *    /dir1/a/b.txt	    -> insert ( server )
	 *    /dir1/b/          -> nothing to do. 
	 *    /dir1/c/c.txt     -> insert ( server )
	 *    /dir1/c/c1.txt    -> insert ( server )
	 *    /dir1/d			-> insert ( server )
	 * 
	 * wait 2 seconds to create base /dir2 cases, so the last ts of dir2 cases always bigger than dir1 cases
	 * 
	 * -> case for base /dir2
	 *    /dir2/a/a.txt 'aa' -> update ( client )
	 *    /dir2/a/c.txt 	 -> delete ( server )
	 *    /dir2/b/b.txt 	 -> delete ( server ), delete b.txt 
	 *    /dir2/c/c1/c2.txt  -> delete ( server ), 特别注意的是，此删除过程分为两步完成，因为是每次同步只删除一个子节点，所以需要分两次同步进行删除，第一次同步删除 c2.txt，第二次同步删除 /c1
	 *    
	 * -> results of server
	 * 	  /dir2/a/a.txt 'a'
	 * 	  /dir2/a/b.txt
	 *    /dir2/b/
	 *    /dir2/c/c.txt
	 *    /dir2/c/c1.txt
	 *    /dir2/d   
	 *    
	 * Attention, normal sync case do not have client deleted/inserted cases, those steps only happens on initial steps   
	 *    
	 * @throws Exception 
	 * 
	 */
	public static void makeupTestCases1(String rootpath) throws Exception{
		
		Date lastModification = new Date();
		
		createFile( rootpath + "/dir1/a/a.txt", "a", lastModification );
		createFile( rootpath + "/dir1/a/b.txt", null, lastModification );
		createFile( rootpath + "/dir1/b/", null, lastModification );
		createFile( rootpath + "/dir1/c/c.txt", null, lastModification );
		createFile( rootpath + "/dir1/c/c1.txt", null, lastModification );
		createFile( rootpath + "/dir1/d/", null, lastModification );
		updateLastModifications( new File( rootpath + "/dir1/" ), lastModification );
		
		createFile( rootpath + "/dir2/a/a.txt", "aa", lastModification );
		createFile( rootpath + "/dir2/a/c.txt", null, lastModification );
		createFile( rootpath + "/dir2/b/b.txt", null, lastModification );
		createFile( rootpath + "/dir2/c/c1/c2.txt", null, lastModification );
		updateLastModifications( new File( rootpath + "/dir2/" ), lastModification );
		
		createFile( rootpath + "/dir3/", null, lastModification );		
		
		TimeUnit.MILLISECONDS.sleep(1000);
		
		// made larger last modification, so Client will update Server.
		new File( rootpath + "/dir1/a/a.txt").setLastModified( System.currentTimeMillis() );
		
	}
	
	public static void updateLastModifications( File file, Date last ){
		
		java.io.File[] fs = file.listFiles();

		if ( fs == null ) {
			return;
		}

		for (java.io.File f : fs) {
			
			f.setLastModified( last.getTime() );
			
			if ( f.isDirectory() ) 
				
				updateLastModifications( f, last );
				
			}
	}
	
	public static void cleanupTestCases1(String rootpath) throws IOException{
		
		if( new File( rootpath + "/dir1" ).exists() )
			FileUtils.forceDelete( new File( rootpath + "/dir1" ) );
		
		if( new File( rootpath + "/dir2" ).exists() )
			FileUtils.forceDelete( new File( rootpath + "/dir2" ) );
		
		if( new File( rootpath + "/dir3" ).exists() )
			FileUtils.forceDelete( new File( rootpath + "/dir3" ) );
	}
	
	public static void createFile(String path, String content, Date last) throws IOException{
		
		File file = new File(path);
		
		if( !path.endsWith("/") ){
			
			FileUtils.forceMkdirParent( file );
			
			FileUtils.write( file, content == null ? "xx" : content, Charset.defaultCharset() );
		
		} else{
			
			FileUtils.forceMkdir( file ); // or else dir1/b/ cannot be created
			
		}
		
		if( last != null ) file.setLastModified( last.getTime() );
		
	}
	
}
