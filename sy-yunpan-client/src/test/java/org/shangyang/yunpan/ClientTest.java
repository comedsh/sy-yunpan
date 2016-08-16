package org.shangyang.yunpan;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shangyang.yunpan.client.Client;
import org.shangyang.yunpan.directory.FileAction;
import org.shangyang.yunpan.directory.FileChecker;
import org.shangyang.yunpan.directory.FileDTO;
import org.shangyang.yunpan.directory.FileDifference;
import org.shangyang.yunpan.directory.TestUtils;
import org.shangyang.yunpan.server.SyncServer;

public class ClientTest {
	
	static String rootpath;
	
	static String basepath1;
	
	static String basepath2;
	
	static String basepath3; // the server directory
	
	FileDifference differ = ServiceLoader.load( FileDifference.class ).iterator().next();
	
	SyncServer syncServer = ServiceLoader.load( SyncServer.class ).iterator().next();
	
	Client client = null;
	
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
		
//		FileUtils.forceDeleteOnExit( new File( basepath3 ) );
		
		TestUtils.cleanupTestCases1(rootpath);		
		
		TestUtils.makeupTestCases1(rootpath);
		
		FileUtils.forceMkdir( new File( basepath3 ) );
		
		client = Client.getInstance();
		
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
	public void testSync1() throws Exception{
		
		client.setBasePathPath(basepath1);
		
		setServerBasePath(syncServer, basepath3);
			
		client.sync(); 
		
		List<FileDTO> client = FileChecker.getInstance().check( new File(basepath1) );
		
		List<FileDTO> server = FileChecker.getInstance().check( new File(basepath3) );
		
		assertTrue( CollectionUtils.isEmpty( differ.difference(client, server ) ) );
		
	}
	
	/**
	 * 同步
	 * 
	 * 有趣的是，某些操作，特别是与文件夹相关的 case，往往需要两步或者更多步骤完成同步，详情参考 {@link TestUtils#makeupTestCases1(String) }
	 * 
	 */
	@Test
	public void testSync2() throws Exception{
		
		client.setBasePathPath(basepath1);
		
		setServerBasePath(syncServer, basepath2);
		
		client.sync();
		
		TimeUnit.MILLISECONDS.sleep(1000); 
		
		List<FileDTO> source = FileChecker.getInstance().check( new File(basepath1) );
		
		List<FileDTO> target = FileChecker.getInstance().check( new File(basepath2) );
		
		System.out.println("========================================================");
		
		List<FileAction> actions = differ.difference( source, target );
		
		assertTrue("dir2/b/b.txt and dir2/c/c1/c1.txt both takes 2 steps get synchronized", actions.size() == 2 );
		
		assertTrue( FileUtils.readFileToString( new File(basepath2+"/a/a.txt"), "UTF-8" ).equals("a") );

		for( FileAction action : actions ){
			
			System.out.println(action.toString());
			
		}
		
		client.sync();
		
		source = FileChecker.getInstance().check( new File(basepath1) );
		
		target = FileChecker.getInstance().check( new File(basepath2) );		
		
		assertTrue("status synchroized", differ.difference(source, target ).size() == 0 );
		
		
	}
	
	@Test
	public void testOther(){
		
		// 1471246807000
		System.out.println( new File(basepath1+"/b/").lastModified() );
		
	}
	
	void setServerBasePath( SyncServer syncServer, String basepath ) throws Exception{
		
		ClassLoader cl = (ClassLoader) syncServer.getClass().getDeclaredMethod("getClassLoader").invoke(syncServer, (Object[])null );
		
		Class<?> clazz = cl.loadClass("org.shangyang.yunpan.server.Repository");
		
		Object instance = clazz.getDeclaredMethod("getInstance").invoke(null, (Object[]) null);
		
		clazz.getDeclaredMethod("setBasePath", String.class ).invoke(instance, basepath );
		
	}
	
}
