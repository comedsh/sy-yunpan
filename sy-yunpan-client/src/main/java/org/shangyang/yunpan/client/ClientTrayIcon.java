package org.shangyang.yunpan.client;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class ClientTrayIcon {
	
	static Logger logger = Logger.getLogger(ClientTrayIcon.class);
	
	// the path for the eclipse env
	final static String RELATIVEPATH1 = System.getProperty("user.dir") + "/src/main/resources/icons/";
	
	// the path for the mac app @See Client#fixpath(). 
	final static String RELATIVEPATH2 = System.getProperty("user.dir") + "/Java/icons/";
	
	Timer timer;
	
	// the path for the windows app
	
	TrayIcon trayIcon;
	
	static ClientTrayIcon clientTrayIcon;
	
	public static ClientTrayIcon getInstance(){
		
		if( clientTrayIcon == null ){
			
			clientTrayIcon = new ClientTrayIcon();
			
		}
		
		return clientTrayIcon;
		
	}
	
	
	public void init() throws Exception{
		
		PopupMenu popupMenu = new PopupMenu();
		
		MenuItem menuItem = new MenuItem();
		
		popupMenu.setLabel("PopupMenu");
		
		menuItem.setLabel("退出");
		
		menuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				
				System.exit(0);
				
				logger.debug("the system gets existed via commands of the user");
				
			}
			
		});
		
		popupMenu.add(menuItem);		
		
		SystemTray systemTray = SystemTray.getSystemTray();
		
		BufferedImage image = ImageIO.read( new File( getIconPath("trayicon.tiff") ) );
		
		trayIcon = new TrayIcon(image);
		
		trayIcon.setImageAutoSize(true);
		
		trayIcon.setToolTip("微云云盘");
		
		trayIcon.setPopupMenu( popupMenu );
		
		systemTray.add( trayIcon );
		
	}
	
	public Timer rotate(){
		
		timer = new Timer();
		
		timer.schedule(new TimerTask() {
        	
			int IMAGE_IDX = 0;
			
            public void run() {
            	
            	try {
		
					BufferedImage image = ImageIO.read( new File( getIconPath("rotate-0" + (++IMAGE_IDX) + ".tiff") ) );
					
					trayIcon.setImage( image );
					
					if( IMAGE_IDX == 4 ){
						IMAGE_IDX = 0;
					}
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
            	
            }  
            
        }, 1, 250);		
		
		return timer;
	}
	
	public void stop(){
		
		timer.cancel();
		
		timer = null;
		
		BufferedImage image=null;
		
		try {
			
			image = ImageIO.read( new File( getIconPath("trayicon.tiff") ) );
			
		} catch (IOException e) {
			
			logger.debug(e.getMessage(), e);
			
			throw new RuntimeException(e);
		}
		
		trayIcon.setImage(image);
		
	}
	
	public void error(){
		
		timer.cancel();
		
		timer = null;
		
		BufferedImage image=null;
		
		try {
			
			image = ImageIO.read( new File( getIconPath("trayicon-error.tiff") ) );
			
		} catch (IOException e) {
			
			logger.debug(e.getMessage(), e);
			
			throw new RuntimeException(e);
		}
		
		trayIcon.setImage(image);
		
	}	
	
	String getIconPath(String path){
		
		String iconpath = RELATIVEPATH1 + path;
		
		logger.debug("icon path:" + iconpath);
		
		if( !new File(iconpath).exists() ){
			
			iconpath = RELATIVEPATH2 + path;
			logger.debug("icon path:" + iconpath);
		}
		
		return iconpath;
		
	}
	
	
}
