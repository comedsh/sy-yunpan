package org.shangyang.yunpan.directory;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 
 * 第一个版本的实现，只考虑额 Client 同步 Server 端
 * 
 * @author 商洋
 *
 */
public class FileDifferenceImpl1 implements FileDifference{
	
	Logger logger = Logger.getLogger( FileDifferenceImpl1.class );
	
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
	 * 记录一次 performance tuning, 之前的做法是，通过 temps 缓存 sources 与 targets 两者匹配的文件，到最后再删除 remove temps 以后的 targets 的笨办法，结果导致这个方法每次都要执行5分钟左右，性能很差，
	 * tuning的思路也很简单，不缓存而是直接 remove targets 与 sources 匹配的部分，剩下的就是 targets 需要 DELETE 的。之前的时间复杂度为 120K * n 既是 120K*n 的复杂度 (n 表示 t 循环多少次与 s 匹配，然后 break)，
	 * 而现在为 n 始终为 1，所以复杂度为 120K*1，因为 s 和 t 经过排序的，t 每次都 remove 与 s 之前的匹配，所以 t 的下一个取值始终与 s 的下一个取值匹配 break，所以每次循环的复杂度为 1 了，整个复杂度就将为了 120K*1 了，
	 * 提升了 n 倍的性能；从之前的 350秒左右提升到现在仅需要 11 秒的时间 提升了接近 30 倍的性能。
	 * 
	 * 
	 * @param sources the file structure of client
	 * @param targets the file structure of server   
	 *    
	 */
	public List<FileAction> difference( List<FileDTO> sources, List<FileDTO> targets ){
		
		long start = System.currentTimeMillis();
		
		long free = Runtime.getRuntime().freeMemory();
		
		logger.debug("start difference check");
		
		Collections.sort(sources);
		
		Collections.sort(targets);
		
		logger.debug("time spent on collections sort from start " + ( System.currentTimeMillis() - start ) /1000 +" seconds" );
		
		List<FileAction> actions = new LinkedList<FileAction>();
		
		// caches those common parts
		// List<FileDTO> temps = new LinkedList<FileDTO>();
		
		for( FileDTO s : sources ){
			
			boolean found = false;
			
			for( FileDTO t : targets ){
				
				// special case for Directory only, 
				// 1, if T is sub dir of S, then delete all the sub dirs. ( T represents Target directories, S represents Source directories )
				// 2. if totally matches, do nothing. 
				// 3. if S is sub dir of T, insert s.  
				if( s.isDirectory() ){
					
					// handles #1 and #2 above
					if( t.getRelativePath().startsWith( s.getRelativePath() ) ){
						
						// #1 indicates the current t dir has sub dirs against s
						
						// String remain = t.getRelativePath().replace(s.getRelativePath(), ""); // replace replaceAll, replace() is works.
						String remain = StringUtils.removeEnd(t.getRelativePath(), s.getRelativePath());
						
						// 有剩余部分，且剩余部分必须是 directory, fix the #v0.0.1 bug1
						if( remain.length() >0  && remain.startsWith( File.separator ) ) {
						
							actions.add( new FileAction( s, ActionEnum.DELETE, TargetEnum.SERVER ) );
						}

						// #2 means totally matches
						
						// if #1 and #2 cases that means found the matched t. 
						found = true;
						
						// temps.add( t ); // why not directly remove it from targets for saving performance?
						
						targets.remove( t );
						
						break;
						
						// then how about #3, if s has more sub dirs than t, this is the normal cases, will handles via the common flows. 
					}				
										
				// resolve #1, if two file matches, compare with the status to decide the action needs to be took.
				}else if( s.equals(t) ){
					
					FileAction a = genUpdateAction( s, t);
					
					if( a != null ) actions.add(a);
					
					found = true;
					
					// temps.add( t ); // why not directly remove it from targets for saving performance?
					
					targets.remove( t );
					
					break;
				}
				
			}
			
			if( found == true ) continue; // 找到了文件名匹配的文件，执行下一个。PS: 真希望 Java 有 GOTO
			
			// resolved #2, s found no matches from Target, so insert case.
			actions.add( new FileAction( s, ActionEnum.INSERT, TargetEnum.SERVER ) );
		}
		
		logger.debug("time spent on collections comparasion " + ( System.currentTimeMillis() - start ) /1000 +" seconds" );
		
		// resolved #3 -> performance tuning -> directly removed
		// targets.removeAll( temps ); // temps 是 client 与 Server 共有的部分，server 移除这些共有的部分后，自然剩下的就是 Server 多余的部分了。
		
		for( FileDTO t : targets ){
			
			actions.add( new FileAction( t, ActionEnum.DELETE, TargetEnum.SERVER ) );
		}		
		
		
		logger.debug("difference check completed, time spent "+ ( System.currentTimeMillis() - start )/1000 +" seconds,"
				+ " and memory consumes " + ( free - Runtime.getRuntime().freeMemory() ) /1000/1000 +" M");
		
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
