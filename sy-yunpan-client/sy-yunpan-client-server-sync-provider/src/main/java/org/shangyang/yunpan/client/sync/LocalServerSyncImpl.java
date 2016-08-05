package org.shangyang.yunpan.client.sync;

import java.util.List;

import org.shangyang.directory.FileDTO;

/**
 * 
 * implements for the very first version, that the server is on the local.
 * 
 * but because it is the server, so do not project reference it
 * 
 * switch to use ClassLoader to load it indicate it is the remote case.
 * 
 * @author 商洋
 *
 */
public class LocalServerSyncImpl implements ServerSync{
	
	private static ServerSync sync = null;
	
	public static ServerSync getInstance(){
		
		if( sync == null ){
			
			sync = new LocalServerSyncImpl();
			
		}				
		
		return sync;
	}
	
	@Override
	public List<FileDTO> check() {
		return null;
	}

}
