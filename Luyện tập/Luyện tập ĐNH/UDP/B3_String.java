/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UDP;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author MEDIAMART PHU SON
 */
public class B3_String {
    public static void main(String[] args){
        DatagramSocket socket = null;
        
        try{
            InetAddress serverh =  InetAddress.getByName("localhost");
            int serverp= 2208;
            String msv = "B21DCCN319";
            String code = "oC4JH4si";
            String gui = ";" + msv + ";" +code  ;
            
            byte[] bytegui = gui.getBytes();
            
            DatagramPacket datagui = new DatagramPacket(bytegui, bytegui.length, serverh, serverp );
            socket.send(datagui);
            
            byte[] bytenhan = new byte[1024];
            
            DatagramPacket datanhan = new  DatagramPacket(bytenhan, bytenhan.length );
            
            socket.receive(datanhan);
            
            String chuoinhan = new String(datanhan.getData(), 0 , datanhan.getLength());
            
            String[] chuois = chuoinhan.split(";",2);
            String requestid = chuois[0];
            String data = chuois[1];
            String datach = chuanhoa(data);
            
            
            String chuoigui = requestid + ";" + datach;
            
            byte[] guicuoi = chuoigui.getBytes();
            
            DatagramPacket datacuoi = new DatagramPacket(guicuoi,guicuoi.length,serverh, serverp);
            
            socket.send(datacuoi);
            
            
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(socket!=null && !socket.isClosed()){
                socket.close();
            }
        }
    }
    
        public static String chuanhoa(String data){
            String[] tach = data.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for( String tachs : tach){
                if(tachs.length() > 0 ){
                    sb.append(Character.toUpperCase(tachs.charAt(0))).append(tachs.substring(1).toLowerCase()).append(" ");
                                     
                }
            }
            return sb.toString().trim() ;
            
            
        }
}
