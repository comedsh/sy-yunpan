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
	 * 注意，这个方法不具备幂等性，因为 targets 会通过 targets.removeAll(temps) 而发生改变。另外，sources 和 targets 的也会进行排序
	 * 
	 * @param sources the file structure of client
	 * @param targets the file structure of server   
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
				
				// special case for Directory only, 
				// 1, if T is sub dir of S, then delete all the sub dirs. 
				// 2. if totally matches, do nothing. 
				// 3. if S is sub dir of T, insert s.  
				if( s.isDirectory() ){
					
					// handles #1 and #2 above
					if( t.getRelativePath().startsWith( s.getRelativePath() ) ){
						
						// #1 indicates the current t dir has sub dirs against s
						if( t.getRelativePath().replaceAll(s.getRelativePath(), "").length() > 0 ){
						
							actions.add( new FileAction( s, ActionEnum.DELETE, TargetEnum.SERVER ) );
							
						}
						
						// #2 means totally matches
						
						// means s found t. 
						found = true;
						
						temps.add( t );
						
						break;
						
						// then how about #3, if s has more sub dirs than t, this is the normal cases, will handles via the common flows. 
						
					}				
										
				// resolve #1, if two file matches, compare with the status to decide the action needs to be took.
				}else if( s.equals(t) ){
					
					FileAction a = genUpdateAction( s, t);
					
					if( a != null ) actions.add(a);
					
					found = true;
					
					temps.add( t ); 
					
					break;
					
				}
				
				
				
			}
			
			if( found == true ) continue; // 找到了文件名匹配的文件，执行下一个。PS: 真希望 Java 有 GOTO
			
			// resolved #2, s found no matches from Target, so insert case.
			actions.add( new FileAction( s, ActionEnum.INSERT, TargetEnum.SERVER ) );
			
			
		}
		
		// resolved #3
		targets.removeAll( temps ); // temps 是 client 与 Server 共有的部分，server 移除这些共有的部分后，自然剩下的就是 Server 多余的部分了。
		
		for( FileDTO t : targets ){
			
			actions.add( new FileAction( t, ActionEnum.DELETE, TargetEnum.SERVER ) );
			
		}		
		
		return actions;
	}
	
	/**
	 * Resolves the Directory scenarios
	 * 
	 * case I, do nothing
	 *   dir1/d1/
	 *   dir2/d1/
	 *   
	 * case II, delete all the sub directories and files of dir2, but remain the d1  
	 *   dir1/d1/
	 *   dir1/d1/d2
	 *   dir1/d1/d3/f1
	 *   dir1/d1/d4/f2
	 * 
	 * case III, 
	 * 
	 *   dir1/d1/d2/d3/
	 *   dir2/d1/d2/f2
	 *   dir2/d1/d3
	 *   
	 * 
	 * @param sources
	 * @param targets
	 * @return
	 */
	List<FileAction> difference0(List<FileDTO> sources, List<FileDTO> targets){
		
		List<FileAction> actions = new LinkedList<FileAction>();
		
		for( FileDTO s : sources ){
			
			for( FileDTO t : targets ){
				
				if( t.getRelativePath().startsWith( s.getRelativePath() ) ){
					
					// case II matched
					if( t.getRelativePath().replaceAll( s.getRelativePath(), "" ).length() > 0 ){
						
						actions.add( ( new FileAction(s, ActionEnum.DELETE, TargetEnum.SERVER ) ) );
						
						break;
						
					}
					
				}
				
			}
			
			actions.add( new FileAction(s, ActionEnum.INSERT, TargetEnum.SERVER ) );
			
			
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
