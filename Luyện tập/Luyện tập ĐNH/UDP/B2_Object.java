/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UDP;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import java.io.Serializable;

/**
 *
 * @author MEDIAMART PHU SON
 */
class Product implements Serializable{
    
    private static final long serialVersionUID = 20161107; 
    private String id;
    private String code;
    private String name;
    private int quantity;
    
    public Product(){}
    
    public Product(String id, String code, String name, int quantity){
       this.id = id;
       this.code = code;
       this.name = name;
       this.quantity = quantity;
    }
    
    public String getId(){
        return id;
    }
    
    public String getCode(){
        return code;
    }
    public String getName(){
    
     return name;
    }
    
    public void setName(String Name){
       name = Name;
    }
    
    
    public int getQuantity(){
        return quantity;
    }
    
    public void setQuantity(int SL){
       quantity = SL;
    }
//     ghi đè 1 lớp để in dữ liệu 
    
     @Override
    public String toString() {
        return "Product{id='" + id + "', code='" + code + "', name='" + name + "', quantity=" + quantity + "}";
    }
}

/**
 *
 * @author MEDIAMART PHU SON
 */
public class B2_Object {
        
        
        public static void main(String[] args){
            
            DatagramSocket socket = null;
            try{
                
            socket = new DatagramSocket();
//            Địa chỉ port được đưa về dạng InetAddress;
            InetAddress serverHost = InetAddress.getByName("localhost");
            
            int serverPort = 2209;
//            địa chỉ port
            String msv = "B21DCCN319";
            String code = "tjedvqT8";
            String mess = ";" + msv +";" + code;
//           tạo một mess 
           byte[] sendData = mess.getBytes();
//  đưa mess về dạng byte
// tạo DatagramPacket để đóng gói toàn bộ dữ liệu cần gửi đến Server
           DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverHost, serverPort);
//  gửi thống điệp đến Server          
           socket.send(sendPacket);

// nhận thông điệp từ Server

//           tạo một mảng byte để nhận
            byte[] nhan = new byte[1024];
            
//            tạo gói dữ liệu gồm mảng dữ liệu vừa tạo và độ dài

            DatagramPacket nhangoi = new DatagramPacket(nhan, nhan.length);
            
            socket.receive(nhangoi);  // nhận được gói gì bỏ vào gói đấy 
//            giải mã thông điệp nhận 
            

           ByteArrayInputStream bytestream = new ByteArrayInputStream(nhangoi.getData()); // lấy dữ liệu byte từ nhận
           DataInputStream datastream = new DataInputStream(bytestream); // chuyển về dạng dữ liệu
           byte[] requestid = new byte[8];
           
           datastream.read(requestid); // tạo mảng byte và đọc dữ liệu ký tự vào mảng 
           
//            nhận dũ liệu đối tượng từ mảng
        
           ObjectInputStream sp = new ObjectInputStream(bytestream);
           Product pro = (Product)sp.readObject(); // đọc đối tượng dưới kiểu dữ liệu Product
           System.out.println("Server đã gửi đối tượng: "+ pro);
           
//           Xử lý dữ liệu: Sửa lại thông tin
           pro.setName( chuanhoaten(pro.getName()));
           
           pro.setQuantity(chuanhoasl(pro.getQuantity()));
           
//           gửi thông tin cho Server bằng cách gom hết vào Byte
           
           ByteArrayOutputStream byteout = new ByteArrayOutputStream();
           DataOutputStream data = new DataOutputStream(byteout);
           
           data.write(requestid);
           
           ObjectOutputStream Pro = new ObjectOutputStream(byteout);
           
           Pro.writeObject(pro);
           
//           đóng gói thông tin
          byte[] sendbyte = byteout.toByteArray();
          
          
          DatagramPacket packet = new DatagramPacket(sendbyte, sendbyte.length, serverHost,serverPort);
          
          socket.send(packet);           
            }catch( Exception e){
              e.printStackTrace();
            }finally{
                if(socket != null && !socket.isClosed()){
                    socket.close();
                }
            }



    }
        
        
    public static String chuanhoaten(String ten){
        String[] vanban = ten.split(" ");
        if(vanban.length < 2){ return ten ; }
 
        String tam = vanban[0];
        vanban[0] = vanban[vanban.length -1];
        vanban[vanban.length -1] = tam;
        
        return String.join(" ",vanban);
    }
    
    public static int chuanhoasl(int sl){
        String SL = String.valueOf(sl);
        String SLM = new StringBuilder(SL).reverse().toString();
        return Integer.parseInt(SLM); 
    }
    
}
