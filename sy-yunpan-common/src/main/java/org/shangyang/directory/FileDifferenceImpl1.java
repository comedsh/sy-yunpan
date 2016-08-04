package org.shangyang.directory;

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
public class FileDifferenceImpl1 implements IFileDifference{
	
	/**
	 * 第一个版本，只通过 Client 往 Server 同步，只考虑单边的情况，所以，target -> Server
	 * 
	 * 1. client 和 server 都有，比较 last update, 不相等 -> action update.
	 * 2. client 有, server 没有 -> action paste
	 * 3. client 没有，server 有 -> action delete
	 *    2.1 client 删除
	 *    2.2 client 移动 
	 *    
	 */
	public List<FileAction> difference( List<FileDTO> sources, List<FileDTO> targets ){
		
		Collections.sort(sources);
		
		Collections.sort(targets);
		
		List<FileAction> actions = new LinkedList<FileAction>();
		
		// caches those common parts
		List<FileDTO> temps = new LinkedList<FileDTO>();
		
		for( FileDTO s : sources ){
			
			boolean found = false;
			
			for( FileDTO t : targets ){
				
				// resolve #1
				if( s.equals(t) ){
					
					found = true;
					
					// 只有修改时间上不匹配的，认为有修改。这里，因为只考虑客户端覆盖服务器端的情况，所以，无论哪种情况，都直接覆盖之
					if( Long.compare( s.getLastModified().getTime(), t.getLastModified().getTime() ) != 0 ){
						
						FileAction a = new FileAction( s.getPath(), ActionEnum.UPDATE );
						
						actions.add(a);
						
					}	
					
					temps.add( t ); 
					
					break;
					
				}
				
			}
			
			if( found == true ) continue; // 找到了文件名匹配的文件，执行下一个操作。
			
			// resolved #2
			actions.add( new FileAction( s.getPath(), ActionEnum.INSERT ) );
			
		}
		
		// resolved #3
		targets.removeAll( temps ); // temps 是 client 与 Server 共有的部分，server 移除这些共有的部分后，自然剩下的就是 Server 多余的部分了。
		
		for( FileDTO t : targets ){
			
			actions.add( new FileAction( t.getPath(), ActionEnum.DELETE ) );
			
		}		
		
		return actions;
	}
	
}
