package org.shangyang.yunpan.directory;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileCommonTest {

	static String rootpath;
	
	@BeforeClass
	public static void beforeClass() throws IOException{
		
		rootpath = new File(".").getCanonicalPath();
		
	}
	
	/**
	 * 测试文档当被写的时候是否可以读
	 * 
	 * results: 可被读取... 
	 * 
	 * @throws IOException 
	 */
	@Test
	public void testReadWhenWriting() throws IOException{
		
		File file = new File( rootpath + "/src/test/resources/test.txt" );
		
		assertTrue( file.exists() );
		
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader( new FileReader(file) );
		
		String line = null;
		
		while( ( line = reader.readLine() ) != null  ){
			
			System.out.println(line);
		}
		
		file = new File( rootpath + "/src/test/resources/test.docx" );
		
		reader = new BufferedReader( new FileReader(file) );
		
		line = null;
		
		while( ( line = reader.readLine() ) != null  ){
			
			System.out.println(line);
		}
		
	}
	
	/**
	 * 
	 * 这个要取决于编辑器了，如果编辑该文档的编辑器不是以独占的方式打开文档，那么是没办法通过如下的方式进行判断的。
	 * 
	 * test.docx 测试通过。
	 * 
	 * 副作用，如果获得了锁，fos 关闭以后，还是会强行写入，以至于将原有的文件内容全部替换了。
	 * 
	 * @throws IOException 
	 */
	@Test
	public void testIsFileOpening() throws IOException{
		
		File file = new File( rootpath + "/src/test/resources/test.txt" );
		
		@SuppressWarnings("resource")
		FileOutputStream fos = new FileOutputStream( file );
		
        FileChannel fc = fos.getChannel();
        
        FileLock lock = fc.tryLock();
		
        if( lock == null ){
        	
        	System.out.println("the file is opening");
        	
        }else{
        	
        	System.out.println("the file is not opening");
        	
        }
        
        lock.release();       
        
	}
	
	@Test
	public void testDirectory() throws Exception{
		
		String filepath = StringUtils.removeEnd(new File(".").getCanonicalPath(), ".") + "/src/test/resources/dir1/d/";
		
		if( new File(filepath).exists() ) new File(filepath).delete();
		
		TestUtils.createFile(filepath, null);
		
		File dir = new File(filepath);
		
		System.out.println( dir.lastModified() );
		
		TimeUnit.MILLISECONDS.sleep( 500 );
	
		dir.setLastModified(System.currentTimeMillis() - 1000000 );
		
		System.out.println( dir.lastModified() );
	}
	
	@Test
	public void testAbsolutePath() throws Exception{
		
		System.out.println(System.getProperty("user.dir"));
		
		System.out.println(System.getProperty("java.class.path"));
		
		System.out.println(Object.class.getResource("/org/shangyang/yunpan/directory/FileCommonTest.class").getPath()); 
		
		System.out.println( "/Users/mac/Desktop/sy-yunpan.app/Contents/MacOS/lib/sy-yunpan-client-0.0.1.jar".replace(".*lib.*jar", "") );
		
	}
	
	@Test
	public void testSystemProperties(){
		
		System.getProperties().list(System.out);
		
	}
	
}
