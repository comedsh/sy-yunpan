package org.shangyang.yunpan.server;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shangyang.directory.FileDTO;
import org.shangyang.directory.TestUtils;

public class SyncServerTest {

	SyncServer server = new SyncServerImpl();
	
	static String rootpath;
	
	static String basepath;
	
	@BeforeClass
	public static void beforeClass() throws Exception{
		
		// remove the current path indicator "." from Linux System
		rootpath = StringUtils.removeEnd( new File(".").getCanonicalPath(), "." ) + "/src/test/resources";
		
		rootpath = rootpath.replace("sy-yunpan-server", "sy-yunpan-common");
		
		basepath = rootpath + "/dir2/";
		
		TestUtils.cleanupTestCases0(rootpath);
		
		TestUtils.makeupTestCases0(rootpath);
		
	}
	
	@Test
	public void testCheck(){
		
		Repository.getInstance().setBasePath( basepath );
		
		List<FileDTO> files = server.check();
		
		for(FileDTO f:files){
			System.out.println( f.toString() );
		}
		
	}

	
	
}
