package org.shangyang.directory;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

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
	
}
