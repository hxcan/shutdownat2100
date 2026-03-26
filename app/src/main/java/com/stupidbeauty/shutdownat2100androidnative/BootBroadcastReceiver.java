package com.stupidbeauty.shutdownat2100androidnative;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机结束广播接收器。
 * @author root 蔡火胜。
 *
 */
@SuppressWarnings("DanglingJavadoc")
public class BootBroadcastReceiver extends BroadcastReceiver
{
  @Override
  /**
  * 接收到广播。
  */
  public void onReceive(Context context, Intent intent) 
  {
    if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
    {
//       Intent bootActivityIntent=new Intent(context,TimeCheckService.class); //创建意图对象。
//       context.startService(bootActivityIntent); //启动服务。

      AlarmScheduler alarmSchedular=new AlarmScheduler(); // Create an alarm scheduler.
      
      alarmSchedular.schduleAlarm(); // Schecule alarm.
    } //if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
  } //public void onReceive(Context context, Intent intent)
} //public class BootBroadcastReceiver extends BroadcastReceiver
