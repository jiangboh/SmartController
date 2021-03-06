package com.magnet.socket;

import android.content.Context;
import android.util.Log;

import com.magnet.parse_generate_xml.ex_config.ConfigRsp;
import com.magnet.parse_generate_xml.ex_status.StatusNotif;
import com.magnet.parse_generate_xml.udp.ActionResponse;
import com.magnet.parse_generate_xml.udp.BTSOnline;
import com.magnet.socket_service.EventBusMsgDevResponse;
import com.magnet.utils.Logs;
import com.magnet.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by lenovo on 2016/12/22.
 */

public class SocketUDP {

    private boolean stopReceive = false;
    private final String TAG = "SocketUDP";
    private DatagramSocket socket;
    private Context context;

    public SocketUDP(Context context) {
        this.context = context;
        try {
            socket = new DatagramSocket(8001 + new Random().nextInt(1000));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送UDP消息
     * @param ipAddress
     * @param serverPort
     * @param msg
     */
    public void send(final String ipAddress,final int serverPort,final String msg){
        new Thread(){
            @Override
            public void run() {
                try {
//                    Log.d(TAG, "UDP send ip= " + ipAddress + ",Port=" + serverPort + ",data=" + msg);
                    byte[] buf = msg.getBytes();
                    InetAddress address = InetAddress.getByName(ipAddress);//服务器地址
                    //创建发送方的数据报信息(包的最大长度为64k)
                    DatagramPacket dataGramPacket = new DatagramPacket(buf, buf.length, address, serverPort);
                    socket.send(dataGramPacket);  //通过套接字发送数据
                } catch (UnknownHostException e) {
                    Logs.d(TAG,"发送UDP消息异常：" + e.getMessage());
                    e.printStackTrace();
                } catch (SocketException e) {
                    Logs.d(TAG,"发送UDP消息异常：" + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Logs.d(TAG,"发送UDP消息异常：" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 开始接收UDP消息
     */
    public void startReceive(){
        new Thread(){
            @Override
            public void run() {
                try {
                    byte data[] = new byte[8 * 1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    while(!stopReceive){
                        socket.receive(packet);
                        //把接收到的data转换为String字符串
                        String result = new String(packet.getData(), packet.getOffset(), packet.getLength(),"UTF-8");
                        Logs.w(TAG, "接收到的UDP数据为：" + result,"receivedUdpData",true,true);
                        ProtocolStartAndEndTag startAndEndTag = new ProtocolStartAndEndTag();
                        ArrayList<String> headers = startAndEndTag.getUdpHeaders();
                        HashMap<String,String> correspondEnds = startAndEndTag.getCorrespondEnd();
                        for(String header : headers){
                            String endTag = correspondEnds.get(header);
                            //Log.d(TAG,"header == " + header + "endTag == " + endTag);
                            if(result.contains(header)&&result.contains(endTag)){
                                Log.d(TAG,"UDP数据：" + result);
                                parseXml(result,packet.getAddress().getHostAddress(), packet.getPort());
                            }else{
//                                Log.d(TAG,"UDP数据不完整：" + result);
                            }
                        }
                    }
                    socket.close();
                    socket = null;
                } catch (SocketException e) {
                    e.printStackTrace();
                    Logs.d(TAG,"接收UDP消息异常：" + e.getMessage());
                } catch (IOException e) {
                    Logs.d(TAG,"接收UDP消息异常：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     *@author Jack.liao
     *@name stopReceive
     *@return void
     **/
    public void stopReceive(){
        stopReceive = true;
    }

    private void parseXml(String str,String ipAddress, int iPort){
        //Logs.w(TAG,"解析前的UDP数据为：" + str);
        if(str.startsWith("<bts-online")){
            Logs.d("<bts-online","BTSOnline返回数据为：" + str);
            BTSOnline bs = BTSOnline.xmlToBean(str);
            Logs.d("<bts-online","BTSOnline解析后的数据为：" + bs.toString());
            EventBus.getDefault().post(bs);
        /*}else if(str.startsWith("<register-client")){
            //TODO
        }else if(str.startsWith("<set-config")){
            //TODO
        }else if(str.startsWith("<unregister-client")){
            //TODO*/
        }else if(str.startsWith("<action-response")){
            ActionResponse ar = ActionResponse.xmlToBean(str);
            Logs.d(TAG,ar.toString()+"   actionStatus == " + ar.getActionStatus());
            if("register-client".equals(ar.getActionType())){
                SharePreferenceUtils.getInstance(context).setString("registe",ar.getActionStatus());
            }else if("unregister-client".equals(ar.getActionType())){
//                EventBus.getDefault().post(new EventBusMsgStopTCPSocket(ipAddress));
            }
            EventBus.getDefault().post(ar);
        }else if(str.startsWith("<ex-config-rsp")){
            ConfigRsp cr = ConfigRsp.xmlToBean(str);
            Logs.d(TAG,"解析出ex-config-rsp数据：" + cr.toString());
            EventBus.getDefault().post(cr);
        } else if(str.startsWith("<ex-status-notif")){
            StatusNotif sn = StatusNotif.xmlToBean(str);
            Logs.d(TAG,"解析出ex-status-notif数据：" + sn.toString());
            EventBus.getDefault().post(sn);
        }
    }
}
