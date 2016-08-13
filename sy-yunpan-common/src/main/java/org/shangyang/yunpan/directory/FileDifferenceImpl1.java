package org.shangyang.yunpan.directory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * 第一个版本的实现，只考虑额 Client 同步 Server 端
 * 
 * @author 商洋
 *
 */
public class FileDifferenceImpl1 implements FileDifference{
	
	/**
	 * Initial Synchronization step, uses the server as the base snapshot to override the client.
	 * 
	 * 1. client 和 server 都有，比较 last update，... who less update him.
	 * 2. client 有 server 没有，do nothing
	 * 3. client 没有 server 有，覆盖 client
	 * 
	 * @param sources the file structure of client
	 * @param targets the file structure of server
	 * 
	 */
	@Override
	public List<FileAction> difference0(List<FileDTO> sources, List<FileDTO> targets) {
		
		Collections.sort(sources);
		
		Collections.sort(targets);
		
		List<FileAction> actions = new LinkedList<FileAction>();
		
		// caches those common parts
		List<FileDTO> temps = new LinkedList<FileDTO>();
		
		for( FileDTO s : sources ){
			
			for( FileDTO t : targets ){
				// #1
				if( s.equals(t) ){
					
					// why only update the client, see the Use Case diagram of comments #5
					if( s.getLastModified().getTime() < t.getLastModified().getTime() ){
						
						FileAction a = new FileAction( t, ActionEnum.UPDATE, TargetEnum.CLIENT );
						
						actions.add( a );
						
					}
					
					temps.add( t );
					
					break;
					
				}
				
			}
			
			// #2
			
		}
		
		// #3
		targets.removeAll( temps );
		
		for( FileDTO t : targets ){
			
			actions.add( new FileAction( t, ActionEnum.INSERT, TargetEnum.CLIENT ) );
			
		}			
		
		return actions;
	}	
	
	/**
	 * Normal Synchronization step, uses the client as the base snapshot to override the server.
	 * 
	 * 1. client 和 server 都有，比较 last update, 
	 *    1.1 client last update < server last update -> update client
	 *    1.2 client last update > server last update -> update server 
	 * 2. client 有 server 没有 -> action insert [server]
	 * 3. client 没有 server 有 -> action delete [server]
	 *    2.1 client 删除
	 *    2.2 client 移动 
	 *    
	 * 注，构建 FileAction 的时候，TargetEnum 的取值取决于用谁的 last update timestamp   
	 * 
	 * @param sources the file structure of client
	 * @param targets the file structure of server   
	 *    
	 */
	public List<FileAction> difference1( List<FileDTO> sources, List<FileDTO> targets ){
		
		Collections.sort(sources);
		
		Collections.sort(targets);
		
		List<FileAction> actions = new LinkedList<FileAction>();
		
		// caches those common parts
		List<FileDTO> temps = new LinkedList<FileDTO>();
		
		for( FileDTO s : sources ){
			
			boolean found = false;
			
			boolean hasSubfolder = false;
			
			for( FileDTO t : targets ){
				
				// resolve #1
				if( s.equals(t) ){
					
					found = true;
					
					FileAction a = genUpdateAction( s, t);
					
					if( a != null ) actions.add(a);
					
					temps.add( t ); 
					
					break;
					
				}
				
				if( s.isDirectory() ){
				
					if( s.getRelativePath().indexOf( t.getRelativePath() ) >= 0 ){
						
						hasSubfolder = true; 
						
						break;
						
					}
						
				}
				
			}
			
			if( found == true ) continue; // 找到了文件名匹配的文件，执行下一个。PS: 真希望 Java 有 GOTO
			
			// special case for folder, if client only have a folder, that's not means insert into server but the deletion.
			if( s.isDirectory() ){
				
				if( hasSubfolder == true ){
				
					actions.add( new FileAction( s, ActionEnum.DELETE, TargetEnum.SERVER ) ); // 直接删除服务器的子目录

				}else{
					
					actions.add( new FileAction( s, ActionEnum.INSERT, TargetEnum.SERVER ) );
				}
				
			}else{
			
				// resolved #2
				actions.add( new FileAction( s, ActionEnum.INSERT, TargetEnum.SERVER ) );
			}
			
		}
		
		// resolved #3
		targets.removeAll( temps ); // temps 是 client 与 Server 共有的部分，server 移除这些共有的部分后，自然剩下的就是 Server 多余的部分了。
		
		for( FileDTO t : targets ){
			
			actions.add( new FileAction( t, ActionEnum.DELETE, TargetEnum.SERVER ) );
			
		}		
		
		return actions;
	}
	
	private FileAction genUpdateAction( FileDTO s, FileDTO t ){
		
		FileAction a = null;
		
		int c = Long.compare( s.getLastModified().getTime(), t.getLastModified().getTime() );
		
		if( c < 0 ){
			
			a = new FileAction( t, ActionEnum.UPDATE, TargetEnum.CLIENT );
			
		}

		if( c > 0 ){
			
			a = new FileAction( s, ActionEnum.UPDATE, TargetEnum.SERVER );
			
		}	
		
		return a;
	}
	
}
