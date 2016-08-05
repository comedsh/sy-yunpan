package org.shangyang.yunpan.server;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shangyang.yunpan.directory.ActionEnum;
import org.shangyang.yunpan.directory.FileAction;
import org.shangyang.yunpan.directory.FileDTO;
import org.shangyang.yunpan.directory.TestUtils;

public class SyncServerTest {

	SyncServer server = ServiceLoader.load( SyncServer.class ).iterator().next();
	
	static String rootpath;
	
	static String basepath1;
	
	static String basepath2;
	
	@BeforeClass
	public static void beforeClass() throws Exception{
		
		// remove the current path indicator "." from Linux System
		rootpath = StringUtils.removeEnd( new File(".").getCanonicalPath(), "." ) + "/src/test/resources";
		
		rootpath = rootpath.replace("sy-yunpan-server", "sy-yunpan-common");
		
		basepath1 = rootpath + "/dir1/";
		
		basepath2 = rootpath + "/dir2/";
		
		TestUtils.cleanupTestCases0(rootpath);
		
		TestUtils.makeupTestCases0(rootpath);
		
	}
	
	@Test
	public void testCheck(){
		
		Repository.getInstance().setBasePath( basepath2 );
		
		List<FileDTO> files = server.check();
		
		for(FileDTO f:files){
			System.out.println( f.toString() );
		}
		
	}
	
	/**
	 * 0. folder
	 *    /dir1/b/   -> 直接删除 服务器对应的子目录
	 * 
	 * 1. update 
	 *    /dir1/a/a.txt -> /dir2/a/a.txt
	 *    
	 * 2. insert
	 * 	  /dir1/b -> /dir2/b
	 *    /dir1/c/c.txt -> /dir2/c/c.txt 
	 * 
	 * 3. delete
	 *    /dir2/c/c1/c2.txt
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testSync() throws IOException{
		
		Repository.getInstance().setBasePath( basepath2 );
		
		// #0 folder -> 特别的，特立独行的
		
		File source = new File( basepath1 + "/b/");
		
		assertTrue( source.exists() && source.isDirectory() );
		
		FileAction action = new FileAction( new FileDTO( "/b/", new Date( source.lastModified() ), false ), ActionEnum.INSERT );
		
		server.sync( action ); // 单独同步文件夹
		
		File target = new File( basepath2 + "/b/");
		
		assertTrue( target.exists() && target.isDirectory() );
		
		assertTrue("时间戳相等", target.lastModified() == source.lastModified() );		
		
		// #1 case
		
		source = new File( basepath1 + "/a/a.txt" );
		
		target = new File( basepath2 + "/a/a.txt" );
		
		// 内容不相等
		assertTrue( StringUtils.equals( FileUtils.readFileToString(source, "UTF-8"), "a" ) );
		
		assertTrue( StringUtils.equals( FileUtils.readFileToString(target, "UTF-8"), "aa" ) );
		
		// last modified 不相等
		assertTrue( source.lastModified() != target.lastModified() );
		
		action = new FileAction( new FileDTO( "/a/a.txt", new Date( source.lastModified() ), true ) , ActionEnum.UPDATE );
		
		server.sync( action, source );
		
		// sync 以后，服务器内容、修改日期必须完全和客户端相同
		assertTrue("should be overrided", StringUtils.equals( FileUtils.readFileToString(target, "UTF-8"), "a" ) );
		
		assertTrue("and last modified also should equals", source.lastModified() == target.lastModified() ); 
		
		// #2 case

		source = new File( basepath1 + "/c/c.txt");
		
		assertTrue( source.exists() && source.isFile() );
		
		action = new FileAction( new FileDTO( "/c/c.txt", new Date( source.lastModified() ), true ), ActionEnum.INSERT );
		
		server.sync( action, source );
		
		target = new File( basepath2 + "/c/c.txt");
		
		assertTrue( target.exists() );
		
		assertTrue("时间戳相等", target.lastModified() == source.lastModified() );		
		
		// #3 case
		target = new File( basepath2 + "/c/c1/c2.txt");
		
		assertTrue( target.exists() && target.isFile() );
		
		action = new FileAction( new FileDTO( "/c/c1/c2.txt", null, true ), ActionEnum.DELETE );
		
		server.sync( action, null );
		
		assertFalse( target.exists() );				
		
	}

	
	
}
