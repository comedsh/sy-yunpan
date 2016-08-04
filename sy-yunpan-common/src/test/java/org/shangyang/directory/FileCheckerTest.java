package org.shangyang.directory;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class FileCheckerTest {

	FileChecker service = FileChecker.getInstance();
	
	@Test
	public void testCheck() throws IOException{

		String basepath = StringUtils.removeEnd( new File(".").getCanonicalPath(), "." ); // disable the "." path of linux system.
		
		String path =  basepath + "/bin/org/shangyang/directory/test.txt";
		
		boolean error = false;
		
		try{
			
			service.check( new File(path) );
			
		}catch(java.lang.AssertionError e){
			
			error = true;
			
		}
		
		assertTrue(error);		
		
		List<FileDTO> files = service.check( new File( basepath ) );
		
		for( FileDTO f : files ){
			
			System.out.println( f.getPath() + "; " + new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.dddddd").format( f.getLastModified() ) );
			
		}
		
	}
	
	@Test
	public void testCheckPressure() throws IOException{
		
		long time = System.currentTimeMillis();
		
		long free = Runtime.getRuntime().freeMemory();
		
		String path = "/Users/mac/Documents/OneDrive"; // 这就是 basepath
		
		List<FileDTO> files = service.check( new File(path) );
		
		System.out.println("totally read "+ files.size() + " files ");
		
		long m_consumes = free - Runtime.getRuntime().freeMemory();
		
		long t_consumes = System.currentTimeMillis() - time;
		
		System.out.println("Memory consumes: " + m_consumes / 1000 / 1000 + " MB");		
		
		System.out.println("Time consumes:" +  t_consumes + " ms" );
	}
	
	
}
