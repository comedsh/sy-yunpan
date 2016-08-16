package org.shangyang.yunpan.client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.log4j.Logger;
import org.shangyang.yunpan.directory.FileAction;
import org.shangyang.yunpan.directory.FileDTO;
import org.shangyang.yunpan.server.SyncServer;

public class SyncServerRemoteImpl1 implements SyncServer{
	
	Object instance = null;
	
	Class<?> clazz = null;
	
	public static Logger logger = Logger.getLogger( SyncServerRemoteImpl1.class );
	
	public SyncServerRemoteImpl1(){
		
		try {
			
			clazz= MyClassLoader.getInstance().loadClass("org.shangyang.yunpan.server.SyncServerImpl1");
			
			instance = clazz.newInstance();
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
	}
	
	public ClassLoader getClassLoader(){
		
		return MyClassLoader.getInstance();
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<FileDTO> check() {
		
		try {
			
			return (List<FileDTO>) instance.getClass().getDeclaredMethod("check").invoke(instance, (Object[]) null);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			
			throw new RuntimeException( e );
			
		}
		
		
	}

	@Override
	public void sync(FileAction action, File source) throws IOException {
		
		try{
			
			instance.getClass().getDeclaredMethod("sync", FileAction.class, File.class).invoke(instance, action, source);
		
		}catch(Exception e){
			
			throw new RuntimeException(e);
			
		}
		
	}

}

class MyClassLoader extends URLClassLoader{
	
	static MyClassLoader classloader = null;
	
	public static MyClassLoader getInstance(){
		
		try{
		
			if( classloader == null ){
				
				String serverClasspath;
	
				// 特别注明：必须要有最后的那根斜杠，否则会报错，为什么必须要有这根斜杠？难道没有这根斜杠 ClassLoader 不会认为它是一个目录？
				serverClasspath = new File(".").getCanonicalPath().replaceAll("sy-yunpan-client", "sy-yunpan-server") + "/bin/";
				
				if( !new File(serverClasspath).exists() ){
					
					serverClasspath = new File(".").getCanonicalPath() + "/";
				}
				
				SyncServerRemoteImpl1.logger.debug( "serverClasspath:" + serverClasspath );
				
				URL url = new URL("file://" + serverClasspath );
				
				classloader = new MyClassLoader( new URL[]{ url } );
				
			}
			
			return classloader;
		
		}catch(Exception e){
			
			throw new RuntimeException(e);
			
		}
		
	}
	
	public MyClassLoader(URL[] urls) {
		
		super( urls );
	}
	
	
}
