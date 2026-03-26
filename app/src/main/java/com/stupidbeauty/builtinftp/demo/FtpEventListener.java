package com.stupidbeauty.builtinftp.demo;

import com.stupidbeauty.shutdownat2100androidnative.ShutDownAt2100Application;
import com.stupidbeauty.ftpserver.lib.EventListener;
import android.util.Log;
import java.util.Date;    
import java.time.format.DateTimeFormatter;
import java.io.File;
import com.koushikdutta.async.AsyncServerSocket;

public class FtpEventListener implements EventListener
{
  private static final String TAG="FtpEventListener"; //!< 输出调试信息时使用的标记
  private ShutDownAt2100Application launcherActivity=null; //!< 启动活动。

  @Override
  public void onEvent(String eventCode)
  {
  } // public void onEvent(String eventCode)
    
  @Override
  public void onEvent(String eventCode, Object eventContent)
  {
    Log.d(TAG, "onEvent, eventCode: " + eventCode);
    
    if (eventCode.equals(UPLOAD_FINISH)) // file upload finish
    {
      if (eventContent!=null) // The event content eists
      {
        launcherActivity.notifyUploadFinish(eventContent); // notify upload finish.
      } // if (eventContent!=null) // The event content eists
    } // else if (eventCode.equals(UP_FINISH)) // file upload finish
  } // public void onEvent(String eventCode)
    
  public FtpEventListener(ShutDownAt2100Application launcherActivity)
  {
    this.launcherActivity=launcherActivity;
  }
}

