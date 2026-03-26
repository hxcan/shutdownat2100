package com.stupidbeauty.reneweb.androidasyncsocketexamples.udp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.stupidbeauty.lanime.Constants;
import com.stupidbeauty.shutdownat2100androidnative.ShutDownAt2100Application;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server 
{
  // private final InetSocketAddress host;
  private InetAddress host;
  private final int port;
  private AsyncDatagramSocket asyncDatagramSocket;

  public Server(String host, int port) 
  {
    // this.host = new InetAddress(host);
    
    try
    {
      this.host = InetAddress.getByName(host);
    }
    catch(UnknownHostException e)
    {
      e.printStackTrace();
    }
    
    this.port=port;
    setup();
  }

    /**
     * 通知，收到了消息。
     * @param bb 消息字节数组列表。
     */
    private void notifyMessageReceived(InetSocketAddress remoteAddress, ByteBufferList bb)
    {
        String remoteIp=remoteAddress.getHostName(); //获取远端的IP。

        //发送广播，让界面更新：
        Intent intent4Wrk = new Intent(); //创建意图对象。晚安诸位。
        Bundle extras=new Bundle(); //创建数据包。
        extras.putString("remoteIp", remoteIp); //加入文本内容。
//		extras.put

        byte[] messageContent=bb.getAllByteArray(); //获取全部数据。

        extras.putByteArray("messageContent", messageContent); //加入消息内容。

        intent4Wrk.putExtras(extras); //设置附加数据。

        intent4Wrk.setAction(Constants.Operation.ReportMessage);// action与接收器相同

        Context appContext=ShutDownAt2100Application.getAppContext(); //获取应用程序上下文。
        LocalBroadcastManager lclBrdcstMngr=LocalBroadcastManager.getInstance(appContext); //Get the local broadcast manger instance.

        lclBrdcstMngr.sendBroadcast(intent4Wrk); //发送广播。

    } //private void notifyMessageReceived(DataEmitter emitter, ByteBufferList bb)

    private void setup() 
    {
      // try 
      {
        asyncDatagramSocket = AsyncServer.getDefault().openDatagram(host, port, true);
      } 

        asyncDatagramSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                InetSocketAddress remoteAddress=asyncDatagramSocket.getRemoteAddress(); //获取远端的地址。

                notifyMessageReceived(remoteAddress, bb); //通知，收到了消息。
            }
        });

        asyncDatagramSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully closed connection");
            }
        });

        asyncDatagramSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully end connection");
            }
        });
    }
}
