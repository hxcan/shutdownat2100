package com.stupidbeauty.shutdownat2100androidnative;

import com.stupidbeauty.shutdownat2100.helper.ShutDownAt2100Manager;
import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import com.stupidbeauty.ftpserver.lib.EventListener;
import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import java.util.Random;
import com.stupidbeauty.builtinftp.demo.FtpEventListener;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import com.stupidbeauty.builtinftp.BuiltinFtpServer;
import com.stupidbeauty.hxlauncher.listener.BuiltinFtpServerErrorListener; 
import android.os.Process;
import java.util.TimerTask;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import java.util.Timer;
import java.util.TimerTask;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

/**
 * 应用程序对象。
 * @author root 蔡火胜。
 *
 */
public class ShutDownAt2100Application extends Application
{
	private static Context mContext;
  private BuiltinFtpServer builtinFtpServer=null; //!< The builtin ftp server.
  private BuiltinFtpServerErrorListener builtinFtpServerErrorListener=null; //!< the builtin ftp server error listener.  Chen xin.
  private ShutDownAt2100Manager shutDownAt2100Manager= null; //!< Shutdown at 2100 manager.
	
  /**
  * 计划启动内置 FTP 服务器。
  */
  private void scheduleStartBuiltinFtpServer() 
  {
    Timer timerObj = new Timer();
    TimerTask timerTaskObj = new TimerTask() 
    {
      public void run() 
      {
        startBultinFtpServer(); // 启动内置 FTP 服务器。
        
        initializeEventListener(); // 初始化事件监听器。
      }
    };
    timerObj.schedule(timerTaskObj, 15000); // 延时启动。
  } //private void scheduleStartBuiltinFtpServer()

    /**
    * Notify upload finish.
    */
    public void notifyUploadFinish(Object eventContent)
    {
      // chen xin . notify upload finish

      debugWriteShutDownPreference(); // Debug write shut down prefernce.
    } // notifyDownloadFinish(); // 告知文件下载完毕。
    
    /**
    * Debug write shut down prefernce.
    */
    private void debugWriteShutDownPreference()
    {
      // Chen xin.
      int clock_time = 21;
      int ClkMnt=30;

      if ((clock_time>=21) && (ClkMnt>=30)) //超过了21:30
      {
        clock_time=21;
        ClkMnt=30;
      } //if ((clock_time>=21) && (ClkMnt>=30)) //超过了21:30

      //Remember shutdown time:
      PreferenceManagerUtil.setShutdownHour(clock_time, this);
      PreferenceManagerUtil.setShutdownMinute(ClkMnt, this);
      
      shutDownAt2100Manager.writeShutDownTimeToExternalStorage(clock_time, ClkMnt); // Write the shut down time to external storage.
    } // private void debugWriteShutDownPreference()
    
    /**
    * 初始化事件监听器。
    */
    private void initializeEventListener()
    {
      EventListener eventListener=new FtpEventListener(this); // 创建事件监听器。
        
      builtinFtpServer.setEventListener(eventListener); // 设置事件监听器。
    } //private void initializeEventListener()
    
    /**
    * Choose a port
    */
    private int chooseRandomPort() 
    {
      int randomIndex=10101; // Choose a port.

      return randomIndex;
    } //private int chooseRandomPort()

    /**
    * 启动内置 FTP 服务器。
    */
    private void startBultinFtpServer()
    {
      builtinFtpServer=new BuiltinFtpServer(this); //!< The builtin ftp server.
      builtinFtpServerErrorListener=new BuiltinFtpServerErrorListener(); // Create the builtin ftp server error listener. 
      int actualPort=chooseRandomPort(); // 选择随机端口。
      builtinFtpServer.setPort(actualPort); // 设置自动选择FTP监听端口。
      builtinFtpServer.setAllowActiveMode(false); // 设置不允许主动模式。
      builtinFtpServer.setErrorListener(builtinFtpServerErrorListener); // Set the error listener. Chen xin.
      builtinFtpServer.start(); //启动服务器。
      
      
      
    } //        startBultinFtpServer(); // 启动内置 FTP 服务器。
    
	@Override
	/**
	 * 程序被创建。
	 */
	public void onCreate() 
	{
		super.onCreate(); //创建超类。
		mContext = getApplicationContext(); //获取应用程序上下文。

    scheduleStartBuiltinFtpServer(); // 计划启动内置 FTP 服务器。
    shutDownAt2100Manager=new ShutDownAt2100Manager(this);
	} //public void onCreate()

	/**
	 * 获取应用程序上下文。
	 * @return 应用程序上下文。
	 */
	public static Context getAppContext() 
	{ 
		return mContext; 
	}  //public static Context getAppContext()
} //public class ShutDownAt2100Application extends Application

