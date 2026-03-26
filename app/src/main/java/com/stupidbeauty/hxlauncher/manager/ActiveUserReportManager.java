package com.stupidbeauty.hxlauncher.manager;

import com.stupidbeauty.rotatingactiveuser.RotatingActiveUserClient; // RotatingActiveUserClient
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.stupidbeauty.shutdownat2100androidnative.ShutDownAt2100Application;

public class ActiveUserReportManager
{
  private RotatingActiveUserClient rotatingActiveUserClient=null; //!< 滚动活跃用户统计客户端。陈欣。
    
  /**
  * 开始报告活跃用户。
  */
  public void startReportActiveUser() 
  {
    if (rotatingActiveUserClient==null)
    {
      Context context=ShutDownAt2100Application.getAppContext(); // 获取应用程序上下文。
      rotatingActiveUserClient=new RotatingActiveUserClient(context); // 创建客户端对象。
    } //if (rotatingActiveUserClient==null)
    
    rotatingActiveUserClient.reportActiveUser(); // 报告活跃用户。
  } //public void StartReportActiveUser()
}
