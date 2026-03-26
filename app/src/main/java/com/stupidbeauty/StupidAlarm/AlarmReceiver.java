package com.stupidbeauty.StupidAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.stupidbeauty.shutdownat2100androidnative.TimeCheckService;
import com.stupidbeauty.shutdownat2100androidnative.FullscreenActivity;
import android.util.Log;
import android.view.Window;
import android.widget.TimePicker;
import android.widget.Toast;
import com.stupidbeauty.lzc.floatingwindowdemo.service.FloatingService;

public class AlarmReceiver extends BroadcastReceiver 
{
  private static final String TAG="AlarmReceiver"; //!<输出调试信息时使用的标记。

  /* (non-Javadoc)
  * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
  */
  @Override
  public void onReceive(Context context, Intent data) 
  {
    Log.d(TAG, "onReceive, time: "); //Debug.
    
//     FullscreenActivity
    
//     Intent intent= new Intent(context.getApplicationContext(), TimeCheckService.class);
//     context.startService(intent);
    
    Intent intent= new Intent(context.getApplicationContext(), FullscreenActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  } //public void onReceive(Context context, Intent data)
}
