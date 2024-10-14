
package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 *
 * @author Ngọ Văn Trọng
 */
public class B4_DataType {
    public static void main(String[] args){
        DatagramSocket socket = null;
        try{
           InetAddress svh = InetAddress.getByName("203.162.10.109");
           int svp = 2207;
           
           String msv = "B21DCCN726";
           String code = "RGPzy9vA";
           
           String mess = ";" + msv + ";" + code;
           
           byte[] bytegui = mess.getBytes();
           
           DatagramPacket datagui= new  DatagramPacket(bytegui, bytegui.length, svh,svp);
           
           socket.send(datagui);
           
           byte[]  bytenhan = new byte[1024];
           
           DatagramPacket datanhan = new DatagramPacket(bytenhan, bytenhan.length);
           
           socket.receive(datanhan);
           
           String chuoinhan = new String (datanhan.getData(), 0 , datanhan.getLength());
           
           String[] chuois = chuoinhan.split(";",2);
           String requestid = chuois[0];
           String data = chuois[1];
           String[] numbers = chuois[1].split(",");
           int[] number = Arrays.stream(numbers).mapToInt(Integer::parseInt).toArray();
           
           int max = timmax(number);
           int min = timmin(number);
           
           String messgui = requestid + ";" + max + "," + min ;
           
           byte[] responseBuffer = messgui.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, svh, svp);
            socket.send(responsePacket); 
           
         
        }catch(Exception e){
        
        }
            
        
            
    }
    public static int timmax(int[] number){
        int firstMax = Integer.MIN_VALUE;
        int secondMax = Integer.MIN_VALUE;
        for (int num : number) {
            if (num > firstMax) {
                secondMax = firstMax;
                firstMax = num;
            } else if (num > secondMax && num != firstMax) {
                secondMax = num;
            }
        }
        return secondMax;
    }
    
    public static int timmin(int[] number){
        int firstMin = Integer.MAX_VALUE;
        int secondMin = Integer.MAX_VALUE;
        for (int num : number) {
            if (num < firstMin) {
                secondMin = firstMin;
                firstMin = num;
            } else if (num < secondMin && num != firstMin) {
                secondMin = num;
            }
        }
        return secondMin;
    }
}
