package org.shangyang.yunpan;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.junit.Ignore;
import org.junit.Test;

public class TrayIconTest {
	
	/**
	 * the first touch
	 * @throws AWTException 
	 * @throws InterruptedException 
	 */
	@Test
	@Ignore
	public void test1() throws AWTException, InterruptedException{
		
		final JFrame frame = new JFrame();
		
		frame.setLocation(200, 200);
		
		frame.setSize(300, 200);
		
		frame.setVisible(true);
		
		frame.addWindowListener( new WindowAdapter() {
			
			public void windowIconified( WindowEvent evt ) {
				
				frame.setVisible(false);
				
			}
			
		});

		PopupMenu popupMenu1 = new PopupMenu();
		
		MenuItem menuItem1 = new MenuItem();
		
		popupMenu1.setLabel("PopupMenu");
		
		menuItem1.setLabel("打开");
		
		menuItem1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				
				frame.setVisible(true);
				
			}
			
		});
		
		popupMenu1.add(menuItem1);
		
		// 判断当前平台是否支持系统托盘
		if ( SystemTray.isSupported() ) {
			
			SystemTray st = SystemTray.getSystemTray();
			
			Image image = Toolkit.getDefaultToolkit().getImage( System.getProperty("user.dir") + "/src/test/resources/icons/ShowTime.png" );// 定义托盘图标的图片
			
			TrayIcon ti = new TrayIcon( image );
			
			ti.setToolTip("test ");
			
			ti.setPopupMenu( popupMenu1 );
			
			st.add( ti );
			
		}	
		
		TimeUnit.SECONDS.sleep(60);
		
	}
	
	static int IMAGE_IDX = 0;
	
	/**
	 * the dynamic tray icons
	 * @throws InterruptedException 
	 * @throws AWTException 
	 * @throws IOException 
	 */
	@Test
	@Ignore
	public void test2() throws InterruptedException, AWTException, IOException{

		PopupMenu popupMenu1 = new PopupMenu();
		
		MenuItem menuItem1 = new MenuItem();
		
		popupMenu1.setLabel("PopupMenu");
		
		menuItem1.setLabel("关闭");
		
		menuItem1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				// adding exit code there.
			}
			
		});
		
		popupMenu1.add(menuItem1);
		
		// 判断当前平台是否支持系统托盘
		if( !SystemTray.isSupported() ) return;
			
		SystemTray systemTray = SystemTray.getSystemTray();
		
		BufferedImage image = ImageIO.read( new File( System.getProperty("user.dir") + "/src/test/resources/icons/trayicon.tiff") );
		
		final TrayIcon trayIcon = new TrayIcon(image);
		
		trayIcon.setImageAutoSize(true);
		
		trayIcon.setToolTip("test");
		
		trayIcon.setPopupMenu( popupMenu1 );
		
		systemTray.add( trayIcon );
		
		TimeUnit.SECONDS.sleep(10);
		
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask() {
        	
            public void run() {
            	
            	try {
            		
            		String ipath = System.getProperty("user.dir") + "/src/test/resources/icons/rotate-0" + (++IMAGE_IDX) + ".tiff";
					
					System.out.println(ipath);    		
		
					BufferedImage image = ImageIO.read( new File( ipath ) );
					
					trayIcon.setImage( image );
					
					if( IMAGE_IDX == 4 ){
						IMAGE_IDX = 0;
					}
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
            	
            }  
            
        }, 1, 250);
		
		TimeUnit.SECONDS.sleep(10);
		
		trayIcon.setImage(image);
		
		timer.cancel();
		
		TimeUnit.SECONDS.sleep(40);
		
	}

}
