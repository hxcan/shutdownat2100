package com.stupidbeauty.lzc.floatingwindowdemo.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.stupidbeauty.lzc.floatingwindowdemo.MyWindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 类描述：悬浮窗Service
 * 创建时间： 2017/2/13 11:37
 */
public class FloatingService extends Service
{
    private static String TAG="FloatingService"; //!<输出调试信息时使用的标记

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler handler = new Handler();

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
     */
    private Timer timer;

    /**
     * 开始推流时间戳
     */
    private long startTime;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTime = System.currentTimeMillis();
        // 开启定时器，每隔1秒刷新一次
        if (timer == null) {
            timer = new Timer();

            Log.d(TAG, "onStartCommand, timer: " + timer); //Debug.

            timer.scheduleAtFixedRate(new RefreshTask(), 0, 4000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() 
    {
      super.onDestroy();
      // Service被终止的同时也停止定时器继续运行
      
      if (timer!=null) // The timer exists
      {
        timer.cancel();
        timer = null;
      } // if (timer!=null) // The timer exists
    } // public void onDestroy() 

    class RefreshTask extends TimerTask
    {
      private String TAG="RefreshTask"; //!< The tag used for debug code.

      @Override
      public void run()
      {
        Log.d(TAG, "run, timer: " + timer); //Debug.
        
        // boolean shouldShutDown=shutDownAt2100Manager.checkShutDownTime(); // Check shut down time.

        // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
        if (!MyWindowManager.isWindowShowing()) // The window is not showing.
        {
          Log.d(TAG, "run, isHome, timer: " + timer); //Debug.
          
          
          
          handler.post(new Runnable()
          {
            @Override
            public void run() 
            {
              MyWindowManager.createSmallWindow(getApplicationContext());
            }
          });
        } // if (!MyWindowManager.isWindowShowing()) // The window is not showing.
      }
    }
}
