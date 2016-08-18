package org.shangyang.yunpan.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * of 2nd version, web based, plan to replace this with database with accounts and security.
 * 
 * @author 商洋
 *
 */
public class Repository {

	static Repository r;
	
	String basePath;
	
	public static Repository getInstance(){
		
		if( r == null ){
			
			r = new Repository();
			
			// FIXME, find a better to set this
			System.setProperty("logpath", r.getLogpath() );  // so the log4j can dynamic read the ${logpath}
			
		}
		
		return r;
		
	}
	
	/**
	 * Default is fetched from server.properties file, but it can be override.
	 * 
	 * @return
	 */
	public String getSyncBase() {
		
		String basepath;
		
		if( StringUtils.isEmpty( getBasePath() ) ){
			
			basepath = read("basepath", this.getClass().getResourceAsStream("/repository.properties"), "UTF-8");

		}else{
			
			basepath = getBasePath();
			
		}
		
		return basepath;

	}

	public String getLogpath(){
		
		return read("logpath", this.getClass().getResourceAsStream("/repository.properties"), "UTF-8" );
		
	}
	
	String read(String propertyName, InputStream stream, String charSet){
		
		if( StringUtils.isEmpty(charSet) ){
			
			charSet = Charset.defaultCharset().name();
			
		}
		
		try{
			
			Properties prop = new Properties();
			
			InputStreamReader in = new InputStreamReader(stream,  charSet );
	
			prop.load(in);
			
			return prop.getProperty(propertyName).trim();
			
		}catch(IOException e){
			
			throw new RuntimeException(e);
			
		}
		
	}
	
	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	
	
}
